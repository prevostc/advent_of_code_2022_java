package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    // carefully ordered to favor the right and down direction
    private final Vec2d[] nextCandidatesOffsets = new Vec2d[] { new Vec2d(0, 1), new Vec2d(1, 0), new Vec2d(-1, 0),
            new Vec2d(0, -1),
            new Vec2d(0, 0) };

    private Deque<State> sDeque = null;

    private record State(Vec2d position, int time, int step) {

    }

    public Integer part1(String inputFilePath) throws IOException {
        parse(inputFilePath);
        return solve(2);
    }

    public Integer part2(String inputFilePath) throws IOException {
        parse(inputFilePath);
        return solve(4);
    }

    public Integer solve(int stopAtStep) throws IOException {
        sDeque.add(new State(this.initialPosition, 0, 1));

        while (!sDeque.isEmpty()) {
            val s = sDeque.pop();
            if (visited.contains(s)) {
                continue;
            }
            visited.add(s);

            val pos = s.position();
            val time = s.time();
            val step = s.step();

            if (step == stopAtStep) {
                return time;
            }

            // find possible next moves by looking into the future
            for (val candidateOffset : this.nextCandidatesOffsets) {
                val candidate = pos.add(candidateOffset);

                boolean outOfBounds = !candidate.equals(this.initialPosition) && !candidate.equals(this.target)
                        && (candidate.x() < 0 || candidate.x() >= this.dimensions.x()
                                || candidate.y() < 0 || candidate.y() >= this.dimensions.y());

                if (outOfBounds) {
                    continue;
                }

                boolean hasBlizzard = false;
                if (!candidate.equals(this.initialPosition) && !candidate.equals(this.target)) {
                    hasBlizzard = this.blizzards[0].get(time + 1, candidate)
                            || this.blizzards[1].get(time + 1, candidate)
                            || this.blizzards[2].get(time + 1, candidate)
                            || this.blizzards[3].get(time + 1, candidate);
                }

                if (!hasBlizzard) {
                    if (candidate.equals(this.targetByStep[step])) {
                        sDeque.add(new State(candidate, time + 1, step + 1));
                    } else {
                        sDeque.add(new State(candidate, time + 1, step));
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
        this.sDeque = new ArrayDeque<>();
        this.targetByStep = new Vec2d[] {
                this.initialPosition,
                this.target,
                this.initialPosition,
                this.target,
        };

        this.blizzards = new LoopingTimeGrid[4];
        for (Direction direction : Direction.values()) {
            val offset = this.directionToOffset.get(direction);
            this.blizzards[direction.ordinal()] = new LoopingTimeGrid(this.dimensions, offset.negate());
        }

        for (int y = 0; y < this.dimensions.y(); y++) {
            String line = lines.get(y + 1).substring(1, this.dimensions.x() + 1);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '>') {
                    blizzards[Direction.RIGHT.ordinal()].set(x, y, true);
                } else if (line.charAt(x) == '<') {
                    blizzards[Direction.LEFT.ordinal()].set(x, y, true);
                } else if (line.charAt(x) == '^') {
                    blizzards[Direction.UP.ordinal()].set(x, y, true);
                } else if (line.charAt(x) == 'v') {
                    blizzards[Direction.DOWN.ordinal()].set(x, y, true);
                }
            }
        }
    }

    private Map<Direction, Vec2d> directionToOffset = Map.of(
            Direction.UP, new Vec2d(0, -1),
            Direction.DOWN, new Vec2d(0, 1),
            Direction.LEFT, new Vec2d(-1, 0),
            Direction.RIGHT, new Vec2d(1, 0));

    private enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    /**
     * We need a way to look at blizzard position any point in time
     * this does exactly that
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
