package com.github.mochaches.big.file.sorter;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class BigFileSorterApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(BigFileSorterApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .registerShutdownHook(true)
                .run();
    }
}
