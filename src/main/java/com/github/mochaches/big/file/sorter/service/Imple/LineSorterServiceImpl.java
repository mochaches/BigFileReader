package com.github.mochaches.big.file.sorter.service.Imple;

import com.github.mochaches.big.file.sorter.config.ApplicationConfig;
import com.github.mochaches.big.file.sorter.service.FileService;
import com.github.mochaches.big.file.sorter.service.LineGeneratorService;
import com.github.mochaches.big.file.sorter.service.LineSorterService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineSorterServiceImpl implements LineSorterService {
    final FileService fileService;
    final LineGeneratorService lineGeneratorService;
    final ApplicationConfig applicationConfig;

    public void fileGenerateAndSorter() {
        String sourceFile = applicationConfig.getSourceFile();
        File file = fileService.createFile(sourceFile);
        IntStream.range(0, applicationConfig.getAmountLine()).forEach(e -> fileService.writeToFile(sourceFile,
                lineGeneratorService.generate(applicationConfig.getMaxLineLength()),
                true));
        sortFile(file);
    }

    @Override
    @SneakyThrows
    public void sortFile(File file) {
        var passedLetters = new HashMap<String, Integer>();
        var lineLimit = applicationConfig.getLineLimit();
        var amountLine = applicationConfig.getAmountLine();
        var sizePartLine = applicationConfig.getSortSize();
        var pathToTheFile = file.getAbsolutePath();
        if (amountLine < lineLimit) {
            log.info("Переданный файл меньше, чем '{}'. Можно отсортировать", lineLimit);
            sortLineToFile(pathToTheFile);
        } else {
            log.info("Переданный файл превышает допустимый размер '{}'. Переходим к разделению файла", lineLimit);
            fileSeparator(pathToTheFile, passedLetters, sizePartLine);
        }
        log.info("Добиваемся чтобы все файлы были допустимого размера");
        while (true) {
            sizePartLine = sizePartLine + 2;
            var counter = passedLetters.keySet().size();
            for (Map.Entry<String, Integer> partLine : passedLetters.entrySet()) {
                if (partLine.getValue() > lineLimit) {
                    var fileName = partLine.getKey().toLowerCase(Locale.ROOT);
                    fileSeparator(applicationConfig.getHelpersFilePath() + fileName, passedLetters, sizePartLine);
                } else {
                    counter--;
                }
            }
            if (counter == 0) {
                log.info("Файл разделен, новые файлы созданы");
                break;
            }
        }

        log.info("Сортируем строки в мелких файлах");
        passedLetters.keySet().forEach(s ->
                sortLineToFile(applicationConfig.getHelpersFilePath() + s + ".txt"));

        log.info("Создаем итоговый файл");
        fileService.createFile(pathToTheFile);
        log.info("Сортируем файлы и помещаем в итоговый файл строки");
        passedLetters.keySet().stream().sorted().forEach(e -> {
            writeFromFileToFile(pathToTheFile, e);
            fileService.remove(applicationConfig.getHelpersFilePath() + e + ".txt");
        });
    }

    @SneakyThrows
    private void writeFromFileToFile(String pathFinalFile, String pathFileSource) {
        log.debug("Заполняем итоговый файл");
        var partFileName = applicationConfig.getHelpersFilePath() + pathFileSource + ".txt";
        try (var fileReader = new FileReader(partFileName);
             var reader = new BufferedReader(fileReader)) {
            reader.lines().forEach(l -> fileService.writeToFile(pathFinalFile, l, true));
        }
    }

    @SneakyThrows
    private void sortLineToFile(String pathToFile) {
        List<String> listForSort;
        log.debug("Считываем файл для сортировки");
        try (var fileReader = new FileReader(pathToFile);
             var reader = new BufferedReader(fileReader)) {
            listForSort = reader.lines().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
        }
        var length = pathToFile.split("/").length;
        var fileName = pathToFile.split("/")[length - 1];
        fileService.writeToFile(applicationConfig.getHelpersFilePath() + fileName, listForSort.get(0), false);
        for (int i = 1; i < listForSort.size(); i++) {
            fileService.writeToFile(applicationConfig.getHelpersFilePath() + fileName, listForSort.get(i), true);
        }
    }

    @SneakyThrows
    private void fileSeparator(String pathToFile, Map<String, Integer> lettersPassed, int sizePartLine) {
        log.info("Разделяем файл на более мелкие части");
        try (var fileReader = new FileReader(pathToFile);
             var reader = new BufferedReader(fileReader)) {
            log.info("Сравниваем начало строк");
            String line;
            while ((line = reader.readLine()) != null) {
                String helpersFilePath = applicationConfig.getHelpersFilePath();
                if (line.length() - 1 < sizePartLine) {
                    lettersPassed.put(line.toLowerCase(Locale.ROOT), +1);
                    String fileName = line.toLowerCase(Locale.ROOT) + ".txt";
                    fileService.createFile(helpersFilePath + fileName);
                    fileService.writeToFile(helpersFilePath + fileName, line, true);
                    continue;
                }
                String startLine = line.substring(0, sizePartLine);
                if (lettersPassed.containsKey(startLine.toLowerCase(Locale.ROOT))) {
                    log.debug("Добавили часть строки '{}' в файл", startLine);
                    fileService.writeToFile(helpersFilePath + startLine.toLowerCase(Locale.ROOT) + ".txt",
                            line,
                            true);
                    lettersPassed.put(startLine.toLowerCase(Locale.ROOT),
                            (lettersPassed.get(startLine.toLowerCase(Locale.ROOT))) + 1);
                } else {
                    log.debug("Обновили список уникальных строк на '{}'", startLine);
                    lettersPassed.put(startLine.toLowerCase(Locale.ROOT), 1);
                    startLine = startLine.toLowerCase(Locale.ROOT) + ".txt";
                    log.debug("Создаем новый файл с именем '{}'", startLine);
                    fileService.createFile(helpersFilePath + startLine);
                    log.debug("Записываем строку в новый файл");
                    fileService.writeToFile(helpersFilePath + startLine, line, true);
                }
            }
            log.info("Удаляем исходный файл");
            fileService.remove(pathToFile);
        }
    }
}

