package com.github.mochaches.big.file.sorter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("big-file-reader")
public class ApplicationConfig {
    String sourceFile;
    Integer lineLimit;
    Integer maxLineLength;
    String helpersFilePath;
    Integer sortSize;
}
