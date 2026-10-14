package com.nick1416.wormholetech.knowledge;

import com.nick1416.wormholetech.ModConfig;

/** Worlds the Repository of Knowledge can study, in display order. */
public final class Planets {
    private Planets() {}
    public static final String[] KEYS = { "overworld", "p3x_1416", "derelict_vessel", "tal_vora" };

    public static int dim(int i) {
        switch (i) {
            case 0: return 0;
            case 1: return ModConfig.p3xDimensionId;
            case 2: return ModConfig.derelictDimensionId;
            default: return ModConfig.talVoraDimensionId;
        }
    }

    /** Index into {@link #KEYS} for a dimension id, or -1. */
    public static int indexOf(int dim) {
        for (int i = 0; i < KEYS.length; i++) if (dim(i) == dim) return i;
        return -1;
    }
}
