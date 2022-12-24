package com.prevostc.adventofcode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.prevostc.utils.FileReader;
import com.prevostc.utils.Grid2d;
import com.prevostc.utils.LoopingList;
import com.prevostc.utils.Vec2d;

import lombok.val;

public class Day17 {

    FileReader fileReader = new FileReader();

    private enum Elem {
        EMPTY('.'), ROCK('#');

        private char symbol;

        Elem(char symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return String.valueOf(symbol);
        }
    }

    private class Shape implements Cloneable {
        private Vec2d[] rocks;
        int leftMostX = Integer.MAX_VALUE;
        int bottomMostY = Integer.MIN_VALUE;

        Shape(Vec2d[] rocks) {
            this.rocks = rocks;
            for (val rock : rocks) {
                leftMostX = Math.min(leftMostX, rock.x());
                bottomMostY = Math.max(bottomMostY, rock.y());
            }
        }

        public Shape clone() {
            val newRocks = new Vec2d[rocks.length];
            for (int i = 0; i < rocks.length; i++) {
                newRocks[i] = rocks[i];
            }
            return new Shape(newRocks);
        }

        void move(Vec2d movement) {
            for (int i = 0; i < rocks.length; i++) {
                rocks[i] = rocks[i].add(movement);
            }
            leftMostX += movement.x();
            bottomMostY += movement.y();
        }
    }

    private final LoopingList<Shape> shapes = new LoopingList<>(new Shape[] {
            new Shape(new Vec2d[] { new Vec2d(0, 0), new Vec2d(1, 0), new Vec2d(2, 0), new Vec2d(3, 0) }),
            new Shape(new Vec2d[] { new Vec2d(1, 0), new Vec2d(0, 1), new Vec2d(1, 1), new Vec2d(2, 1),
                    new Vec2d(1, 2) }),
            new Shape(new Vec2d[] { new Vec2d(2, 0), new Vec2d(2, 1), new Vec2d(0, 2), new Vec2d(1, 2),
                    new Vec2d(2, 2) }),
            new Shape(new Vec2d[] { new Vec2d(0, 0), new Vec2d(0, 1), new Vec2d(0, 2), new Vec2d(0, 3) }),
            new Shape(new Vec2d[] { new Vec2d(0, 0), new Vec2d(1, 0), new Vec2d(0, 1), new Vec2d(1, 1) }),
    });
    private final Map<String, Vec2d> jetMovement = Map.of("<", new Vec2d(-1, 0), ">", new Vec2d(1, 0),
            "^", new Vec2d(0, -1), "v", new Vec2d(0, 1));

    private boolean canMove(Grid2d<Elem> grid, Shape shape, Vec2d movement) {
        for (val rock : shape.rocks) {
            val newRock = rock.add(movement);
            // out of walls
            if (newRock.x() < 0 || newRock.x() > grid.bottomRight().x()) {
                return false;
            }
            // blocked by smth else
            if (grid.get(newRock) == Elem.ROCK) {
                return false;
            }
        }
        return true;
    }

    private void dropOne(Grid2d<Elem> grid, LoopingList<String> jetPattern) {
        Shape shape = shapes.next();
        shape = shape.clone();

        // Each rock appears so that its left edge is two units away from the left wall
        // and its bottom edge is three units above the highest rock in the room
        int highestRock = grid.topLeft().y();
        int moveY = highestRock - shape.bottomMostY - 4;
        shape.move(new Vec2d(2, moveY));

        int maxLoops = 1000;
        while (maxLoops-- > 0) {
            val slide = jetMovement.get(jetPattern.next());
            if (canMove(grid, shape, slide)) {
                shape.move(slide);
            }
            val drop = new Vec2d(0, 1);
            if (canMove(grid, shape, drop)) {
                shape.move(drop);
            } else {
                // we can't move down, so we place the shape in the grid
                for (val rock : shape.rocks) {
                    grid.set(rock, Elem.ROCK);
                }
                break;
            }
        }
    }

    public Integer part1(String inputFilePath) throws IOException {
        LoopingList<String> jetPattern = new LoopingList(fileReader.readAllLines(inputFilePath)
                .get(0).chars().mapToObj(c -> Character.toString(c)).collect(Collectors.toList()));

        val gridWidth = 7;
        val gridHeight = 1_000_001;
        val grid = new Grid2d<>(gridWidth, gridHeight, Elem.EMPTY);

        // put the floor in place
        for (int x = 0; x < gridWidth; x++) {
            grid.set(x, gridHeight - 1, Elem.ROCK);
        }

        for (int i = 0; i < 2022; i++) {
            dropOne(grid, jetPattern);
            // System.out.println(grid);
        }
        return gridHeight - grid.topLeft().y() - 1;
    }

    public BigInteger part2(String inputFilePath) throws IOException {
        LoopingList<String> jetPattern = new LoopingList(fileReader.readAllLines(inputFilePath)
                .get(0).chars().mapToObj(c -> Character.toString(c)).collect(Collectors.toList()));

        // same as 1, but we look for a pattern since we can't compute all the way up
        // we'll just hope the jet patterns are not messed up

        val gridWidth = 7;
        val gridHeight = 1_00_000_001;
        val grid = new Grid2d<>(gridWidth, gridHeight, Elem.EMPTY);
        val maxIter = BigInteger.TEN.pow(12);

        // put the floor in place
        for (int x = 0; x < gridWidth; x++) {
            grid.set(x, gridHeight - 1, Elem.ROCK);
        }
        int slidingWindowHeight = jetPattern.size();
        Map<String, Vec2d> lastSeenMap = new HashMap<>(); // map of (height, rocks dropped)
        BigInteger skippedHeight = BigInteger.ZERO.subtract(BigInteger.ONE);

        boolean hasSkipped = false;

        for (BigInteger droppedBlocks = BigInteger.ZERO; droppedBlocks
                .compareTo(maxIter) < 0; droppedBlocks = droppedBlocks
                        .add(BigInteger.ONE)) {
            dropOne(grid, jetPattern);

            if (hasSkipped) {
                continue;
            }

            val cacheKey = hashSlidingWindow(grid, slidingWindowHeight);
            val lastSeenData = lastSeenMap.get(cacheKey);
            // never seen
            if (lastSeenData == null) {
                lastSeenMap.put(cacheKey, new Vec2d(grid.topLeft().y(), droppedBlocks.intValue()));
                continue;
            }
            // seen, but too soon
            if (lastSeenData.x() - grid.topLeft().y() < slidingWindowHeight) {
                continue;
            }

            // we found a match, do some magic skip
            val patternBlockCount = BigInteger.valueOf(droppedBlocks.intValue() - lastSeenData.y());
            val patternHeight = BigInteger.valueOf(lastSeenData.x() - grid.topLeft().y());

            BigInteger remainingBlocks = maxIter.subtract(droppedBlocks);
            BigInteger repeats = remainingBlocks.divide(patternBlockCount);
            droppedBlocks = droppedBlocks.add(repeats.multiply(patternBlockCount));
            skippedHeight = repeats.multiply(patternHeight);
            hasSkipped = true;
        }

        return BigInteger.valueOf(gridHeight - grid.topLeft().y() - 1).add(skippedHeight);
    }

    private String hashSlidingWindow(Grid2d<Elem> grid, int slidingWindowHeight) {
        val sb = new StringBuilder();
        for (int y = grid.topLeft().y(); y < grid.topLeft().y() + slidingWindowHeight
                && y < grid.bottomRight().y(); y++) {
            for (int x = 0; x <= grid.bottomRight().x(); x++) {
                sb.append(grid.get(x, y));
            }
        }
        return sb.toString();
    }
}
