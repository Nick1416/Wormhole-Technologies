package com.nick1416.wormholetech.moon;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.world.biome.Biome;

/** Sel'Vora's only biome: grey regolith plains over stone, lifeless. */
public class BiomeSelVora extends Biome {
    public BiomeSelVora() {
        super(new BiomeProperties("Sel'Vora Highlands").setBaseHeight(0.18F).setHeightVariation(0.22F)
                .setTemperature(0.0F).setRainfall(0.0F).setRainDisabled());
        topBlock = Registration.MOON_REGOLITH.getDefaultState();
        fillerBlock = Registration.MOON_REGOLITH.getDefaultState();
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        decorator.treesPerChunk = -999;
        decorator.flowersPerChunk = 0;
        decorator.grassPerChunk = 0;
        decorator.deadBushPerChunk = 0;
        decorator.reedsPerChunk = 0;
        decorator.cactiPerChunk = 0;
        decorator.mushroomsPerChunk = 0;
        decorator.sandPatchesPerChunk = 0;
        decorator.clayPerChunk = 0;
        decorator.gravelPatchesPerChunk = 0;
        decorator.generateFalls = false;
    }

    @Override public int getSkyColorByTemp(float t) { return 0x000004; }
    @Override public int getGrassColorAtPos(net.minecraft.util.math.BlockPos p) { return 0x9A9A9A; }
}
