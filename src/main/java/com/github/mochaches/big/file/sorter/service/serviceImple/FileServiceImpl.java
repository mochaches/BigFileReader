package com.github.mochaches.big.file.sorter.service.serviceImple;

import com.github.mochaches.big.file.sorter.service.FileService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Override
    @SneakyThrows
    public void createFile(String pathAndFileName) {
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
     * @param line            добавляемая строка
     * @param append          признак дозаписи файла
     */
    @SneakyThrows
    public void writeToFile(String pathAndFileName, String line, boolean append) {
        try (var file = new FileWriter(pathAndFileName, append);
             var bufferWriter = new BufferedWriter(file)
        ) {
            bufferWriter.write(line + "\n");
            log.debug("Строка '{}' успешно записана в файл", line);
        }
    }

    @Override
    @SneakyThrows
    public void remove(String pathAndFileName) {
        Files.deleteIfExists(Paths.get(pathAndFileName));
        log.debug("Файл '{}' успешно удален", pathAndFileName);
    }
}
