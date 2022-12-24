package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.prevostc.utils.FileReader;
import com.prevostc.utils.Grid2d;
import com.prevostc.utils.Vec2d;

import lombok.val;

public class Day22 {

    private Grid2d<Terrain> grid;
    private List<String> instructionList;
    private Monkey monkey;
    private Map<Direction, Vec2d> directionToOffset = Map.of(
            Direction.UP, new Vec2d(0, -1),
            Direction.DOWN, new Vec2d(0, 1),
            Direction.LEFT, new Vec2d(-1, 0),
            Direction.RIGHT, new Vec2d(1, 0));

    private Map<Direction, Map<String, Direction>> directionRotate = Map.of(
            Direction.UP, Map.of("L", Direction.LEFT, "R", Direction.RIGHT),
            Direction.DOWN, Map.of("L", Direction.RIGHT, "R", Direction.LEFT),
            Direction.LEFT, Map.of("L", Direction.DOWN, "R", Direction.UP),
            Direction.RIGHT, Map.of("L", Direction.UP, "R", Direction.DOWN));

    FileReader fileReader = new FileReader();

    public Integer part1(String inputFilePath) throws IOException {
        parse(inputFilePath);

        // find the leftmost grass node of the top row
        for (int x = 0; x < grid.getWidth(); x++) {
            if (grid.get(x, 0) == Terrain.GRASS) {
                monkey = new Monkey(new Vec2d(x, 0), Direction.RIGHT);
                break;
            }
        }

        // process instructions
        // System.out.println(monkey.position);
        for (String instruction : instructionList) {

            if ("L".equals(instruction) || "R".equals(instruction)) {
                monkey = new Monkey(monkey.position, directionRotate.get(monkey.facing).get(instruction));
            } else {
                monkey = moveMonkey(monkey, Integer.parseInt(instruction));
            }
            // System.out.println(instruction + " " + monkey.position);
        }

        System.out.println(monkey.position);

        // get position password
        int directionValue = 0;
        if (monkey.facing == Direction.UP) {
            directionValue = 3;
        } else if (monkey.facing == Direction.RIGHT) {
            directionValue = 0;
        } else if (monkey.facing == Direction.DOWN) {
            directionValue = 1;
        } else if (monkey.facing == Direction.LEFT) {
            directionValue = 2;
        }
        return 1000 * (monkey.position.y() + 1) + 4 * (monkey.position.x() + 1) + directionValue;
    }

    public Integer part2(String inputFilePath) throws IOException {
        parse(inputFilePath);

        return 0;
    }

    private void parse(String inputFilePath) throws IOException {
        val lines = fileReader.readTestLines(inputFilePath);
        val mapLines = lines.subList(0, lines.size() - 2);
        val width = mapLines.stream().map(String::length).max(Integer::compareTo).orElseThrow();

        this.grid = new Grid2d<Terrain>(width + 1, mapLines.size() + 1, Terrain.VOID);
        for (int y = 0; y < mapLines.size(); y++) {
            val line = mapLines.get(y);
            for (int x = 0; x < line.length(); x++) {
                String c = String.valueOf(line.charAt(x));
                Terrain t;
                // idk why I should do this
                if (" ".equals(c)) {
                    t = Terrain.VOID;
                } else if ("#".equals(c)) {
                    t = Terrain.ROCK;
                } else if (".".equals(c)) {
                    t = Terrain.GRASS;
                } else {
                    throw new IllegalStateException("Unexpected value: " + c);
                }
                grid.set(x, y, t);
            }
        }

        val instructions = Pattern.compile("(\\d+|[UDLR])");
        val strInstructions = lines.get(lines.size() - 1);
        val matcher = instructions.matcher(strInstructions);
        this.instructionList = new ArrayList<>();
        while (matcher.find()) {
            instructionList.add(matcher.group());
        }

    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    private record Monkey(Vec2d position, Direction facing) {
    }

    private enum Terrain {
        VOID(' '), ROCK('#'), GRASS('.');

        private char symbol;

        Terrain(char symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return String.valueOf(symbol);
        }
    }

    public Monkey moveMonkey(Monkey monkey, int amount) {
        var position = monkey.position;
        for (int i = 0; i < amount; i++) {
            var candidate = position.add(directionToOffset.get(monkey.facing));

            // if candidate is still in the bounds of the grid
            if (candidate.x() >= 0 && candidate.x() < grid.getWidth() && candidate.y() >= 0
                    && candidate.y() < grid.getHeight()) {
                var candidateTerrain = grid.get(candidate);
                if (candidateTerrain == Terrain.ROCK) {
                    return new Monkey(position, monkey.facing);
                }
                if (candidateTerrain == Terrain.GRASS) {
                    position = candidate;
                    continue;
                }
            }

            // now we wrap around
            Vec2d wrapCandidate = null;
            if (monkey.facing == Direction.UP) {
                wrapCandidate = new Vec2d(candidate.x(), grid.getHeight() - 1);
            } else if (monkey.facing == Direction.DOWN) {
                wrapCandidate = new Vec2d(candidate.x(), 0);
            } else if (monkey.facing == Direction.LEFT) {
                wrapCandidate = new Vec2d(grid.getWidth() - 1, candidate.y());
            } else if (monkey.facing == Direction.RIGHT) {
                wrapCandidate = new Vec2d(0, candidate.y());
            } else {
                throw new IllegalStateException("Unexpected value: " + monkey.facing);
            }
            // and we try to find land
            var wrapCandidateTerrain = grid.get(wrapCandidate);
            while (wrapCandidateTerrain == Terrain.VOID) {
                wrapCandidate = wrapCandidate.add(directionToOffset.get(monkey.facing));
                wrapCandidateTerrain = grid.get(wrapCandidate);
            }

            // we found land brothers
            if (wrapCandidateTerrain == Terrain.GRASS) {
                position = wrapCandidate;
                continue;
            }
            // we found rock, sad
            if (wrapCandidateTerrain == Terrain.ROCK) {
                return new Monkey(position, monkey.facing);
            }
        }
        return new Monkey(position, monkey.facing);
    }
}
