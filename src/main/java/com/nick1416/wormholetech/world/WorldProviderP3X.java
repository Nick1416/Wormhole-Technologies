package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;

/** Overworld-style terrain made entirely of the Naquadah Wastes biome. */
public class WorldProviderP3X extends WorldProvider {

    @Override
    protected void init() {
        this.hasSkyLight = true;
        this.biomeProvider = new BiomeProviderSingle(Registration.NAQUADAH_WASTES);
    }

    @Override
    public DimensionType getDimensionType() { return ModDimensions.P3X; }

    @Override
    public IChunkGenerator createChunkGenerator() {
        // Different seed than the overworld so the terrain is not a copy; no structures.
        return new ChunkGeneratorOverworld(world, world.getSeed() ^ 0x5033581416L, false, "");
    }

    @Override public boolean isSurfaceWorld() { return true; }
    @Override public boolean canRespawnHere() { return false; }
    @Override public int getRespawnDimension(EntityPlayerMP player) { return 0; }
    @Override public String getSaveFolder() { return "DIM_P3X_1416"; }
}
