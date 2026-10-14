package com.nick1416.wormholetech.talvora;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;

/** Tal'Vora's only biome: red sand dunes over red sandstone, almost bare. */
public class BiomeTalVora extends Biome {
    public BiomeTalVora() {
        super(new BiomeProperties("Tal'Vora Dunes").setBaseHeight(0.22F).setHeightVariation(0.12F)
                .setTemperature(2.0F).setRainfall(0.0F).setRainDisabled());
        topBlock = Blocks.SAND.getStateFromMeta(1);
        fillerBlock = Blocks.SAND.getStateFromMeta(1);
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        decorator.treesPerChunk = -999;
        decorator.flowersPerChunk = 0;
        decorator.grassPerChunk = 0;
        decorator.deadBushPerChunk = 1;
        decorator.reedsPerChunk = 0;
        decorator.cactiPerChunk = 0;
        decorator.mushroomsPerChunk = 0;
        decorator.sandPatchesPerChunk = 0;
        decorator.clayPerChunk = 0;
        decorator.gravelPatchesPerChunk = 0;
        decorator.generateFalls = false;
    }

    @Override public int getSkyColorByTemp(float t) { return 0xD9824A; }
}
