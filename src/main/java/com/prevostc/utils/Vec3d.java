package com.prevostc.utils;

import java.util.List;

public record Vec3d(int x, int y, int z) {
    public List<Vec3d> getNeighborsCardinal() {
        return List.of(
                new Vec3d(x + 0, y + 0, z + 1),
                new Vec3d(x + 0, y + 0, z + -1),
                new Vec3d(x + 0, y + 1, z + 0),
                new Vec3d(x + 0, y + -1, z + 0),
                new Vec3d(x + 1, y + 0, z + 0),
                new Vec3d(x + -1, y + 0, z + 0));
    }
}
