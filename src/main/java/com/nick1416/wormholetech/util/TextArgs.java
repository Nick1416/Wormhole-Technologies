package com.nick1416.wormholetech.util;

import com.nick1416.wormholetech.ModConfig;

/** Format arguments for lang lines whose numbers come from the config (tooltips and JEI info share them). */
public final class TextArgs {
    private TextArgs() {}

    private static String num(long v) { return String.format("%,d", v); }

    /** "10 minutes", "60 minutes", "45 seconds" at 20 TPS. */
    public static String duration(int ticks) {
        int s = Math.round(ticks / 20f);
        if (s >= 60) { int m = Math.round(s / 60f); return m + (m == 1 ? " minute" : " minutes"); }
        return s + (s == 1 ? " second" : " seconds");
    }

    public static Object[] args(String key) {
        switch (key) {
            case "tooltip.wormholetech.naquadah_generator.summary":
                return new Object[] { num(ModConfig.naquadahGeneratorRfPerTick), duration(ModConfig.naquadahGeneratorLifetimeTicks) };
            case "tooltip.wormholetech.naquadah_generator.detail.1":
                return new Object[] { num(ModConfig.naquadahGeneratorBuffer), num(ModConfig.naquadahGeneratorMaxExtract) };
            case "tooltip.wormholetech.naquadah_generator.detail.2":
                return new Object[] { num(ModConfig.naquadahGeneratorLifetimeTicks), duration(ModConfig.naquadahGeneratorLifetimeTicks) };
            case "tooltip.wormholetech.relativistic_computer.detail.1":
                return new Object[] { num(ModConfig.chronoFieldRfPerBlockPerTick), String.valueOf(ModConfig.chronoFieldExtraTicks + 1) };
            default:
                return new Object[0];
        }
    }
}
