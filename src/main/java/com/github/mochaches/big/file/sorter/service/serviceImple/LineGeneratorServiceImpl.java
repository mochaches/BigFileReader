package com.github.mochaches.big.file.sorter.service.serviceImple;

import com.github.mochaches.big.file.sorter.service.LineGeneratorService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.io.FileWriter;

@Slf4j
@Service
public class LineGeneratorServiceImpl implements LineGeneratorService {

    @Override
    @SneakyThrows
    public void generate(int amountOfLines, int maxLineLength, String fileName) {
        log.debug("Генерируем строки и заполняем ими файл");
        try (var file = new FileWriter(fileName)) {
            for (int i = 0; i < amountOfLines; i++) {
                var stringLine = RandomStringUtils.randomAlphanumeric(1, maxLineLength);
                file.write(stringLine + "\n");
                log.debug("В файл сохранили строку - {}", stringLine);
            }
        }
        log.info("Завершили запись в файл");
    }
}
