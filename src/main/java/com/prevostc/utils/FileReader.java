package com.prevostc.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import lombok.val;

public class FileReader {
    public List<String> readTestLines(String path) throws IOException {
        val classLoader = getClass().getClassLoader();
        return Files.readAllLines(Paths.get(classLoader.getResource(path).getFile()));
    }
}
