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
    public static int highEnergyRefinerCookTimeTicks = 36_000;
    public static int refinedNaquadahBlockDecayTicks = 12_000;
    public static int highEnergyRefinerBuffer = 1_000_000;
    public static int highEnergyRefinerMaxReceive = 10_000;

    // Wormhole Pad
    public static int wormholePadUpkeepRfPerTick = 50_000;
    public static int wormholePadBuffer = 2_000_000;
    public static int wormholePadMaxReceive = 100_000;
    public static int wormholePadTeleportCooldownTicks = 60;

    // stargate / P3X-1416
    public static boolean stargateEnabled = true;
    public static int p3xDimensionId = -1416;
    public static int dialingComputerRfPerTickSetting = -1; // -1: half the Relativistic Computer upkeep
    public static int dialingComputerBuffer = 1_000_000;
    public static int dialingComputerMaxReceive = 100_000;
    public static int p3xNaquadahOreVeinsPerChunk = 8;
    public static int p3xNaquadahOreVeinSize = 6;
    public static int p3xNaquadahOreMinY = 5;
    public static int p3xNaquadahOreMaxY = 64;

    /** Dialing Computer upkeep per tick while the wormhole is open. */
    public static int dialingComputerRfPerTick() {
        return dialingComputerRfPerTickSetting >= 0 ? dialingComputerRfPerTickSetting
                : Math.max(0, relativisticComputerUpkeepRfPerTick / 2);
    }

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

        stableConverterRfPerTick = cfg.getInt("stableConverterRfPerTick", gen, 100_000, 0, Integer.MAX_VALUE,
                "Wormhole Energy Converter RF per tick while connected to a wormhole");
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
        net.minecraftforge.common.config.Property refineTime = cfg.get(late, "highEnergyRefinerCookTimeTicks", 36_000,
                "High Energy Refiner cook time in ticks (~30 minutes at 20 TPS)", 1, Integer.MAX_VALUE);
        if (refineTime.getInt() == 72_000) {
            // Migrate the old 1-hour default; custom values are left alone.
            refineTime.set(36_000);
        }
        highEnergyRefinerCookTimeTicks = refineTime.getInt();
        highEnergyRefinerBuffer = cfg.getInt("highEnergyRefinerBuffer", late, 1_000_000, 1, Integer.MAX_VALUE,
                "High Energy Refiner RF buffer");
        highEnergyRefinerMaxReceive = cfg.getInt("highEnergyRefinerMaxReceive", late, 10_000, 0, Integer.MAX_VALUE,
                "High Energy Refiner max RF receive per tick");

        refinedNaquadahBlockDecayTicks = cfg.getInt("refinedNaquadahBlockDecayTicks", "wormhole_connection", 12_000, 1,
                Integer.MAX_VALUE, "Game ticks one Block of Refined Naquadah keeps a wormhole connection open "
                        + "(Duplicators, Energy Converter) before it becomes an Unstable Block of Naquadah (~10 minutes at 20 TPS)");

        final String sg = "stargate";
        stargateEnabled = cfg.getBoolean("stargateEnabled", sg, true,
                "Titanium gate + Dialing Computer + the P3X-1416 dimension. Needs Galacticraft Titanium Blocks or any oredict blockTitanium");
        p3xDimensionId = cfg.getInt("p3xDimensionId", sg, -1416, Integer.MIN_VALUE, Integer.MAX_VALUE,
                "Dimension ID of P3X-1416. Keep it stable once a world uses it; must not clash with another mod");
        dialingComputerRfPerTickSetting = cfg.getInt("dialingComputerRfPerTick", sg, -1, -1, Integer.MAX_VALUE,
                "Dialing Computer RF per tick while the wormhole is open (-1 = half of relativisticComputerUpkeepRfPerTick)");
        dialingComputerBuffer = cfg.getInt("dialingComputerBuffer", sg, 1_000_000, 1, Integer.MAX_VALUE, "Dialing Computer RF buffer");
        dialingComputerMaxReceive = cfg.getInt("dialingComputerMaxReceive", sg, 100_000, 0, Integer.MAX_VALUE,
                "Dialing Computer max RF receive per tick");
        p3xNaquadahOreVeinsPerChunk = cfg.getInt("p3xNaquadahOreVeinsPerChunk", sg, 8, 0, 64,
                "Naquadah ore veins per chunk on P3X-1416 (no ore is added to any other dimension)");
        p3xNaquadahOreVeinSize = cfg.getInt("p3xNaquadahOreVeinSize", sg, 6, 1, 32, "Blocks per Naquadah ore vein on P3X-1416");
        p3xNaquadahOreMinY = cfg.getInt("p3xNaquadahOreMinY", sg, 5, 1, 255, "Lowest Y for Naquadah ore on P3X-1416");
        p3xNaquadahOreMaxY = cfg.getInt("p3xNaquadahOreMaxY", sg, 64, 1, 255, "Highest Y for Naquadah ore on P3X-1416");

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
