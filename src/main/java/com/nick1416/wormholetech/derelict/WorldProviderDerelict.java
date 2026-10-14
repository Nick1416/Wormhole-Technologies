package com.nick1416.wormholetech.derelict;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.world.ModDimensions;
import com.nick1416.wormholetech.world.WorldProviderP3X;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** The derelict vessel: permanent night among the stars, no weather. */
public class WorldProviderDerelict extends WorldProvider {
    @Override
    protected void init() {
        this.hasSkyLight = true;
        this.biomeProvider = new BiomeProviderSingle(Registration.DERELICT_BIOME);
    }

    /** Slight ambient light so unlit corners are dim rather than pitch black (the sky colour is unaffected). */
    public static final float AMBIENT = 0.10F;

    @Override
    protected void generateLightBrightnessTable() {
        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - i / 15.0F;
            lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - AMBIENT) + AMBIENT;
        }
    }

    @Override public DimensionType getDimensionType() { return ModDimensions.DERELICT; }
    @Override public IChunkGenerator createChunkGenerator() { return new ChunkGeneratorDerelict(world, world.getSeed() ^ 0x0DE1E1C7L); }

    /** Always midnight, so the star field is always out. */
    @Override public float calculateCelestialAngle(long worldTime, float partialTicks) { return 0.5F; }

    @SideOnly(Side.CLIENT) @Override public Vec3d getSkyColor(Entity camera, float partialTicks) { return new Vec3d(0.0, 0.0, 0.01); }
    @SideOnly(Side.CLIENT) @Override public Vec3d getFogColor(float a, float p) { return new Vec3d(0.01, 0.01, 0.03); }
    @SideOnly(Side.CLIENT) @Override public float getCloudHeight() { return -128.0F; }
    @SideOnly(Side.CLIENT) @Override public float[] calcSunriseSunsetColors(float a, float p) { return null; }
    @SideOnly(Side.CLIENT) @Override public double getVoidFogYFactor() { return 1.0D; }
    @Override public boolean doesXZShowFog(int x, int z) { return false; }

    @Override public void updateWeather() { WorldProviderP3X.clearWeather(world); }
    @Override public boolean canDoRainSnowIce(Chunk chunk) { return false; }
    @Override public boolean canSnowAt(net.minecraft.util.math.BlockPos pos, boolean checkLight) { return false; }

    @Override public boolean isSurfaceWorld() { return true; }
    @Override public boolean canRespawnHere() { return false; }
    @Override public int getRespawnDimension(EntityPlayerMP player) { return 0; }
    @Override public int getAverageGroundLevel() { return DerelictLayout.FLOOR + 1; }
    @Override public String getSaveFolder() { return "DIM_DERELICT"; }
}
