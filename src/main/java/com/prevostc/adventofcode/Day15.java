package com.prevostc.adventofcode;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.prevostc.utils.FileReader;
import com.prevostc.utils.IntRange;
import com.prevostc.utils.Vec2d;

import lombok.val;

public class Day15 {

    FileReader fileReader = new FileReader();

    private record Sensor(Vec2d center, int reach) {
        public boolean contains(Vec2d point) {
            return center.manhattanDistance(point) <= reach;
        }

        Sensor(Vec2d center, Vec2d point) {
            this(center, center.manhattanDistance(point));
        }

        public IntRange xRangeAt(int y) {
            if (y < center.y() - reach || y > center.y() + reach) {
                throw new InvalidParameterException("y is outside of reach");
            }
            int yDiff = Math.abs(center.y() - y);
            int xDiff = reach - yDiff;
            return new IntRange(center.x() - xDiff, center.x() + xDiff);
        }
    }

    public Integer part1(String inputFilePath, int targetY) throws IOException {
        val sensors = getSensors(inputFilePath);
        val beacons = getBeacons(inputFilePath);

        // find the X range to cover
        val minX = sensors.stream().mapToInt(s -> s.center.x() - s.reach).min().orElseThrow();
        val maxX = sensors.stream().mapToInt(s -> s.center.x() + s.reach).max().orElseThrow();

        int totalInReach = 0;
        for (int x = minX; x <= maxX; x++) {
            val consider = new Vec2d(x, targetY);
            boolean isInReach = sensors.stream().anyMatch(s -> s.contains(consider));
            if (isInReach) {
                boolean isBeacon = beacons.stream().anyMatch(b -> b.equals(consider));
                if (!isBeacon) {
                    totalInReach++;
                }
            }
        }

        return totalInReach;
    }

    public BigInteger part2(String inputFilePath, int minXY, int maxXY) throws IOException {
        val sensors = getSensors(inputFilePath);

        for (int y = minXY; y <= maxXY; y++) {
            val streamY = y; // final to make streams happy
            val xRanges = sensors.stream()
                    .filter(s -> streamY >= s.center.y() - s.reach && streamY <= s.center.y() + s.reach)
                    .map(s -> s.xRangeAt(streamY))
                    .toList();
            val mergedRanges = IntRange.merge(xRanges);
            mergedRanges.sort((a, b) -> Integer.compare(a.min(), b.min()));

            for (int i = 0; i < mergedRanges.size() - 1; i++) {
                val a = mergedRanges.get(i);
                val b = mergedRanges.get(i + 1);

                if (!a.intersects(b)) {
                    val consider = new Vec2d(a.max() + 1, y);
                    if (consider.x() <= maxXY && consider.y() <= maxXY && consider.x() >= minXY
                            && consider.y() >= minXY) {
                        return BigInteger.valueOf(consider.x()).multiply(BigInteger.valueOf(4_000_000))
                                .add(BigInteger.valueOf(consider.y()));
                    }
                }
            }
        }
        return BigInteger.ZERO;
    }

    private final static Pattern INPUT_PATTERN = Pattern
            .compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)");

    public List<Sensor> getSensors(String inputFilePath) throws IOException {
        return fileReader.readTestLines(inputFilePath).stream().filter(Predicate.not(String::isEmpty))
                .map(INPUT_PATTERN::matcher)
                .filter(Matcher::matches)
                .map(m -> new Sensor(new Vec2d(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
                        new Vec2d(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)))))
                .toList();
    }

    public List<Vec2d> getBeacons(String inputFilePath) throws IOException {
        return fileReader.readTestLines(inputFilePath).stream().filter(Predicate.not(String::isEmpty))
                .map(INPUT_PATTERN::matcher)
                .filter(Matcher::matches)
                .map(m -> new Vec2d(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))))
                .toList();
    }

}
