import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Slf4j
public class LineSorterService {
    final static String PATH_TO_HELPERS = "./src/main/resources/helpers/";

    @SneakyThrows
    public static void sortLinesInFile(String pathToTheFile, int sizePartLine, int lineLimit) {
        Map<String, Integer> lettersPassed = new TreeMap<>();
        if (checkSizeFile(pathToTheFile, lineLimit)) {
            log.info("Переданный файл меньше, чем '{}'. Можно отсортировать", lineLimit);
            sortLineToFile(pathToTheFile);
        } else {
            log.info("Переданный файл превышает допустимый размер '{}'. Переходим к разделению файла", lineLimit);
            fileSeparator(pathToTheFile, lettersPassed, sizePartLine);
        }
        log.info("Добиваемся чтобы все файлы были допустимого размера");
        while (true) {
            sizePartLine = sizePartLine + 2;
            int counter = lettersPassed.keySet().size();
            for (Map.Entry<String, Integer> partLine : lettersPassed.entrySet()) {
                if (partLine.getValue() > lineLimit) {
                    String fileName = partLine.getKey().toLowerCase(Locale.ROOT);
                    fileSeparator(PATH_TO_HELPERS + fileName, lettersPassed, 5);
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
        for (String fileName : lettersPassed.keySet()) {
            sortLineToFile(PATH_TO_HELPERS + fileName + ".txt");
        }
        log.info("Создаем итоговый файл");
        FileService.createNewFile(pathToTheFile);
        log.info("Сортируем файлы и помещаем в итоговый файл строки");
        lettersPassed.keySet().stream().sorted().forEach(e -> formationFinalFile(pathToTheFile, e));
    }

    /**
     * Переносит строки из одного файла в другой
     *
     * @param pathFinalFile основной файл для записи
     * @param pathPartFile  файл для чтения и переноса строк
     */
    @SneakyThrows
    private static void formationFinalFile(String pathFinalFile, String pathPartFile) {
        log.debug("Заполняем итоговый файл");
        String partFileName = PATH_TO_HELPERS + pathPartFile + ".txt";
        try (FileReader fileReader = new FileReader(partFileName);
             FileWriter writer = new FileWriter(pathFinalFile, true);
             BufferedReader reader = new BufferedReader(fileReader)) {
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
    private static void fileSeparator(String pathToFile, Map<String, Integer> lettersPassed, int sizePartLine) {
        log.info("Разделяем файл на более мелкие части");
        try (FileReader fileReader = new FileReader(pathToFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            log.info("Сравниваем начало строк");
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() - 1 < sizePartLine) {
                    lettersPassed.put(line.toLowerCase(Locale.ROOT), +1);
                    var fileName = line.toLowerCase(Locale.ROOT) + ".txt";
                    FileService.createNewFile(PATH_TO_HELPERS + fileName);
                    FileService.writeToFile(PATH_TO_HELPERS + fileName, line, true);
                    continue;
                }
                String startLine = line.substring(0, sizePartLine);
                if (lettersPassed.containsKey(startLine.toLowerCase(Locale.ROOT))) {
                    log.debug("Добавили часть строки '{}' в файл", startLine);
                    FileService.writeToFile(PATH_TO_HELPERS + startLine.toLowerCase(Locale.ROOT) + ".txt",
                            line,
                            true);
                    lettersPassed.put(startLine.toLowerCase(Locale.ROOT),
                            (lettersPassed.get(startLine.toLowerCase(Locale.ROOT))) + 1);
                } else {
                    log.debug("Обновили список уникальных строк на '{}'", startLine);
                    lettersPassed.put(startLine.toLowerCase(Locale.ROOT), 1);
                    startLine = startLine.toLowerCase(Locale.ROOT) + ".txt";
                    log.debug("Создаем новый файл с именем '{}'", startLine);
                    FileService.createNewFile(PATH_TO_HELPERS + startLine);
                    log.debug("Записываем строку в новый файл");
                    FileService.writeToFile(PATH_TO_HELPERS + startLine, line, true);
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
    private static void sortLineToFile(String pathToFile) {
        List<String> listForSort;
        log.debug("Считываем файл для сортировки");
        try (FileReader fileReader = new FileReader(pathToFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            listForSort = reader.lines().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
        }
        int length = pathToFile.split("/").length;
        String fileName = pathToFile.split("/")[length - 1];
        FileService.writeToFile(PATH_TO_HELPERS + fileName, listForSort.get(0), false);
        for (int i = 1; i < listForSort.size(); i++) {
            FileService.writeToFile(PATH_TO_HELPERS + fileName, listForSort.get(i), true);
        }
    }


    /**
     * Проверка размера текстового файла. Если влезает в ограничение, то вернет true
     *
     * @param pathToFile путь до файла
     * @param lineLimit  максимально допустимое количество строк
     */
    @SneakyThrows
    private static boolean checkSizeFile(String pathToFile, int lineLimit) {
        long countLine;
        log.info("Проверяем размер текстового файла");
        try (FileReader fileReader = new FileReader(pathToFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            countLine = reader.lines().count();
        }
        return countLine < lineLimit;
    }

}
