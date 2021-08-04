import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@Slf4j
public class FileService {

    /**
     * Создает файл по указанному пути
     *
     * @param pathAndFileName путь и имя файла
     */
    @SneakyThrows
    public static void createNewFile(String pathAndFileName) {
        File file = new File(pathAndFileName);
        if (file.createNewFile()) {
            log.debug(pathAndFileName + " - файл создан по указанному пути");
        } else {
            log.debug(pathAndFileName + " - файл по указанному пути уже существует");
        }
    }

    /**
     * Записать строку в файл
     *
     * @param pathAndFileName название файла для записи и путь
     * @param lineToAdd       добавляемая строка
     * @param append          признак дозаписи файла
     */
    @SneakyThrows
    public static void writeToFile(String pathAndFileName, String lineToAdd, boolean append) {
        try (FileWriter file = new FileWriter(pathAndFileName, append);
             BufferedWriter bufferWriter = new BufferedWriter(file)
        ) {
            bufferWriter.write(lineToAdd + "\n");
        }
    }
}
