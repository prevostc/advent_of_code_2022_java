package com.prevostc.adventofcode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.prevostc.utils.FileReader;
import com.prevostc.utils.Vec3d;

public class Day18 {

    FileReader fileReader = new FileReader();

    private Set<Vec3d> parse(String inputFilePath) throws IOException {
        return fileReader.readAllLines(inputFilePath).stream()
                .map(s -> s.split(","))
                .map(p -> new Vec3d(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])))
                .collect(Collectors.toSet());
    }

    public Integer part1(String inputFilePath) throws IOException {
        Set<Vec3d> cubes = parse(inputFilePath);
        int total = 0;
        for (Vec3d cube : cubes) {
            for (Vec3d neigh : cube.getNeighborsCardinal()) {
                if (!cubes.contains(neigh)) {
                    total++;
                }
            }
        }
        return total;
    }

    public Integer part2(String inputFilePath) throws IOException {
        Set<Vec3d> cubes = parse(inputFilePath);
        Vec3d minBounds = new Vec3d(
                cubes.stream().map(Vec3d::x).min(Integer::compare).orElse(0) - 1,
                cubes.stream().map(Vec3d::y).min(Integer::compare).orElse(0) - 1,
                cubes.stream().map(Vec3d::z).min(Integer::compare).orElse(0) - 1);
        Vec3d maxBounds = new Vec3d(
                cubes.stream().map(Vec3d::x).max(Integer::compare).orElse(0) + 1,
                cubes.stream().map(Vec3d::y).max(Integer::compare).orElse(0) + 1,
                cubes.stream().map(Vec3d::z).max(Integer::compare).orElse(0) + 1);

        Set<Vec3d> visited = new HashSet<>(cubes);

        Set<Vec3d> current = new HashSet<>();
        current.add(minBounds);

        int maxIter = 100_000;
        while (maxIter-- > 0) {
            visited.addAll(current);
            Set<Vec3d> next = current.stream()
                    .flatMap(c -> c.getNeighborsCardinal().stream())
                    // exclude out of bounds
                    .filter(c -> c.x() >= minBounds.x() && c.x() <= maxBounds.x()
                            && c.y() >= minBounds.y() && c.y() <= maxBounds.y()
                            && c.z() >= minBounds.z() && c.z() <= maxBounds.z())
                    // exclude visited
                    .filter(c -> !visited.contains(c))
                    .collect(Collectors.toSet());

            if (next.isEmpty()) {
                break;
            }
            current = next;
        }

        int total = 0;
        for (Vec3d cube : cubes) {
            for (Vec3d neigh : cube.getNeighborsCardinal()) {
                if (!cubes.contains(neigh) && visited.contains(neigh)) {
                    total++;
                }
            }
        }
        return total;
    }

}
