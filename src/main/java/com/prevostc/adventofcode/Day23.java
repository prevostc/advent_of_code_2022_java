package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.prevostc.utils.FileReader;
import com.prevostc.utils.Grid2d;
import com.prevostc.utils.Vec2d;

import lombok.val;

public class Day23 {

    FileReader fileReader = new FileReader();
    Deque<ProposalRule> proposalRules = new ArrayDeque<>();
    List<Vec2d> elfs = new ArrayList<>();
    Grid2d<Elem> grid;

    public Day23() {
        val dirN = new Vec2d(0, -1);
        val dirS = new Vec2d(0, 1);
        val dirE = new Vec2d(1, 0);
        val dirW = new Vec2d(-1, 0);
        val dirNE = dirN.add(dirE);
        val dirNW = dirN.add(dirW);
        val dirSE = dirS.add(dirE);
        val dirSW = dirS.add(dirW);

        proposalRules.add(new ProposalRule(List.of(dirN, dirNE, dirNW), dirN));
        proposalRules.add(new ProposalRule(List.of(dirS, dirSE, dirSW), dirS));
        proposalRules.add(new ProposalRule(List.of(dirW, dirNW, dirSW), dirW));
        proposalRules.add(new ProposalRule(List.of(dirE, dirNE, dirSE), dirE));
    }

    public Integer part1(String inputFilePath) throws IOException {
        init(inputFilePath);
        System.out.println(grid);

        for (int i = 0; i < 10; i++) {
            round();
        }

        // now count the empty ground tiles
        val minX = elfs.stream().map(Vec2d::x).min(Integer::compareTo).orElseThrow();
        val minY = elfs.stream().map(Vec2d::y).min(Integer::compareTo).orElseThrow();
        val maxX = elfs.stream().map(Vec2d::x).max(Integer::compareTo).orElseThrow();
        val maxY = elfs.stream().map(Vec2d::y).max(Integer::compareTo).orElseThrow();
        // val gridWidth = grid.bottomRight().x() - grid.topLeft().x() + 1;
        // val gridHeight = grid.bottomRight().y() - grid.topLeft().y() + 1;
        val gridWidth = maxX - minX + 1;
        val gridHeight = maxY - minY + 1;
        return (gridWidth * gridHeight) - elfs.size();
    }

    public Integer part2(String inputFilePath) throws IOException {
        init(inputFilePath);

        int i = 0;
        while (round()) {
            i++;
        }

        return i + 1;
    }

    private record ElfHandle(Vec2d elf, int elfIdx) {
    }

    private record ProposalRule(List<Vec2d> check, Vec2d go) {
    }

    private enum Elem {
        EMPTY('.'), ELF('#');

        private char symbol;

        Elem(char symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return String.valueOf(symbol);
        }
    }

    public void init(String inputFilePath) throws IOException {
        val lines = fileReader.readAllLines(inputFilePath);
        int offset = 10_000;
        this.grid = new Grid2d<Elem>(offset * 2, offset * 2, Elem.EMPTY);
        for (int y = 0; y < lines.size(); y++) {
            val line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == '#') {
                    val elf = new Vec2d(x + offset, y + offset);
                    grid.set(elf, Elem.ELF);
                    elfs.add(elf);
                }
            }
        }
    }

    private boolean round() {
        boolean hasMoved = false;
        // first half: all elfs propose smth
        Map<Vec2d /* proposed */, List<ElfHandle>> proposals = new HashMap<>();

        for (int elfIndex = 0; elfIndex < elfs.size(); elfIndex++) {
            val elf = elfs.get(elfIndex);
            // If no other Elves are in one of those eight positions, the Elf does not do
            // anything during this round.
            boolean hasNeighbor = false;
            for (val n : elf.getNeighborsAll()) {
                if (grid.get(n) == Elem.ELF) {
                    hasNeighbor = true;
                    break;
                }
            }
            if (!hasNeighbor) {
                continue;
            }

            for (val proposal : proposalRules) {
                boolean ruleOk = true;
                for (val check : proposal.check()) {
                    if (grid.get(elf.add(check)) != Elem.EMPTY) {
                        ruleOk = false;
                        break;
                    }
                }
                if (ruleOk) {
                    proposals.computeIfAbsent(elf.add(proposal.go()), t -> new ArrayList<>())
                            .add(new ElfHandle(elf, elfIndex));
                    break;
                }
            }
        }
        // second half: move if accepted
        for (val proposal : proposals.entrySet()) {
            if (proposal.getValue().size() == 1) {
                val elfHandle = proposal.getValue().get(0);
                val newPos = proposal.getKey();
                grid.set(elfHandle.elf(), Elem.EMPTY);
                grid.set(newPos, Elem.ELF);
                elfs.set(elfHandle.elfIdx(), newPos);
                hasMoved = true;
            }
        }

        // end of round: rotate proposal rules
        proposalRules.addLast(proposalRules.removeFirst());

        return hasMoved;
    }
}
