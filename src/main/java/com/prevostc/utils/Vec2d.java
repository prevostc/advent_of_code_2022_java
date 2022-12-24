package com.prevostc.utils;

import java.util.List;
import java.util.stream.IntStream;

import lombok.val;

public record Vec2d(int x, int y) implements Cloneable {

    public static Vec2d fromString(String coords, String sep) {
        val coordsArray = coords.split(sep);
        return new Vec2d(Integer.parseInt(coordsArray[0]), Integer.parseInt(coordsArray[1]));
    }

    public static Vec2d fromString(String coords) {
        return fromString(coords, ",");
    }

    public Vec2d wrap(int width, int height) {
        return new Vec2d(x % width, y % height);
    }

    public Vec2d negate() {
        return new Vec2d(-x, -y);
    }

    public Vec2d add(Vec2d other) {
        return new Vec2d(x + other.x, y + other.y);
    }

    public Vec2d max(Vec2d other) {
        return new Vec2d(Math.max(x, other.x), Math.max(y, other.y));
    }

    public Vec2d min(Vec2d other) {
        return new Vec2d(Math.min(x, other.x), Math.min(y, other.y));
    }

    public Vec2d down() {
        return new Vec2d(x, y + 1);
    }

    public Vec2d up() {
        return new Vec2d(x, y - 1);
    }

    public Vec2d left() {
        return new Vec2d(x - 1, y);
    }

    public Vec2d right() {
        return new Vec2d(x + 1, y);
    }

    public Vec2d downLeft() {
        return new Vec2d(x - 1, y + 1);
    }

    public Vec2d downRight() {
        return new Vec2d(x + 1, y + 1);
    }

    public List<Vec2d> getNeighborsCardinal() {
        return List.of(new Vec2d(-1, 0), new Vec2d(1, 0), new Vec2d(0, -1), new Vec2d(0, 1)).stream().map(this::add)
                .toList();
    }

    public List<Vec2d> getNeighborsAll() {
        return List.of(new Vec2d(-1, 0), new Vec2d(1, 0), new Vec2d(0, -1), new Vec2d(0, 1), new Vec2d(-1, -1),
                new Vec2d(1, -1), new Vec2d(-1, 1), new Vec2d(1, 1)).stream().map(this::add).toList();
    }

    public List<Vec2d> getNeighborsDiagonal() {
        return List.of(new Vec2d(-1, -1), new Vec2d(1, -1), new Vec2d(-1, 1), new Vec2d(1, 1)).stream().map(this::add)
                .toList();
    }

    public List<Vec2d> straightTo(Vec2d other) {
        if (other.x == this.x) {
            return IntStream.rangeClosed(Math.min(other.y, this.y), Math.max(other.y, this.y)).boxed()
                    .map(y -> new Vec2d(this.x, y)).toList();
        } else if (other.y == this.y) {
            return IntStream.rangeClosed(Math.min(other.x, this.x), Math.max(other.x, this.x)).boxed()
                    .map(x -> new Vec2d(x, this.y)).toList();
        } else {
            throw new IllegalArgumentException("No straight line to " + other + " from " + this);
        }
    }

    public int manhattanDistance(Vec2d other) {
        return Math.abs(other.x - this.x) + Math.abs(other.y - this.y);
    }
}