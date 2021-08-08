package com.github.mochaches.big.file.sorter;

import com.github.mochaches.big.file.sorter.service.Imple.LineSorterServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileEncoderRunner implements ApplicationRunner {
    final LineSorterServiceImpl lineSorterService;

    @Override
    @SneakyThrows
    public void run(ApplicationArguments args) {
        lineSorterService.fileGenerateAndSorter();
    }
}