package com.nick1416.wormholetech.elyndra;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.biome.Biome;

/** Rolling, overgrown wilds: dense flowers and old forest. No monsters roam the abandoned world. */
public class BiomeElyndra extends Biome {
    public BiomeElyndra() {
        super(new BiomeProperties("Elyndra Wilds").setBaseHeight(0.2F).setHeightVariation(0.35F)
                .setTemperature(0.8F).setRainfall(0.8F).setWaterColor(0x3FD6E0));
        decorator.treesPerChunk = 7;
        decorator.flowersPerChunk = 20;
        decorator.grassPerChunk = 16;
        decorator.mushroomsPerChunk = 1;
        decorator.reedsPerChunk = 4;
        decorator.waterlilyPerChunk = 6;
        spawnableMonsterList.clear();
        spawnableCaveCreatureList.clear();
        spawnableCreatureList.clear();
        spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 8, 2, 4));
        spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 8, 2, 3));
        spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 4, 2, 4));
        spawnableCreatureList.add(new SpawnListEntry(EntityParrot.class, 8, 1, 3));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityOcelot.class, 3, 1, 2));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityCow.class, 6, 2, 4));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityPig.class, 5, 2, 4));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityChicken.class, 6, 2, 4));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityLlama.class, 2, 2, 3));
        spawnableCaveCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityBat.class, 6, 4, 8));
        spawnableWaterCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntitySquid.class, 6, 1, 4));
    }

    /** Extra vines hanging off trees and ruins. */
    @Override
    public void decorate(net.minecraft.world.World w, java.util.Random r, net.minecraft.util.math.BlockPos pos) {
        super.decorate(w, r, pos);
        net.minecraft.world.gen.feature.WorldGenVines vines = new net.minecraft.world.gen.feature.WorldGenVines();
        for (int i = 0; i < 12; i++) vines.generate(w, r, pos.add(r.nextInt(16) + 8, 64 + r.nextInt(60), r.nextInt(16) + 8));
    }

    @Override public int getSkyColorByTemp(float t) { return 0x7FA8FF; }
    @Override public int getGrassColorAtPos(net.minecraft.util.math.BlockPos pos) { return 0x5BD86A; }
    @Override public int getFoliageColorAtPos(net.minecraft.util.math.BlockPos pos) { return 0x4CC27A; }
}
