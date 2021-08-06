package sorter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@Slf4j
@Service
public class FileService {

    /**
     * Создает файл по указанному пути
     *
     * @param pathAndFileName путь и имя файла
     */
    @SneakyThrows
    public void createNewFile(String pathAndFileName) {
        var file = new File(pathAndFileName);
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
    public void writeToFile(String pathAndFileName, String lineToAdd, boolean append) {
        try (var file = new FileWriter(pathAndFileName, append);
             var bufferWriter = new BufferedWriter(file)
        ) {
            bufferWriter.write(lineToAdd + "\n");
        }
    }
}
