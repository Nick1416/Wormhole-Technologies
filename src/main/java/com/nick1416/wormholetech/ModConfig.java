package com.nick1416.wormholetech;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Forge Configuration for RF rates, lifetimes, and energy costs.
 * Defaults match the pre-0.1.0 hardcoded values (no balance nerfs).
 */
public final class ModConfig {
    private ModConfig() {}

    // Naquadah Generator
    public static int naquadahGeneratorRfPerTick = 5000;
    public static int naquadahGeneratorBuffer = 500_000;
    public static int naquadahGeneratorMaxExtract = 5000;
    public static int naquadahGeneratorLifetimeTicks = 12_000;

    // Unstable Wormhole Energy Converter
    public static int unstableConverterRfPerTick = 10_000;
    public static int unstableConverterBuffer = 1_000_000;
    public static int unstableConverterMaxExtract = 10_000;
    public static int unstableConverterLifetimeTicks = 72_000;

    // Stable Wormhole Energy Converter (GeneratorTileEntity)
    public static int stableConverterRfPerTick = 100_000;
    public static int stableConverterBuffer = 10_000_000;
    public static int stableConverterMaxExtract = 100_000;

    // Relativistic Computer
    public static int relativisticComputerUpkeepRfPerTick = 50_000;
    public static int relativisticComputerBuffer = 2_000_000;
    public static int relativisticComputerMaxReceive = 100_000;

    // High Energy Refiner (Aetherius)
    public static int highEnergyRefinerRfPerTick = 100;
    public static int highEnergyRefinerCookTimeTicks = 72_000;
    public static int highEnergyRefinerBuffer = 1_000_000;
    public static int highEnergyRefinerMaxReceive = 10_000;

    // Wormhole Pad
    public static int wormholePadUpkeepRfPerTick = 50_000;
    public static int wormholePadBuffer = 2_000_000;
    public static int wormholePadMaxReceive = 100_000;
    public static int wormholePadTeleportCooldownTicks = 60;

    public static void load(File file) {
        Configuration cfg = new Configuration(file);
        cfg.load();

        final String gen = "generators";
        naquadahGeneratorRfPerTick = cfg.getInt("naquadahGeneratorRfPerTick", gen, 5000, 0, Integer.MAX_VALUE,
                "Naquadah Generator RF generated per tick");
        naquadahGeneratorBuffer = cfg.getInt("naquadahGeneratorBuffer", gen, 500_000, 1, Integer.MAX_VALUE,
                "Naquadah Generator internal RF buffer");
        naquadahGeneratorMaxExtract = cfg.getInt("naquadahGeneratorMaxExtract", gen, 5000, 0, Integer.MAX_VALUE,
                "Naquadah Generator max RF extract per tick");
        naquadahGeneratorLifetimeTicks = cfg.getInt("naquadahGeneratorLifetimeTicks", gen, 12_000, 1, Integer.MAX_VALUE,
                "Naquadah Generator lifetime in ticks (~10 min at 20 TPS)");

        unstableConverterRfPerTick = cfg.getInt("unstableConverterRfPerTick", gen, 10_000, 0, Integer.MAX_VALUE,
                "Unstable Wormhole Energy Converter RF per tick");
        unstableConverterBuffer = cfg.getInt("unstableConverterBuffer", gen, 1_000_000, 1, Integer.MAX_VALUE,
                "Unstable Converter internal RF buffer");
        unstableConverterMaxExtract = cfg.getInt("unstableConverterMaxExtract", gen, 10_000, 0, Integer.MAX_VALUE,
                "Unstable Converter max RF extract per tick");
        unstableConverterLifetimeTicks = cfg.getInt("unstableConverterLifetimeTicks", gen, 72_000, 1, Integer.MAX_VALUE,
                "Unstable Converter lifetime in ticks (~60 min at 20 TPS)");

        stableConverterRfPerTick = cfg.getInt("stableConverterRfPerTick", gen, 100_000, 0, Integer.MAX_VALUE,
                "Wormhole Energy Converter (stable) RF per tick");
        stableConverterBuffer = cfg.getInt("stableConverterBuffer", gen, 10_000_000, 1, Integer.MAX_VALUE,
                "Stable Converter internal RF buffer");
        stableConverterMaxExtract = cfg.getInt("stableConverterMaxExtract", gen, 100_000, 0, Integer.MAX_VALUE,
                "Stable Converter max RF extract per tick");

        final String late = "late_game";
        relativisticComputerUpkeepRfPerTick = cfg.getInt("relativisticComputerUpkeepRfPerTick", late, 50_000, 0, Integer.MAX_VALUE,
                "Relativistic Computer RF upkeep per tick while active");
        relativisticComputerBuffer = cfg.getInt("relativisticComputerBuffer", late, 2_000_000, 1, Integer.MAX_VALUE,
                "Relativistic Computer RF buffer");
        relativisticComputerMaxReceive = cfg.getInt("relativisticComputerMaxReceive", late, 100_000, 0, Integer.MAX_VALUE,
                "Relativistic Computer max RF receive per tick");

        highEnergyRefinerRfPerTick = cfg.getInt("highEnergyRefinerRfPerTick", late, 100, 0, Integer.MAX_VALUE,
                "High Energy Refiner RF consumed per tick while refining");
        highEnergyRefinerCookTimeTicks = cfg.getInt("highEnergyRefinerCookTimeTicks", late, 72_000, 1, Integer.MAX_VALUE,
                "High Energy Refiner cook time in ticks (~1 hour at 20 TPS)");
        highEnergyRefinerBuffer = cfg.getInt("highEnergyRefinerBuffer", late, 1_000_000, 1, Integer.MAX_VALUE,
                "High Energy Refiner RF buffer");
        highEnergyRefinerMaxReceive = cfg.getInt("highEnergyRefinerMaxReceive", late, 10_000, 0, Integer.MAX_VALUE,
                "High Energy Refiner max RF receive per tick");

        final String pads = "wormhole_pads";
        wormholePadUpkeepRfPerTick = cfg.getInt("wormholePadUpkeepRfPerTick", pads, 50_000, 0, Integer.MAX_VALUE,
                "Wormhole Pad RF upkeep per tick while linked");
        wormholePadBuffer = cfg.getInt("wormholePadBuffer", pads, 2_000_000, 1, Integer.MAX_VALUE,
                "Wormhole Pad RF buffer");
        wormholePadMaxReceive = cfg.getInt("wormholePadMaxReceive", pads, 100_000, 0, Integer.MAX_VALUE,
                "Wormhole Pad max RF receive per tick");
        wormholePadTeleportCooldownTicks = cfg.getInt("wormholePadTeleportCooldownTicks", pads, 60, 0, Integer.MAX_VALUE,
                "Cooldown after a pad teleport in ticks");

        if (cfg.hasChanged()) {
            cfg.save();
        }
    }
}
