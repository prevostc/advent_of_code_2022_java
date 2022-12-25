package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

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
        assertEquals("2-=102--02--=1-12=22", answer);
        System.out.println("Day25 - Part 1: " + answer);
    }

}
