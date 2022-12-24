package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day24Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day24();
        val answer = day.part1("day24/example.txt");
        assertEquals(18, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day24();
        val answer = day.part1("day24/input.txt");
        System.out.println("Day24 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day24();
        val answer = day.part2("day24/example.txt");
        assertEquals(54, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day24();
        val answer = day.part2("day24/input.txt");
        System.out.println("Day24 - Part 2: " + answer);
    }
}
