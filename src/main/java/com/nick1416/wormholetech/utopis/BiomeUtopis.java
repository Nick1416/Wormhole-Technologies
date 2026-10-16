package com.nick1416.wormholetech.utopis;

import com.nick1416.wormholetech.entity.EntityEmeraldDrone;
import com.nick1416.wormholetech.entity.EntityEmeraldWisp;
import net.minecraft.world.biome.Biome;

/** The city of Utopis: nothing natural, only the drones that outlived their makers. */
public class BiomeUtopis extends Biome {
    public BiomeUtopis() {
        super(new BiomeProperties("Utopis").setBaseHeight(0.1F).setHeightVariation(0.0F).setTemperature(0.5F).setRainfall(0.0F).setRainDisabled());
        spawnableMonsterList.clear();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableCaveCreatureList.clear();
        spawnableMonsterList.add(new SpawnListEntry(EntityEmeraldDrone.class, 100, 2, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityEmeraldWisp.class, 50, 1, 3));
        spawnableMonsterList.add(new SpawnListEntry(EntityEmeraldDroid.class, 70, 1, 3));
    }

    @Override public int getSkyColorByTemp(float t) { return 0x020004; }
}
