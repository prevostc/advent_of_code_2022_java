package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day18Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day18();
        val answer = day.part1("day18/example.txt");
        assertEquals(64, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day18();
        val answer = day.part1("day18/input.txt");
        System.out.println("Day18 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day18();
        val answer = day.part2("day18/example.txt");
        assertEquals(58, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day18();
        val answer = day.part2("day18/input.txt");
        System.out.println("Day18 - Part 2: " + answer);
    }
}
