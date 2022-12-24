package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day21Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day21();
        val answer = day.part1("day21/example.txt");
        assertEquals(BigInteger.valueOf(152), answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day21();
        val answer = day.part1("day21/input.txt");
        System.out.println("Day21 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day21();
        val answer = day.part2("day21/example.txt");
        assertEquals(BigInteger.valueOf(301), answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day21();
        val answer = day.part2("day21/input.txt");
        System.out.println("Day21 - Part 2: " + answer);
    }
}
