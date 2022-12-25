package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day25Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day25();
        val answer = day.part1("day25/example.txt");
        assertEquals("2=-1=0", answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day25();
        val answer = day.part1("day25/input.txt");
        System.out.println("Day25 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day25();
        val answer = day.part2("day25/example.txt");
        assertEquals(54, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day25();
        val answer = day.part2("day25/input.txt");
        System.out.println("Day25 - Part 2: " + answer);
    }
}
