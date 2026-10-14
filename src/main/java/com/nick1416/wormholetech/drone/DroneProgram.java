package com.nick1416.wormholetech.drone;

/** Xenolith Chip programs. Miner programs come first (their ordinal is the Miner Drone item meta). */
public enum DroneProgram {
    NAQUADAH("naquadah", "Naquadah", 0x6FA8FF, "item:wormholetech:refined_naquadah"),
    COAL("coal", "Coal", 0x2B2B2B, "item:minecraft:coal:0"),
    IRON("iron", "Iron", 0xD8AF93, "item:minecraft:iron_ingot"),
    MAGNETITE("magnetite", "Magnetite", 0x4A4A55, "item:minecraft:iron_ore"),
    PYRITE("pyrite", "Pyrite", 0xD8C24A, "ore:dustSulfur"),
    BAUXITE("bauxite", "Bauxite", 0xC9733F, "item:minecraft:clay_ball"),
    COPPER("copper", "Copper", 0xE07A3A, "ore:ingotCopper"),
    CASSITERITE("cassiterite", "Cassiterite", 0xB8B8C8, "ore:ingotTin"),
    GOLD("gold", "Gold", 0xFFD83A, "item:minecraft:gold_ingot"),
    NICKEL("nickel", "Nickel", 0xC8C89A, "ore:ingotNickel"),
    PLATINUM("platinum", "Platinum", 0x9EE6F0, "ore:ingotPlatinum"),
    URANIUM("uranium", "Uranium", 0x6FE04A, "ore:ingotUranium"),
    QUARTZITE("quartzite", "Quartzite", 0xF0EAE0, "item:minecraft:quartz"),
    GALENA("galena", "Galena", 0x6A6A8A, "ore:dustLead"),
    LEAD("lead", "Lead", 0x5A5A7A, "ore:ingotLead"),
    SILVER("silver", "Silver", 0xDDE6F0, "ore:ingotSilver"),
    LAPIS("lapis", "Lapis", 0x2A4FC8, "item:minecraft:dye:4"),
    CINNABAR("cinnabar", "Cinnabar", 0xD03030, "item:minecraft:redstone"),
    SILT("silt", "Silt", 0xD8C89A, "item:minecraft:sand:0"),
    DEFENSIVE("defensive", null, 0xFF7A1A, "item:minecraft:blaze_powder"),
    RESEARCH("research", null, 0xC9A0FF, "item:minecraft:book");

    public final String id, mineral, mineralSlot;
    public final int color;

    DroneProgram(String id, String mineral, int color, String mineralSlot) {
        this.id = id; this.mineral = mineral; this.color = color; this.mineralSlot = mineralSlot;
    }

    public static final DroneProgram[] VALUES = values();
    public static final int MINERS = 19;

    public boolean isMiner() { return ordinal() < MINERS; }
    public static DroneProgram byMeta(int m) { return m >= 0 && m < VALUES.length ? VALUES[m] : NAQUADAH; }
}
