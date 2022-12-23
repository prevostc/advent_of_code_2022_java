package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day13Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day13();
        val answer = day.part1("day13/example.txt");
        assertEquals(13, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day13();
        val answer = day.part1("day13/input.txt");
        System.out.println("Day13 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day13();
        val answer = day.part2("day13/example.txt");
        assertEquals(140, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day13();
        val answer = day.part2("day13/input.txt");
        System.out.println("Day13 - Part 2: " + answer);
    }
}
