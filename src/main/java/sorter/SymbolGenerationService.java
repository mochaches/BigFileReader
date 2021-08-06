package sorter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.FileWriter;

@Slf4j
public class SymbolGenerationService {

    /**
     * Генерирует строки и заполняет ими файл
     *
     * @param amountOfLines количество генерируемых строк
     * @param maxLineLength максимальная длина генерируемой строки
     * @param fileName      название файла для заполнения
     */
    @SneakyThrows
    public void generateLinesAndWriteToFile(int amountOfLines, int maxLineLength, String fileName) {
        log.debug("Генерируем строки и заполняем ими файл");
        try (FileWriter file = new FileWriter(fileName)) {
            for (int i = 0; i < amountOfLines; i++) {
                String stringLine = RandomStringUtils.randomAlphanumeric(1, maxLineLength);
                file.write(stringLine + "\n");
                log.debug("В файл сохранили строку - {}", stringLine);
            }
        }
        log.info("Завершили запись в файл");

    }
}
