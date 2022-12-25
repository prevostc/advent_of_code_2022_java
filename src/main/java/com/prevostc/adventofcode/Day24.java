package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import com.prevostc.utils.Direction;
import com.prevostc.utils.FileReader;
import com.prevostc.utils.Vec2d;

import lombok.val;

public class Day24 {

    private Vec2d dimensions;
    private LoopingTimeGrid[] blizzards;
    private Set<State> visited = new HashSet<>();
    private Vec2d target = null;
    private Vec2d initialPosition = null;
    private Vec2d[] targetByStep;

    private record State(Vec2d position, int time, int step) {

    }

    public Integer part1(String inputFilePath) throws IOException {
        parse(inputFilePath);
        return solve(1);
    }

    public Integer part2(String inputFilePath) throws IOException {
        parse(inputFilePath);
        return solve(3);
    }

    public Integer solve(int stopAtStep) throws IOException {
        Deque<State> sDeque = new ArrayDeque<>();
        sDeque.add(new State(this.initialPosition, 0, 0));

        while (!sDeque.isEmpty()) {
            val s = sDeque.pop();
            if (visited.contains(s)) {
                continue;
            }
            visited.add(s);

            if (s.step() == stopAtStep) {
                return s.time();
            }

            // find possible next moves by looking into the future
            for (val candidate : s.position().getNeighborsCardinalAndSelf()) {

                // out of bounds test
                if (!candidate.equals(this.initialPosition) && !candidate.equals(this.target)
                        && !candidate.isContainedInDimension(this.dimensions)) {
                    continue;
                }

                boolean hasBlizzard = false;
                if (!candidate.equals(this.initialPosition) && !candidate.equals(this.target)) {
                    hasBlizzard = this.blizzards[0].get(s.time() + 1, candidate)
                            || this.blizzards[1].get(s.time() + 1, candidate)
                            || this.blizzards[2].get(s.time() + 1, candidate)
                            || this.blizzards[3].get(s.time() + 1, candidate);
                }

                if (!hasBlizzard) {
                    if (candidate.equals(this.targetByStep[s.step()])) {
                        sDeque.add(new State(candidate, s.time() + 1, s.step() + 1));
                    } else {
                        sDeque.add(new State(candidate, s.time() + 1, s.step()));
                    }
                }
            }
        }
        return 0;
    }

    FileReader fileReader = new FileReader();

    private void parse(String inputFilePath) throws IOException {
        val lines = fileReader.readAllLines(inputFilePath);
        this.dimensions = new Vec2d(lines.get(0).length() - 2, lines.size() - 2);
        this.target = this.dimensions.add(new Vec2d(-1, 0));
        this.initialPosition = new Vec2d(0, -1);
        this.targetByStep = new Vec2d[] {
                this.target,
                this.initialPosition,
                this.target,
        };

        this.blizzards = new LoopingTimeGrid[4];
        for (Direction direction : Direction.values()) {
            this.blizzards[direction.ordinal()] = new LoopingTimeGrid(this.dimensions, direction.toVec2d().negate());
        }

        for (int y = 0; y < this.dimensions.y(); y++) {
            String line = lines.get(y + 1).substring(1, this.dimensions.x() + 1);
            for (int x = 0; x < line.length(); x++) {
                val c = line.charAt(x);
                if (Direction.isArrowChar(c)) {
                    blizzards[Direction.fromArrowChar(c).ordinal()].set(x, y, true);
                }
            }
        }
    }

    /**
     * We need a way to look at blizzard position any point in time
     */
    private class LoopingTimeGrid {
        private boolean[] grid;
        private Vec2d dimensions;
        private Vec2d moveDirection;

        LoopingTimeGrid(Vec2d dimensions, Vec2d moveDirection) {
            this.dimensions = dimensions;
            this.moveDirection = moveDirection;
            this.grid = new boolean[dimensions.x() * dimensions.y()];
        }

        public void set(int x, int y, boolean value) {
            this.grid[y * dimensions.x() + x] = value;
        }

        public boolean get(int timeOffset, Vec2d pos) {
            return this.get(timeOffset, pos.x(), pos.y());
        }

        public boolean get(int timeOffset, int x, int y) {
            x = (x + timeOffset * moveDirection.x()) % dimensions.x();
            y = (y + timeOffset * moveDirection.y()) % dimensions.y();
            if (x < 0) {
                x += dimensions.x();
            }
            if (y < 0) {
                y += dimensions.y();
            }

            return this.grid[y * dimensions.x() + x];
        }
    }
}
