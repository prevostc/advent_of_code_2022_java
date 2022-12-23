package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day16Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day16();
        val answer = day.part1("day16/example.txt");
        assertEquals(1651, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day16();
        val answer = day.part1("day16/input.txt");
        System.out.println("Day16 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day16();
        val answer = day.part2("day16/example.txt");
        assertEquals(1707, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day16();
        val answer = day.part2("day16/input.txt");
        System.out.println("Day16 - Part 2: " + answer);
    }
}
