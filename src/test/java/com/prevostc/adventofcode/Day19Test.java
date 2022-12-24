package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day19Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day19();
        val answer = day.part1("day19/example.txt");
        assertEquals(33, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day19();
        val answer = day.part1("day19/input.txt");
        System.out.println("Day19 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day19();
        val answer = day.part2("day19/example.txt");
        assertEquals(3472, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day19();
        val answer = day.part2("day19/input.txt");
        System.out.println("Day19 - Part 2: " + answer);
    }
}
