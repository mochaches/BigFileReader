package com.github.mochaches.big.file.sorter.service;

public interface LineSorterService {
    void sort(String pathToTheFile, int sizePartLine, int lineLimit);
}
