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
        decorator.treesPerChunk = 12;
        decorator.flowersPerChunk = 32;
        decorator.grassPerChunk = 28;
        decorator.mushroomsPerChunk = 1;
        decorator.reedsPerChunk = 4;
        decorator.waterlilyPerChunk = 10;
        spawnableMonsterList.clear();
        spawnableCaveCreatureList.clear();
        spawnableCreatureList.clear();
        spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 14, 3, 6));
        spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 14, 3, 5));
        spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 6, 3, 6));
        spawnableCreatureList.add(new SpawnListEntry(EntityParrot.class, 18, 2, 5));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityOcelot.class, 5, 1, 3));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityCow.class, 10, 3, 5));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityPig.class, 9, 3, 5));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityChicken.class, 12, 3, 6));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityLlama.class, 4, 2, 4));
        spawnableCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityWolf.class, 3, 2, 4));
        spawnableCaveCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntityBat.class, 10, 4, 8));
        spawnableWaterCreatureList.add(new SpawnListEntry(net.minecraft.entity.passive.EntitySquid.class, 14, 3, 6));
    }

    /** Chunks are stocked with animals more often when first generated (vanilla 0.1). */
    @Override public float getSpawningChance() { return 0.35F; }

    /** Mix in big oaks and birches for an old-growth look. */
    @Override
    public net.minecraft.world.gen.feature.WorldGenAbstractTree getRandomTreeFeature(java.util.Random r) {
        int k = r.nextInt(10);
        return k < 3 ? BIG_TREE_FEATURE : k < 5 ? new net.minecraft.world.gen.feature.WorldGenBirchTree(false, false) : TREE_FEATURE;
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
