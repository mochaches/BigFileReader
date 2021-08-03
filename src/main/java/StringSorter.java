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
public class StringSorter {

    @SneakyThrows
    public static void sortLinesInFile(String pathToTheFile, int sizePartLine, int lineLimit) {
        log.info("создаем словарь уникальных начал строк(3 символа) и их количества");
        Map<String, Integer> lettersPassed = new TreeMap<>();
        if (checkSizeFile(pathToTheFile, 5)) {
            log.info("Файл невелик. Можно отсортировать");
            sortLineToFile(pathToTheFile);
        } else {
            log.info("Файл превышает допустимый размер. Переходим к разделению файла");
            fileSeparator(pathToTheFile, lettersPassed, sizePartLine);
        }
        log.info("Проверяем более мелки части и при необходимости делим файл на доступные для сортировки части");
        while (true) {
            sizePartLine = sizePartLine + 2;
            int counter = lettersPassed.keySet().size();
            for (Map.Entry<String, Integer> partLine : lettersPassed.entrySet()) {
                if (partLine.getValue() > lineLimit) {
                    fileSeparator("./src/main/resources/helpers/" + partLine.getKey().toLowerCase(Locale.ROOT), lettersPassed, 5);
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
            sortLineToFile("./src/main/resources/helpers/" + fileName + ".txt");
        }
        log.info("Создаем итоговый файл");
        CreateNewFile.createNewFile(pathToTheFile);
        log.info("Сортируем файлы и помещаем в итоговый файл строки");
        lettersPassed.keySet().stream().sorted().forEach(e -> formationFinalFile(pathToTheFile, e));
    }

    @SneakyThrows
    private static void formationFinalFile(String pathFinalFile, String pathPartFile) {
        log.info("Заполняем итоговый файл");
        String partFileName = "./src/main/resources/helpers/" + pathPartFile + ".txt";
        try (FileReader fileReader = new FileReader(partFileName);
             FileWriter writer = new FileWriter(pathFinalFile, true);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
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
                    CreateNewFile.createNewFile("./src/main/resources/helpers/" + fileName);
                    WriterClass.writeToFile(fileName, line, true);
                    continue;
                }
                String startLine = line.substring(0, sizePartLine);
                if (lettersPassed.containsKey(startLine.toLowerCase(Locale.ROOT))) {
                    log.info("Добавили часть строки '{}' в файл", startLine);
                    WriterClass.writeToFile(startLine.toLowerCase(Locale.ROOT) + ".txt", line, true);
                    try {
                        lettersPassed.put(startLine.toLowerCase(Locale.ROOT), (lettersPassed.get(startLine.toLowerCase(Locale.ROOT))) + 1);
                    } catch (NullPointerException npe) {
                        log.error("она таки свалилась!");
                        log.error("Строка - '{}'", line);
                        npe.getStackTrace();
                        log.error("--------------------------------------------");
                        npe.getMessage();
                        log.error("lettersPassed пыталась схавать");
                        log.error("'{}'", startLine.toLowerCase(Locale.ROOT));
                        log.error("'{}'", (lettersPassed.get(startLine)) + 1);
                    }
                } else {
                    log.info("Обновили список уникальных строк на '{}'", startLine);
                    lettersPassed.put(startLine.toLowerCase(Locale.ROOT), 1);
                    startLine = startLine.toLowerCase(Locale.ROOT) + ".txt";
                    log.info("Создаем новый файл с именем '{}'", startLine);
                    CreateNewFile.createNewFile("./src/main/resources/helpers/" + startLine);
                    log.info("Записываем строку в новый файл");
                    WriterClass.writeToFile(startLine, line, true);
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
        log.info("Считываем файл для сортировки");
        try (FileReader fileReader = new FileReader(pathToFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            listForSort = reader.lines().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
        }
        int length = pathToFile.split("/").length;
        String fileName = pathToFile.split("/")[length - 1];
        log.info("создаем файл с настройкой дозаписи");
        WriterClass.writeToFile(fileName, listForSort.get(0), false);
        log.info("Меняем настройку дозаписи");
        for (int i = 1; i < listForSort.size(); i++) {
            WriterClass.writeToFile(fileName, listForSort.get(i), true);
        }
    }


    /**
     * Проверка размера текстового файла. Если влезает в ограничение, то вернет true
     *
     * @param pathToFile            путь до файла
     * @param maxAllowableValueLine максимально допустимое количество символов
     */
    @SneakyThrows
    private static boolean checkSizeFile(String pathToFile, int maxAllowableValueLine) {
        long countLine;
        log.info("Проверяем размер текстового файла");
        try (FileReader fileReader = new FileReader(pathToFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            countLine = reader.lines().count();
        }
        return countLine < maxAllowableValueLine;
    }

}
