package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day14Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day14();
        val answer = day.part1("day14/example.txt");
        assertEquals(24, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day14();
        val answer = day.part1("day14/input.txt");
        System.out.println("Day 14 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day14();
        val answer = day.part2("day14/example.txt");
        assertEquals(93, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day14();
        val answer = day.part2("day14/input.txt");
        System.out.println("Day 13 - Part 2: " + answer);
    }
}
