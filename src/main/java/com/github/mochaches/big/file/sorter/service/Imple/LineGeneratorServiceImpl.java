package com.github.mochaches.big.file.sorter.service.Imple;

import com.github.mochaches.big.file.sorter.service.LineGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LineGeneratorServiceImpl implements LineGeneratorService {

    @Override
    @SneakyThrows
    public String generate(int maxLineLength) {
        log.debug("Генерируем строку с максимальной длинной - '{}'", maxLineLength);
        return RandomStringUtils.randomAlphanumeric(1, maxLineLength);
    }

}
