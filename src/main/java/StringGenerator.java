import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.FileWriter;

@Slf4j
public class StringGenerator {
    @SneakyThrows
    public static void generateLinesToFile(int numberOfLines, int maxLineLength, String fileName) {
        log.debug("Записиваем строки в файл");
        try (FileWriter file = new FileWriter(fileName)) {
            for (int i = 0; i < numberOfLines; i++) {
                String stringLine = RandomStringUtils.randomAlphanumeric(1, maxLineLength);
                file.write(stringLine + "\n");
                log.debug("В файл сохранили строку - {}", stringLine);
            }
        }
        log.info("Завершили запись в файл");

    }
}
