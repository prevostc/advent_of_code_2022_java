package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.prevostc.utils.FileReader;
import com.prevostc.utils.Grid2d;
import com.prevostc.utils.Vec2d;

import lombok.val;

public class Day14 {

    FileReader fileReader = new FileReader();

    private enum Terrain {
        EMPTY('.'), ROCK('#'), SAND('o');

        private char symbol;

        Terrain(char symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return String.valueOf(symbol);
        }
    }

    public Integer part1(String inputFilePath) throws IOException {
        val grid = parse(inputFilePath);

        // pour sand
        int abyssY = grid.bottomRight().down().y();
        Vec2d pourFrom = new Vec2d(500, 0);
        int totalPour = 0;
        while (totalPour < 1_000_000 && pourSand(grid, pourFrom, abyssY)) {
            totalPour++;
        }

        return totalPour;
    }

    public Integer part2(String inputFilePath) throws IOException {
        val grid = parse(inputFilePath);

        // just add a fake floor to the grid
        Vec2d pourFrom = new Vec2d(500, 0);
        int lowestPoint = grid.bottomRight().y() + 2;
        int yDist = lowestPoint - pourFrom.y();
        for (int x = pourFrom.x() - yDist; x <= pourFrom.x() + yDist; x++) {
            grid.set(new Vec2d(x, lowestPoint), Terrain.ROCK);
        }

        // pour sand
        int totalPour = 0;
        while (totalPour < 1_000_000 && pourSand(grid, pourFrom, lowestPoint + 1)) {
            totalPour++;
        }

        return totalPour;
    }

    public Grid2d<Terrain> parse(String inputFilePath) throws IOException {
        val grid = new Grid2d<Terrain>(1000, 1000, Terrain.EMPTY);
        val lines = fileReader.readAllLines(inputFilePath);

        // parse input into a grid
        for (val line : lines) {
            val coords = Arrays.stream(line.split(" -> ")).map(Vec2d::fromString).toList();
            for (int i = 0; i < coords.size() - 1; i++) {
                val start = coords.get(i);
                val end = coords.get(i + 1);
                for (val c : start.straightTo(end)) {
                    grid.set(c, Terrain.ROCK);
                }
            }
        }
        return grid;
    }

    /**
     * @return true if we reached equilibrium, false if we reached the abyss
     */
    public boolean pourSand(Grid2d<Terrain> grid, Vec2d from, int abyssY) {
        if (grid.is(from, Terrain.SAND)) {
            // sand pouring overflowed
            return false;
        }

        grid.set(from, Terrain.SAND);

        while (from.y() < abyssY) {
            val tries = List.of(from.down(), from.downLeft(), from.downRight());
            boolean moved = false;
            for (val t : tries) {
                if (grid.is(t, Terrain.EMPTY)) {
                    grid.set(t, Terrain.SAND);
                    grid.set(from, Terrain.EMPTY);
                    from = t;
                    moved = true;
                    break;
                }
            }

            if (!moved) {
                // we reached equilibrium
                return true;
            }
        }
        // we reach the abyss, so we're done
        return false;
    }
}
