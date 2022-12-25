package com.prevostc.adventofcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import com.prevostc.utils.Direction;
import com.prevostc.utils.Vec2d;

import org.junit.jupiter.api.Test;

import lombok.val;

public class Day22Test {

    @Test
    public void testRotateDiceCoord() {
        val day = new Day22(50, List.of(), List.of());

        assertEquals(new Vec2d(49, 40), day.diceEdgePosRotateClockwise(new Vec2d(40, 0)));
        assertEquals(new Vec2d(9, 49), day.diceEdgePosRotateClockwise(new Vec2d(49, 40)));
        assertEquals(new Vec2d(0, 10), day.diceEdgePosRotateClockwise(new Vec2d(10, 49)));
        assertEquals(new Vec2d(39, 0), day.diceEdgePosRotateClockwise(new Vec2d(0, 10)));
    }

    @Test
    public void testPortalCoord() {
        val day = new Day22(50, List.of(), List.of());

        var pos = new Vec2d(0, 32);
        pos = day.diceEdgePosRotateClockwise(pos);
        pos = day.diceEdgePosRotateClockwise(pos);
        pos = day.diceEdgePosOpposite(pos);
        assertEquals(new Vec2d(0, 17), pos);
    }

    @Test
    public void testPart1Example() throws IOException {
        val day = new Day22(0, List.of(), List.of());
        val answer = day.part1("day22/example.txt");
        assertEquals(6032, answer);
    }

    @Test
    public void testPart1Main() throws IOException {
        val day = new Day22(0, List.of(), List.of());
        val answer = day.part1("day22/input.txt");
        System.out.println("Day22 - Part 1: " + answer);
        assertEquals(75388, answer);
    }

    @Test
    public void testPart2Example() throws IOException {

        // there's probably a smart way to infer those from face positions
        val dicePortals = List.of(
                new Day22.Portal(1, Direction.UP, 2, Direction.DOWN),
                new Day22.Portal(1, Direction.RIGHT, 6, Direction.LEFT),
                new Day22.Portal(1, Direction.LEFT, 3, Direction.DOWN),
                new Day22.Portal(2, Direction.UP, 1, Direction.DOWN),
                new Day22.Portal(2, Direction.DOWN, 5, Direction.UP),
                new Day22.Portal(2, Direction.LEFT, 6, Direction.UP),
                new Day22.Portal(3, Direction.UP, 1, Direction.RIGHT),
                new Day22.Portal(3, Direction.DOWN, 5, Direction.RIGHT),
                new Day22.Portal(4, Direction.RIGHT, 6, Direction.DOWN),
                new Day22.Portal(5, Direction.DOWN, 2, Direction.UP),
                new Day22.Portal(5, Direction.LEFT, 3, Direction.UP),
                new Day22.Portal(6, Direction.UP, 4, Direction.LEFT),
                new Day22.Portal(6, Direction.RIGHT, 1, Direction.LEFT),
                new Day22.Portal(6, Direction.DOWN, 2, Direction.RIGHT));
        val diceFacePositions = List.of(
                new Day22.Face(1, new Vec2d(2, 0)),
                new Day22.Face(2, new Vec2d(0, 1)),
                new Day22.Face(3, new Vec2d(1, 1)),
                new Day22.Face(4, new Vec2d(2, 1)),
                new Day22.Face(5, new Vec2d(2, 2)),
                new Day22.Face(6, new Vec2d(3, 2)));
        val day = new Day22(4, diceFacePositions, dicePortals);
        val answer = day.part2("day22/example.txt");
        assertEquals(5031, answer);
    }

    @Test
    public void testPart2Main() throws IOException {
        val dicePortals = List.of(
                new Day22.Portal(1, Direction.UP, 6, Direction.RIGHT),
                new Day22.Portal(1, Direction.LEFT, 4, Direction.RIGHT),
                new Day22.Portal(2, Direction.UP, 6, Direction.UP),
                new Day22.Portal(2, Direction.RIGHT, 5, Direction.LEFT),
                new Day22.Portal(2, Direction.DOWN, 3, Direction.LEFT),
                new Day22.Portal(3, Direction.RIGHT, 2, Direction.UP),
                new Day22.Portal(3, Direction.LEFT, 4, Direction.DOWN),
                new Day22.Portal(4, Direction.UP, 3, Direction.RIGHT),
                new Day22.Portal(4, Direction.LEFT, 1, Direction.RIGHT),
                new Day22.Portal(5, Direction.RIGHT, 2, Direction.LEFT),
                new Day22.Portal(5, Direction.DOWN, 6, Direction.LEFT),
                new Day22.Portal(6, Direction.RIGHT, 5, Direction.UP),
                new Day22.Portal(6, Direction.DOWN, 2, Direction.DOWN),
                new Day22.Portal(6, Direction.LEFT, 1, Direction.DOWN));
        val diceFacePositions = List.of(
                new Day22.Face(1, new Vec2d(1, 0)),
                new Day22.Face(2, new Vec2d(2, 0)),
                new Day22.Face(3, new Vec2d(1, 1)),
                new Day22.Face(4, new Vec2d(0, 2)),
                new Day22.Face(5, new Vec2d(1, 2)),
                new Day22.Face(6, new Vec2d(0, 3)));

        val day = new Day22(50, diceFacePositions, dicePortals);
        val answer = day.part2("day22/input.txt");
        System.out.println("Day22 - Part 2: " + answer);
        assertEquals(182170, answer);
    }
}
