package com.nick1416.wormholetech.elyndra;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Elyndra: the abandoned home world of the gate builders. Lush, bright and silent. One-way: you respawn here. */
public class WorldProviderElyndra extends WorldProvider {
    @Override
    protected void init() {
        hasSkyLight = true;
        biomeProvider = new BiomeProviderSingle(Registration.ELYNDRA_BIOME);
    }

    @Override public DimensionType getDimensionType() { return ModDimensions.ELYNDRA; }

    @Override
    public IChunkGenerator createChunkGenerator() { return new ChunkGeneratorElyndra(world, world.getSeed() ^ 0xE1D7AL); }

    @SideOnly(Side.CLIENT)
    @Override public Vec3d getSkyColor(Entity camera, float pt) {
        Vec3d v = super.getSkyColor(camera, pt);
        return new Vec3d(v.x * 0.85 + 0.08, v.y * 0.8 + 0.04, Math.min(1.0, v.z * 1.05 + 0.08));
    }

    @SideOnly(Side.CLIENT)
    @Override public Vec3d getFogColor(float a, float pt) {
        Vec3d v = super.getFogColor(a, pt);
        return new Vec3d(v.x * 0.9 + 0.06, v.y * 0.85, Math.min(1.0, v.z + 0.06));
    }

    @Override public boolean isSurfaceWorld() { return true; }
    @Override public boolean canRespawnHere() { return true; }
    @Override public int getRespawnDimension(EntityPlayerMP player) { return getDimension(); }
    @Override public String getSaveFolder() { return "DIM_ELYNDRA"; }
}
