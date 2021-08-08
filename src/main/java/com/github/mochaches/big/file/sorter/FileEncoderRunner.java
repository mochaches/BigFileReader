package com.github.mochaches.big.file.sorter;

import com.github.mochaches.big.file.sorter.config.ApplicationConfig;
import com.github.mochaches.big.file.sorter.service.FileService;
import com.github.mochaches.big.file.sorter.service.LineGeneratorService;
import com.github.mochaches.big.file.sorter.service.LineSorterService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileEncoderRunner implements ApplicationRunner {
    final LineSorterService lineSorterService;
    final FileService fileService;
    final LineGeneratorService lineGeneratorService;
    final ApplicationConfig applicationConfig;


    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) {
        fileService.createFile(applicationConfig.getSourceFile());
        lineGeneratorService.generate(applicationConfig.getLineLimit(), applicationConfig.getMaxLineLength(), applicationConfig.getSourceFile());
        lineSorterService.sort(applicationConfig.getSourceFile(), applicationConfig.getSortSize(), applicationConfig.getLineLimit());
    }
}