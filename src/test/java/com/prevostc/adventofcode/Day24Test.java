package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day22Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day22();
        val answer = day.part1("day22/example.txt");
        assertEquals(6032, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day22();
        val answer = day.part1("day22/input.txt");
        System.out.println("Day22 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day22();
        val answer = day.part2("day22/example.txt");
        assertEquals(58, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day22();
        val answer = day.part2("day22/input.txt");
        System.out.println("Day22 - Part 2: " + answer);
    }
}
