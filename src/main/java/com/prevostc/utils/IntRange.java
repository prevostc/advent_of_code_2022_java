package com.prevostc.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.val;

/**
 * Inclusive on both sides
 */
public record IntRange(int min, int max) {

    public boolean contains(int value) {
        return min <= value && max >= value;
    }

    public boolean intersects(IntRange other) {
        return this.contains(other.min) || this.contains(other.max) || other.contains(min) || other.contains(max);
    }

    public static IntRange[] merge(IntRange a, IntRange b) {
        if (!a.intersects(b)) {
            return new IntRange[] { a, b };
        }
        return new IntRange[] { new IntRange(Math.min(a.min, b.min), Math.max(a.max, b.max)) };
    }

    public static List<IntRange> merge(List<IntRange> ranges) {
        if (ranges.size() < 2) {
            return ranges;
        }

        List<IntRange> merged = new ArrayList<>();
        val sorted = new ArrayList<>(ranges);
        sorted.sort((a, b) -> Integer.compare(a.min, b.min));

        IntRange acc = sorted.get(0);
        for (int i = 1; i < sorted.size(); i++) {
            val current = sorted.get(i);
            if (acc.intersects(current)) {
                acc = new IntRange(Math.min(acc.min, current.min), Math.max(acc.max, current.max));
            } else {
                merged.add(acc);
                acc = current;
            }
        }
        merged.add(acc);
        return merged;
    }
}
