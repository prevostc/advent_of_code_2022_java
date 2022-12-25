package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day23Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day23();
        val answer = day.part1("day23/example.txt");
        assertEquals(110, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day23();
        val answer = day.part1("day23/input.txt");
        assertEquals(4138, answer);
        System.out.println("Day23 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day23();
        val answer = day.part2("day23/example.txt");
        assertEquals(20, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day23();
        val answer = day.part2("day23/input.txt");
        assertEquals(1010, answer);
        System.out.println("Day23 - Part 2: " + answer);
    }
}
