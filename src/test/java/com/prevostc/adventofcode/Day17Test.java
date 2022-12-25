package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import com.prevostc.utils.BigInt;

import lombok.val;

public class Day17Test {

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day17();
        val answer = day.part1("day17/example.txt");
        assertEquals(3068, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day17();
        val answer = day.part1("day17/input.txt");
        assertEquals(3083, answer);
        System.out.println("Day17 - Part 1: " + answer);
    }

    @Test
    public void testPart2Example() throws IOException {
        val day = new Day17();
        val answer = day.part2("day17/example.txt");
        assertEquals(BigInt.of("1514285714288"), answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val day = new Day17();
        val answer = day.part2("day17/input.txt");
        assertEquals(BigInt.of("1532183908048"), answer);
        System.out.println("Day17 - Part 2: " + answer);
    }
}
