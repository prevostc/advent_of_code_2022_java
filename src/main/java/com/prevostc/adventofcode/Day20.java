package com.prevostc.adventofcode;

import java.io.IOException;

import com.prevostc.utils.FileReader;

public class Day20 {

    public Integer part1(String inputFilePath) throws IOException {
        val initial = parse(inputFilePath);
        return 0;
    }

    public Integer part2(String inputFilePath) throws IOException {
        var initial = parse(inputFilePath);
        return 0;
    }

    FileReader fileReader = new FileReader();

    private int[] parse(String inputFilePath) throws IOException {
        return fileReader.readAllLines(inputFilePath)
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();
    }
}
