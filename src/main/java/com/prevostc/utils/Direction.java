package com.prevostc.utils;

import java.util.Map;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    private static final Map<Direction, Vec2d> vec2dMap = Map.of(
            Direction.UP, new Vec2d(0, -1),
            Direction.DOWN, new Vec2d(0, 1),
            Direction.LEFT, new Vec2d(-1, 0),
            Direction.RIGHT, new Vec2d(1, 0));

    public Vec2d toVec2d() {
        return vec2dMap.get(this);
    }

    public Direction rotateClockwise() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    public Direction rotateCounterClockwise() {
        return switch (this) {
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
            case RIGHT -> UP;
        };
    }

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
