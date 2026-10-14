package com.nick1416.wormholetech;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Forge Configuration for RF rates, lifetimes, and energy costs.
 * Defaults match the pre-0.1.0 hardcoded values (no balance nerfs).
 */
public final class ModConfig {
    private ModConfig() {}

    public static final String MODE_CC = "cc", MODE_NORMAL = "normal";
    /** Game mode, read once at startup: "cc" (Compact Claustrophobia, duplicators enabled) or "normal". */
    public static String mode = MODE_NORMAL;

    public static boolean isCC() { return MODE_CC.equals(mode); }

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
    public static int highEnergyRefinerTitaniumTicks = 200;
    /** Ticks a full Aetherius lasts at quantum clock +1 (and takes to recover fully at -1). */
    public static int aetheriusDecayTicks = 36_000;
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
    public static int derelictDimensionId = -1417;
    public static int talVoraDimensionId = -1418;
    public static double talVoraGravity = 1.15D;
    public static double talVoraFallDamageMultiplier = 1.5D;
    public static int talVoraVillageRegionChunks = 28;
    public static int talVoraVillageChance = 70;
    public static int derelictModulatorRoomChance = 384;
    public static int derelictRoomChance = 7;
    public static int derelictSpawnIntervalTicks = 100;
    public static int derelictCrawlerCap = 10;
    public static int dialingComputerRfPerTickSetting = -1; // -1: half the Relativistic Computer upkeep
    public static int dialingComputerBuffer = 1_000_000;
    public static int dialingComputerMaxReceive = 100_000;
    public static int p3xNaquadahOreVeinsPerChunk = 11;
    public static int p3xNaquadahOreVeinSizeMin = 4;
    public static int p3xNaquadahOreVeinSize = 8;   // max vein size
    public static int p3xNaquadahOreMinY = 5;
    public static int p3xNaquadahOreMaxY = 80;
    public static int p3xSurfaceOreClusterChance = 3; // 1 in N chunks, 0 = off
    public static int p3xCrystalsPerChunk = 3;
    public static int p3xSpireChance = 6;            // 1 in N chunks, 0 = off
    public static int p3xObeliskChance = 40;         // 1 in N chunks, 0 = off
    public static int p3xTempleChance = 60;          // 1 in N chunks, 0 = off
    public static int p3xTempleChestChance = 2;      // 1 in N temples get a chest
    public static int minerDroneTicksPerItem = 400;    // 20 s per ore
    public static int xenolithOreVeinsPerChunk = 5;
    public static int xenolithDroneSpawnWeight = 5;     // 0 = off
    public static int emeraldDroneSpawnWeight = 50;     // 0 = off
    public static boolean p3xDustStorms = true;
    public static int p3xDustStormClearTicks = 36_000;   // average clear spell
    public static int p3xDustStormDurationTicks = 6_000; // average storm length
    public static int p3xCrystalCaveChance = 80;         // 1 in N chunks, 0 = off
    public static int p3xNightPatrolIntervalTicks = 1_200; // 0 = off
    public static int p3xGuardianTempleChance = 400;     // 1 in N chunks, 0 = off
    public static int p3xPrysmianOreVeinsPerChunk = 1;
    public static int p3xPrysmianOreVeinSize = 8;
    public static int p3xPrysmianOreMaxY = 16;

    /** Reads an int; if it still holds the previous default, moves it to the new default. */
    private static int migrated(Configuration cfg, String cat, String key, int def, int oldDef, int min, int max, String comment) {
        return migrated(cfg, cat, key, def, new int[] { oldDef }, min, max, comment);
    }

    /** Same, for a setting whose default has changed more than once. */
    private static int migrated(Configuration cfg, String cat, String key, int def, int[] oldDefs, int min, int max, String comment) {
        net.minecraftforge.common.config.Property p = cfg.get(cat, key, def, comment, min, max);
        for (int old : oldDefs) if (p.getInt() == old) p.set(def);
        return Math.max(min, Math.min(max, p.getInt()));
    }

    /** Dialing Computer upkeep per tick while the wormhole is open. */
    public static int dialingComputerRfPerTick() {
        return dialingComputerRfPerTickSetting >= 0 ? dialingComputerRfPerTickSetting
                : Math.max(0, relativisticComputerUpkeepRfPerTick / 2);
    }

    public static void load(File file) {
        Configuration cfg = new Configuration(file);
        cfg.load();

        String m = cfg.getString("mode", Configuration.CATEGORY_GENERAL, MODE_NORMAL,
                "Game mode: 'cc' (Compact Claustrophobia: Wormhole Duplicators enabled) or 'normal' (duplicators removed "
                        + "from gameplay). Read once at startup; clients must use the same mode as the server.",
                new String[] {MODE_CC, MODE_NORMAL}).trim().toLowerCase(java.util.Locale.ROOT);
        if (!MODE_CC.equals(m) && !MODE_NORMAL.equals(m)) {
            WormholeTech.LOGGER.error("Invalid mode '{}' in wormholetech.cfg (allowed: cc, normal); using normal", m);
            m = MODE_NORMAL;
        }
        mode = m;
        WormholeTech.LOGGER.info("Wormhole Technologies mode: {}", mode);

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
        highEnergyRefinerTitaniumTicks = cfg.getInt("highEnergyRefinerTitaniumTicks", late, 200, 1, Integer.MAX_VALUE,
                "High Energy Refiner ticks to turn one silicon into a Titanium Ingot (200 = furnace speed)");
        aetheriusDecayTicks = cfg.getInt("aetheriusDecayTicks", late, 36_000, 20, Integer.MAX_VALUE,
                "Ticks a full Aetherius lasts in a Relativistic Computer at quantum clock +1 (lost at zero); it recovers at the same rate at -1");
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
        derelictDimensionId = cfg.getInt("derelictDimensionId", sg, -1417, Integer.MIN_VALUE, Integer.MAX_VALUE,
                "Dimension ID of the derelict vessel (second gate address). Keep it stable once a world uses it; must not clash with another mod");
        net.minecraftforge.common.config.Property modRoom = cfg.get(sg, "derelictModulatorRoomChance", 384,
                "1 in N ship chunks is a modulator room with a Giant Emerald Sentinel (384 = twice as common as nether fortresses, which are 1 in 768; 0 = off)",
                0, 1_000_000);
        if (modRoom.getInt() == 770) modRoom.set(384); // migrate the old default
        talVoraDimensionId = cfg.getInt("talVoraDimensionId", sg, -1418, Integer.MIN_VALUE, Integer.MAX_VALUE,
                "Dimension ID of Tal'Vora (Intergalactic Coordinates). Keep it stable once a world uses it; must not clash with another mod");
        talVoraGravity = cfg.getFloat("talVoraGravity", sg, 1.15F, 1.0F, 1.2F,
                "Gravity on Tal'Vora relative to normal (1.15 = jumps ~1.07 blocks instead of 1.25, faster falls; capped at 1.2 so one-block steps stay jumpable)");
        talVoraFallDamageMultiplier = cfg.getFloat("talVoraFallDamageMultiplier", sg, 1.5F, 1.0F, 10.0F,
                "Fall damage multiplier on Tal'Vora");
        net.minecraftforge.common.config.Property vr = cfg.get(sg, "talVoraVillageRegionChunks", 28,
                "Tal'Vora is split into square regions of this many chunks; each may hold one alien village", 8, 1000);
        if (vr.getInt() == 40) vr.set(28); // migrate the old default
        talVoraVillageRegionChunks = Math.max(8, Math.min(1000, vr.getInt()));
        net.minecraftforge.common.config.Property vc = cfg.get(sg, "talVoraVillageChance", 70,
                "Percent chance that a Tal'Vora region holds an alien village (0 = none)", 0, 100);
        if (vc.getInt() == 50 || vc.getInt() == 65) vc.set(70); // migrate the old default
        talVoraVillageChance = Math.max(0, Math.min(100, vc.getInt()));
        derelictModulatorRoomChance = Math.max(0, Math.min(1_000_000, modRoom.getInt()));
        derelictRoomChance = cfg.getInt("derelictRoomChance", sg, 7, 0, 1000,
                "1 in N ship chunks is an open room (half of them with a chest; 0 = maze only)");
        derelictSpawnIntervalTicks = cfg.getInt("derelictSpawnIntervalTicks", sg, 100, 0, 1_000_000,
                "Every N ticks each player on the derelict vessel gets up to 2 extra crawlers (drones/wisps) nearby (0 = off)");
        derelictCrawlerCap = cfg.getInt("derelictCrawlerCap", sg, 10, 0, 200,
                "Extra crawler spawning stops while this many hostile mobs are within 32 blocks of the player");
        dialingComputerRfPerTickSetting = cfg.getInt("dialingComputerRfPerTick", sg, -1, -1, Integer.MAX_VALUE,
                "Dialing Computer RF per tick while the wormhole is open (-1 = half of relativisticComputerUpkeepRfPerTick)");
        dialingComputerBuffer = cfg.getInt("dialingComputerBuffer", sg, 1_000_000, 1, Integer.MAX_VALUE, "Dialing Computer RF buffer");
        dialingComputerMaxReceive = cfg.getInt("dialingComputerMaxReceive", sg, 100_000, 0, Integer.MAX_VALUE,
                "Dialing Computer max RF receive per tick");
        // Ore defaults changed with the natural Naquadah Ore (PR "P3X exotic"): migrate untouched old defaults.
        p3xNaquadahOreVeinsPerChunk = migrated(cfg, sg, "p3xNaquadahOreVeinsPerChunk", 11, 8, 0, 64,
                "Natural Naquadah Ore veins per chunk on P3X-1416 (no ore is added to any other dimension)");
        p3xNaquadahOreVeinSizeMin = cfg.getInt("p3xNaquadahOreVeinSizeMin", sg, 4, 1, 32,
                "Smallest Natural Naquadah Ore vein on P3X-1416 (blocks)");
        p3xNaquadahOreVeinSize = migrated(cfg, sg, "p3xNaquadahOreVeinSize", 8, 6, 1, 32,
                "Largest Natural Naquadah Ore vein on P3X-1416 (blocks)");
        p3xNaquadahOreMinY = cfg.getInt("p3xNaquadahOreMinY", sg, 5, 1, 255, "Lowest Y for Naquadah ore on P3X-1416");
        p3xNaquadahOreMaxY = migrated(cfg, sg, "p3xNaquadahOreMaxY", 80, 64, 1, 255, "Highest Y for Naquadah ore on P3X-1416");
        p3xSurfaceOreClusterChance = cfg.getInt("p3xSurfaceOreClusterChance", sg, 3, 0, 1000,
                "1 in N chunks on P3X-1416 get a small exposed Naquadah Ore cluster on the surface (0 = off)");
        p3xCrystalsPerChunk = cfg.getInt("p3xCrystalsPerChunk", sg, 3, 0, 64,
                "Naquadah Crystal placement attempts per chunk on P3X-1416 (surface + caves)");
        p3xSpireChance = cfg.getInt("p3xSpireChance", sg, 6, 0, 1000, "1 in N chunks on P3X-1416 get a rock spire (0 = off)");
        p3xObeliskChance = cfg.getInt("p3xObeliskChance", sg, 40, 0, 10000,
                "1 in N chunks on P3X-1416 get an Ancient Stone obelisk/ruin (0 = off)");
        p3xTempleChance = migrated(cfg, sg, "p3xTempleChance", 60, 150, 0, 100000,
                "1 in N chunks on P3X-1416 get a small ancient temple (0 = off)");
        p3xTempleChestChance = cfg.getInt("p3xTempleChestChance", sg, 2, 0, 1000,
                "1 in N temples contain a loot chest (0 = never)");
        minerDroneTicksPerItem = cfg.getInt("minerDroneTicksPerItem", sg, 400, 1, 72000,
                "Ticks a Miner Drone needs per ore it puts into the adjacent storage (400 = one every 20 s). No power needed.");
        xenolithOreVeinsPerChunk = cfg.getInt("xenolithOreVeinsPerChunk", sg, 5, 0, 64,
                "Xenolith Ore veins (up to 6 blocks) per Tal'Vora chunk, y 12-60 (0 = off).");
        xenolithDroneSpawnWeight = cfg.getInt("xenolithDroneSpawnWeight", sg, 5, 0, 1000,
                "Spawn weight of the harmless Xenolith Drone over Tal'Vora (0 = off). At most 2 drones within 96 blocks.");
        emeraldDroneSpawnWeight = migrated(cfg, sg, "emeraldDroneSpawnWeight", 50, new int[] { 6, 20 }, 0, 1000,
                "Spawn weight of Emerald Drones on the P3X-1416 surface (vanilla zombies are 100; 0 = off). Groups of 1-2. Needs a restart");
        p3xDustStorms = cfg.getBoolean("p3xDustStorms", sg, true,
                "Dust storms on P3X-1416 (its own weather cycle; dense haze, drones more aggressive). Off = always clear");
        p3xDustStormClearTicks = cfg.getInt("p3xDustStormClearTicks", sg, 36_000, 200, 10_000_000,
                "Average ticks between P3X-1416 dust storms (actual: 50%-150% of this)");
        p3xDustStormDurationTicks = cfg.getInt("p3xDustStormDurationTicks", sg, 6_000, 200, 10_000_000,
                "Average length of a P3X-1416 dust storm in ticks (actual: 50%-150% of this)");
        p3xCrystalCaveChance = cfg.getInt("p3xCrystalCaveChance", sg, 80, 0, 100000,
                "1 in N chunks on P3X-1416 get an underground crystal cave (0 = off)");
        p3xNightPatrolIntervalTicks = cfg.getInt("p3xNightPatrolIntervalTicks", sg, 1_200, 0, 1_000_000,
                "At night, every N ticks each player on P3X-1416 has a 1 in 2 chance of an Emerald Drone patrol (2-4 drones) "
                        + "arriving 24-40 blocks away, capped at 6 drones nearby (0 = off)");
        p3xGuardianTempleChance = cfg.getInt("p3xGuardianTempleChance", sg, 400, 0, 1_000_000,
                "1 in N chunks on P3X-1416 get a guardian temple with an Emerald Sentinel (0 = off)");
        p3xPrysmianOreVeinsPerChunk = cfg.getInt("p3xPrysmianOreVeinsPerChunk", sg, 1, 0, 64,
                "Prysmian Ore veins per chunk on P3X-1416 (vanilla diamond: 1; 0 = off)");
        p3xPrysmianOreVeinSize = cfg.getInt("p3xPrysmianOreVeinSize", sg, 8, 1, 32, "Prysmian Ore vein size (vanilla diamond: 8)");
        p3xPrysmianOreMaxY = cfg.getInt("p3xPrysmianOreMaxY", sg, 16, 1, 255, "Highest Y for Prysmian Ore (lowest is 1)");

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
