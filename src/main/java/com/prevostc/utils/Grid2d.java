package com.prevostc.utils;

import lombok.Getter;
import lombok.val;

public class Grid2d<T> {
    @Getter
    private int width;
    @Getter
    private int height;
    private T[] data;
    private Vec2d[] printScope;
    private T empty;

    public Grid2d(int width, int height, T empty) {
        this.width = width;
        this.height = height;
        data = (T[]) new Object[width * height];
        this.empty = empty;
        printScope = new Vec2d[] { new Vec2d(Integer.MAX_VALUE, Integer.MAX_VALUE),
                new Vec2d(Integer.MIN_VALUE, Integer.MIN_VALUE) };
    }

    public Grid2d<T> set(Vec2d p, T value) {
        data[p.y() * width + p.x()] = value;
        printScope[0] = p.min(printScope[0]);
        printScope[1] = p.max(printScope[1]);
        return this;
    }

    public T get(Vec2d p) {
        T d = data[p.y() * width + p.x()];
        if (d == null) {
            return empty;
        } else {
            return d;
        }
    }

    public boolean hasValue(Vec2d p) {
        return data[p.y() * width + p.x()] != null;
    }

    public Grid2d<T> set(int x, int y, T value) {
        return set(new Vec2d(x, y), value);
    }

    public T get(int x, int y) {
        return get(new Vec2d(x, y));
    }

    public Vec2d topLeft() {
        return printScope[0];
    }

    public Vec2d bottomRight() {
        return printScope[1];
    }

    public boolean is(Vec2d p, T value) {
        return value.equals(get(p));
    }

    public boolean is(int x, int y, T value) {
        return value.equals(get(x, y));
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("========================================").append("\n");
        sb.append("Grid2d (").append(width).append("x").append(height).append(')').append("\n");
        for (int y = this.topLeft().y(); y <= this.bottomRight().y(); y++) {
            for (int x = this.topLeft().x(); x <= this.bottomRight().x(); x++) {
                var value = this.get(x, y);
                if (value == null) {
                    sb.append(this.empty);
                } else {
                    sb.append(value);
                }
            }
            sb.append('\n');
        }
        sb.append("========================================");
        return sb.toString();
    }
}
