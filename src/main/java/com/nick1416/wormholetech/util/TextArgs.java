package com.nick1416.wormholetech.util;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.pressurizer.PressurizerRecipe;
import com.nick1416.wormholetech.tile.RefinerRecipes;

/**
 * Format arguments for lang lines whose numbers come from the config or from recipe constants. Tooltips, JEI
 * info pages, the Ancient Tablet and the Repository all format through {@link #format}, so every number the
 * player reads matches the live config.
 */
public final class TextArgs {
    private TextArgs() {}

    /** Full number with thousands separators: 72,000. */
    public static String num(long v) { return String.format("%,d", v); }

    /** Compact RF amount: 500, 1k, 1.5k, 100k, 2M. */
    public static String rf(long v) {
        if (v >= 1_000_000) return trim(v / 1_000_000.0) + "M";
        if (v >= 1_000) return trim(v / 1_000.0) + "k";
        return Long.toString(v);
    }

    /** Multiplier without trailing zeros: 2x, 1.2x, 0.85x. */
    public static String mult(double v) { return trim(v) + "x"; }

    private static String trim(double v) {
        String s = String.format(java.util.Locale.ROOT, "%.2f", v);
        s = s.replaceAll("0+$", "");
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }

    /** Duration at 20 TPS: 45 s, 2 min, 1 min 30 s, 60 min, 2 h, 2 h 30 min (minutes up to 90). */
    public static String duration(long ticks) {
        long s = Math.round(ticks / 20.0);
        if (s < 60) return s + " s";
        long m = s / 60, rs = s % 60;
        if (m < 10 && rs != 0) return m + " min " + rs + " s";
        if (rs >= 30) m++;
        if (m <= 90) return m + " min";
        long h = m / 60, rm = m % 60;
        return rm == 0 ? h + " h" : h + " h " + rm + " min";
    }

    private static RefinerRecipes.Recipe quartz() {
        for (RefinerRecipes.Recipe r : RefinerRecipes.all()) if (r.fluidMb > 0) return r;
        return null;
    }

    /** I18n.format with the config-driven arguments for that key. */
    public static String format(String key) {
        return net.minecraft.client.resources.I18n.format(key, args(key));
    }

    public static Object[] args(String key) {
        switch (key) {
            // Naquadah Generator
            case "tooltip.wormholetech.naquadah_generator.summary":
            case "jei.wormholetech.naquadah_generator.info":
                return new Object[] { rf(ModConfig.naquadahGeneratorRfPerTick), duration(ModConfig.naquadahGeneratorLifetimeTicks) };
            case "tooltip.wormholetech.naquadah_generator.detail.1":
                return new Object[] { rf(ModConfig.naquadahGeneratorBuffer), rf(ModConfig.naquadahGeneratorMaxExtract) };
            case "tooltip.wormholetech.naquadah_generator.detail.2":
                return new Object[] { num(ModConfig.naquadahGeneratorLifetimeTicks), duration(ModConfig.naquadahGeneratorLifetimeTicks) };
            case "gui.wormholetech.tablet.p3.l2":
                return new Object[] { rf(ModConfig.naquadahGeneratorRfPerTick), duration(ModConfig.naquadahGeneratorLifetimeTicks),
                        rf(ModConfig.stableConverterRfPerTick) };
            // Wormhole Energy Converter
            case "tooltip.wormholetech.rf_generator.summary":
                return new Object[] { rf(ModConfig.stableConverterRfPerTick) };
            // Chrono Field Generator
            case "tooltip.wormholetech.relativistic_computer.detail.1":
                return new Object[] { rf(ModConfig.chronoFieldRfPerBlockPerTick), mult(ModConfig.chronoFieldExtraTicks + 1) };
            case "tooltip.wormholetech.relativistic_computer.detail.2":
                return new Object[] { duration(ModConfig.aetheriusDecayTicks) };
            case "gui.wormholetech.tablet.p10.l2":
                return new Object[] { rf(ModConfig.chronoFieldRfPerBlockPerTick), mult(ModConfig.chronoFieldExtraTicks + 1),
                        duration(ModConfig.aetheriusDecayTicks) };
            // Aetherius / Gate Network Computer
            case "tooltip.wormholetech.aetherius.detail.1":
            case "tooltip.wormholetech.gate_network_computer.detail.1":
            case "gui.wormholetech.tablet.p10.l1":
                return new Object[] { duration(ModConfig.aetheriusDecayTicks) };
            case "gui.wormholetech.tablet.p6.l2":
                return new Object[] { duration(ModConfig.aetheriusDecayTicks) };
            case "gui.wormholetech.tablet.p6.l1":
                return new Object[] { duration(ModConfig.highEnergyRefinerCookTimeTicks) };
            // Refined Naquadah fuel
            case "tooltip.wormholetech.refined_naquadah_block.summary":
            case "gui.wormholetech.tablet.p4.l2":
                return new Object[] { duration(ModConfig.refinedNaquadahBlockDecayTicks) };
            // Pads
            case "tooltip.wormholetech.wormhole_pad.summary":
                return new Object[] { rf(ModConfig.wormholePadUpkeepRfPerTick) };
            // Drones
            case "tooltip.wormholetech.drone_modulator":
                return new Object[] { duration(ModConfig.droneChargeTicks) };
            // Pressurizer
            case "tooltip.wormholetech.hydrothermal_pressurizer.detail.1":
                return new Object[] { PressurizerRecipe.WATER_PER_EMERALD / 1000, PressurizerRecipe.COAL_PER_EMERALD, duration(PressurizerRecipe.TICKS) };
            case "jei.wormholetech.pressurizer.hint":
            case "gui.wormholetech.tablet.p9.l2":
                return new Object[] { duration(PressurizerRecipe.TICKS) };
            // Hydrothermal Fluid -> quartz
            case "jei.wormholetech.hydrothermal_fluid.info": {
                RefinerRecipes.Recipe q = quartz();
                if (q == null) return new Object[] { 1, 1, 4, duration(200) };
                return new Object[] { trim(q.fluidMb / 1000.0), 1, q.outputCount, duration(q.ticks() / Math.max(1, q.outputCount)) };
            }
            // Planet gravity (Repository)
            case "gui.wormholetech.repository.info.utopis.gravity":
                return new Object[] { mult(ModConfig.utopisGravity) };
            case "gui.wormholetech.repository.info.tal_vora.gravity":
                return new Object[] { mult(ModConfig.talVoraGravity) };
            case "gui.wormholetech.repository.info.sel_vora.gravity":
                return new Object[] { mult(ModConfig.selVoraGravity) };
            default:
                return new Object[0];
        }
    }
}
