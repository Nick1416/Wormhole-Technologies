package com.nick1416.wormholetech.derelict;

import com.nick1416.wormholetech.entity.EntityEmeraldDrone;
import com.nick1416.wormholetech.entity.EntityEmeraldWisp;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

/** Single biome of the derelict vessel: no weather, no decoration, only ship crawlers spawn. */
public class BiomeDerelict extends Biome {
    public BiomeDerelict() {
        super(new BiomeProperties("Derelict Vessel").setRainDisabled().setTemperature(0.5F).setBaseHeight(0F).setHeightVariation(0F));
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        spawnableMonsterList.add(new SpawnListEntry(EntityEmeraldDrone.class, 100, 2, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityEmeraldWisp.class, 45, 1, 2));
    }

    @Override public void decorate(World worldIn, Random rand, BlockPos pos) {}
}
