package com.github.mochaches.big.file.sorter.service;

import java.io.File;

public interface FileService {
    void createFile(String fileName);

    void writeToFile(String pathAndFileName, String line, boolean append);

    void remove(String fileName);
}
