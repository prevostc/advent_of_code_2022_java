package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day15Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day15();
        val answer = day.part1("day15/example.txt", 10);
        assertEquals(26, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day15();
        val answer = day.part1("day15/input.txt", 2_000_000);
        System.out.println("Day15 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day15();
        val answer = day.part2("day15/example.txt", 0, 20);
        assertEquals(BigInteger.valueOf(56000011), answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day15();
        val answer = day.part2("day15/input.txt", 0, 4_000_000);
        System.out.println("Day15 - Part 2: " + answer);
    }
}
