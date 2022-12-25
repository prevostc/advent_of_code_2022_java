package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.prevostc.utils.Direction;
import com.prevostc.utils.FileReader;
import com.prevostc.utils.Grid2d;
import com.prevostc.utils.Vec2d;

import lombok.val;

public class Day22 {

    private Grid2d<Terrain> grid;
    private List<String> instructionList;
    private Monkey monkey;

    FileReader fileReader = new FileReader();

    public record Portal(int fromFace, Direction exitDir, int toFace, Direction entryDir) {
    }

    public record Face(int face, Vec2d pos) {
        public boolean contains(Vec2d diceFaceSize, Vec2d pos) {
            val bottomLeft = this.pos.add(diceFaceSize);
            return pos.x() >= this.pos.x() && pos.x() < bottomLeft.x() && pos.y() >= this.pos.y()
                    && pos.y() < bottomLeft.y();
        }
    }

    private final int DIE_GRID_MAX;
    private final Vec2d DICE_FACE_SIZE;
    private final List<Portal> dicePortals;
    private final List<Face> diceFaces;

    public Day22(int diceFaceSize, List<Face> diceFaces, List<Portal> dicePortals) {
        this.DICE_FACE_SIZE = new Vec2d(diceFaceSize, diceFaceSize);
        this.DIE_GRID_MAX = diceFaceSize - 1;
        this.diceFaces = diceFaces.stream().map(f -> new Face(f.face, f.pos.mul(this.DICE_FACE_SIZE)))
                .toList();
        this.dicePortals = dicePortals;
    }

    public Integer part1(String inputFilePath) throws IOException {
        parse(inputFilePath);

        // process instructions
        for (String instruction : instructionList) {
            if ("L".equals(instruction)) {
                monkey = new Monkey(monkey.position, monkey.facing.rotateCounterClockwise());
            } else if ("R".equals(instruction)) {
                monkey = new Monkey(monkey.position, monkey.facing.rotateClockwise());
            } else {
                monkey = moveMonkey(monkey, Integer.parseInt(instruction));
            }
        }

        return getPassword(monkey);
    }

    public Integer part2(String inputFilePath) throws IOException {
        parse(inputFilePath);
        // process instructions
        for (String instruction : instructionList) {
            if ("L".equals(instruction)) {
                monkey = new Monkey(monkey.position, monkey.facing.rotateCounterClockwise());
            } else if ("R".equals(instruction)) {
                monkey = new Monkey(monkey.position, monkey.facing.rotateClockwise());
            } else {
                monkey = moveMonkeyDice(monkey, Integer.parseInt(instruction));
            }
        }

        return getPassword(monkey);
    }

    private int getPassword(Monkey monkey) {
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

    public Vec2d diceEdgePosRotateClockwise(Vec2d pos) {
        if (pos.y() == 0) {
            return new Vec2d(DIE_GRID_MAX, pos.x());
        } else if (pos.x() == 0) {
            return new Vec2d(DIE_GRID_MAX - pos.y(), 0);
        } else if (pos.x() == DIE_GRID_MAX) {
            return new Vec2d(DIE_GRID_MAX - pos.y(), DIE_GRID_MAX);
        } else if (pos.y() == DIE_GRID_MAX) {
            return new Vec2d(0, pos.x());
        } else {
            throw new IllegalArgumentException("pos must be on the edge of the die");
        }
    }

    public Vec2d diceEdgePosOpposite(Vec2d pos) {
        if (pos.y() == 0) {
            return new Vec2d(pos.x(), DIE_GRID_MAX);
        } else if (pos.x() == 0) {
            return new Vec2d(DIE_GRID_MAX, pos.y());
        } else if (pos.x() == DIE_GRID_MAX) {
            return new Vec2d(0, pos.y());
        } else if (pos.y() == DIE_GRID_MAX) {
            return new Vec2d(pos.x(), 0);
        } else {
            throw new IllegalArgumentException("pos must be on the edge of the die");
        }
    }

    public Monkey moveMonkeyDice(Monkey monkey, int amount) {
        for (int i = 0; i < amount; i++) {
            var candidate = monkey.position.add(monkey.facing.toVec2d());

            // if candidate is still in the bounds of the grid
            if (candidate.x() >= 0 && candidate.x() < grid.getWidth() && candidate.y() >= 0
                    && candidate.y() < grid.getHeight()) {
                var candidateTerrain = grid.get(candidate);
                if (candidateTerrain == Terrain.ROCK) {
                    return monkey;
                }
                if (candidateTerrain == Terrain.GRASS) {
                    monkey = new Monkey(candidate, monkey.facing);
                    continue;
                }
            }

            // now we wrap around
            Vec2d wrapCandidate = null;

            val pos = monkey.position; // java needs final variables for lambdas
            val fac = monkey.facing; // java needs final variables for lambdas

            // find out on which face we are
            Face currentFace = diceFaces.stream().filter(f -> f.contains(DICE_FACE_SIZE, pos)).findFirst()
                    .orElseThrow();

            // find out which portal we should be using
            val portal = dicePortals.stream()
                    .filter(p -> p.fromFace == currentFace.face && p.exitDir == fac).findFirst()
                    .orElseThrow();

            Face exitFace = diceFaces.stream().filter(f -> f.face == portal.toFace).findFirst().orElseThrow();

            // now map the position to the new face
            // go in the face coordinates system [(0,0);(50,50)]
            Vec2d facePos = monkey.position.sub(currentFace.pos);

            // rotate the face coordinates system
            Direction dir = portal.exitDir;
            while (dir != portal.entryDir) {
                facePos = diceEdgePosRotateClockwise(facePos);
                dir = dir.rotateClockwise();
            }
            // go to the opposite edge (because we are going out of the face)
            facePos = diceEdgePosOpposite(facePos);

            // go back to the grid coordinates system
            wrapCandidate = facePos.add(exitFace.pos);

            // and we try to find land
            var wrapCandidateTerrain = grid.get(wrapCandidate);
            while (wrapCandidateTerrain == Terrain.VOID) {
                throw new IllegalArgumentException("we should never be here");
            }

            // we found land brothers
            if (wrapCandidateTerrain == Terrain.GRASS) {
                monkey = new Monkey(wrapCandidate, dir);
                continue;
            }
            // we found rock, sad
            if (wrapCandidateTerrain == Terrain.ROCK) {
                return monkey;
            }
        }

        return monkey;
    }

    public Monkey moveMonkey(Monkey monkey, int amount) {
        var position = monkey.position;
        for (int i = 0; i < amount; i++) {
            var candidate = position.add(monkey.facing.toVec2d());

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
                wrapCandidate = wrapCandidate.add(monkey.facing.toVec2d());
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

    private void parse(String inputFilePath) throws IOException {
        val lines = fileReader.readAllLines(inputFilePath);
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

        // parse the instructions
        val instructions = Pattern.compile("(\\d+|[UDLR])");
        val strInstructions = lines.get(lines.size() - 1);
        val matcher = instructions.matcher(strInstructions);
        this.instructionList = new ArrayList<>();
        while (matcher.find()) {
            instructionList.add(matcher.group());
        }

        // find the leftmost grass node of the top row
        for (int x = 0; x < grid.getWidth(); x++) {
            if (grid.get(x, 0) == Terrain.GRASS) {
                monkey = new Monkey(new Vec2d(x, 0), Direction.RIGHT);
                break;
            }
        }
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

}
