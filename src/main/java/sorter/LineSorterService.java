package sorter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineSorterService {
    static final String PATH_TO_HELPERS = "./src/main/resources/helpers/";

    private final FileService fileService;

    @SneakyThrows
    public void sortLinesInFile(String pathToTheFile, int sizePartLine, int lineLimit) {
        var passedLetters = new HashMap<String, Integer>();
        if (checkSizeFile(pathToTheFile, lineLimit)) {
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
                    fileSeparator(PATH_TO_HELPERS + fileName, passedLetters, sizePartLine);
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
        for (var fileName : passedLetters.keySet()) {
            sortLineToFile(PATH_TO_HELPERS + fileName + ".txt");
        }
        log.info("Создаем итоговый файл");
        fileService.createNewFile(pathToTheFile);
        log.info("Сортируем файлы и помещаем в итоговый файл строки");
        passedLetters.keySet().stream().sorted().forEach(e -> formationFinalFile(pathToTheFile, e));
    }

    /**
     * Переносит строки из одного файла в другой
     *
     * @param pathFinalFile основной файл для записи
     * @param pathPartFile  файл для чтения и переноса строк
     */
    @SneakyThrows
    private void formationFinalFile(String pathFinalFile, String pathPartFile) {
        log.debug("Заполняем итоговый файл");
        var partFileName = PATH_TO_HELPERS + pathPartFile + ".txt";
        try (var fileReader = new FileReader(partFileName);
             var writer = new FileWriter(pathFinalFile, true);
             var reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }
            Files.deleteIfExists(Paths.get(partFileName));
        }
    }

    /**
     * Разделяет тексовый файл на файлы поменьше(группирует по символам)
     *
     * @param pathToFile    путь до файла
     * @param lettersPassed список уникальных символов(начало строк)
     * @param sizePartLine  часть строки по которой будет проводиться поиск
     */
    @SneakyThrows
    private void fileSeparator(String pathToFile, Map<String, Integer> lettersPassed, int sizePartLine) {
        log.info("Разделяем файл на более мелкие части");
        try (var fileReader = new FileReader(pathToFile);
             var reader = new BufferedReader(fileReader)) {
            log.info("Сравниваем начало строк");
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() - 1 < sizePartLine) {
                    lettersPassed.put(line.toLowerCase(Locale.ROOT), +1);
                    String fileName = line.toLowerCase(Locale.ROOT) + ".txt";
                    fileService.createNewFile(PATH_TO_HELPERS + fileName);
                    fileService.writeToFile(PATH_TO_HELPERS + fileName, line, true);
                    continue;
                }
                String startLine = line.substring(0, sizePartLine);
                if (lettersPassed.containsKey(startLine.toLowerCase(Locale.ROOT))) {
                    log.debug("Добавили часть строки '{}' в файл", startLine);
                    fileService.writeToFile(PATH_TO_HELPERS + startLine.toLowerCase(Locale.ROOT) + ".txt",
                            line,
                            true);
                    lettersPassed.put(startLine.toLowerCase(Locale.ROOT),
                            (lettersPassed.get(startLine.toLowerCase(Locale.ROOT))) + 1);
                } else {
                    log.debug("Обновили список уникальных строк на '{}'", startLine);
                    lettersPassed.put(startLine.toLowerCase(Locale.ROOT), 1);
                    startLine = startLine.toLowerCase(Locale.ROOT) + ".txt";
                    log.debug("Создаем новый файл с именем '{}'", startLine);
                    fileService.createNewFile(PATH_TO_HELPERS + startLine);
                    log.debug("Записываем строку в новый файл");
                    fileService.writeToFile(PATH_TO_HELPERS + startLine, line, true);
                }
            }
            log.info("Удаляем исходный файл");
            Files.deleteIfExists(Paths.get(pathToFile));
        }
    }

    /**
     * Сортирует строки в допустимом по размеру файле
     *
     * @param pathToFile путь до файла
     */
    @SneakyThrows
    private void sortLineToFile(String pathToFile) {
        var fileService = new FileService();
        List<String> listForSort;
        log.debug("Считываем файл для сортировки");
        try (var fileReader = new FileReader(pathToFile);
             var reader = new BufferedReader(fileReader)) {
            listForSort = reader.lines().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
        }
        var length = pathToFile.split("/").length;
        var fileName = pathToFile.split("/")[length - 1];
        fileService.writeToFile(PATH_TO_HELPERS + fileName, listForSort.get(0), false);
        for (int i = 1; i < listForSort.size(); i++) {
            fileService.writeToFile(PATH_TO_HELPERS + fileName, listForSort.get(i), true);
        }
    }


    /**
     * Проверка размера текстового файла. Если влезает в ограничение, то вернет true
     *
     * @param pathToFile путь до файла
     * @param lineLimit  максимально допустимое количество строк
     */
    @SneakyThrows
    private boolean checkSizeFile(String pathToFile, int lineLimit) {
        long countLine;
        log.info("Проверяем размер текстового файла");
        try (var fileReader = new FileReader(pathToFile);
             var reader = new BufferedReader(fileReader)) {
            countLine = reader.lines().count();
        }
        return countLine < lineLimit;
    }

}
