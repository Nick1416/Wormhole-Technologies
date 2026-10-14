package com.nick1416.wormholetech.talvora;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.world.ModDimensions;
import com.nick1416.wormholetech.world.WorldProviderP3X;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Tal'Vora: a red desert world in another galaxy under two suns. */
public class WorldProviderTalVora extends WorldProvider {
    /** Burnt-orange sky, dusty amber-red haze. */
    public static final float[] SKY = { 0.86F, 0.50F, 0.30F };
    public static final float[] FOG = { 0.80F, 0.42F, 0.26F };

    @Override
    protected void init() {
        hasSkyLight = true;
        biomeProvider = new BiomeProviderSingle(Registration.TAL_VORA_BIOME);
    }

    @Override public DimensionType getDimensionType() { return ModDimensions.TAL_VORA; }

    @Override
    public IChunkGenerator createChunkGenerator() { return new ChunkGeneratorTalVora(world, world.getSeed() ^ 0x7A1F0BA1L); }

    private static float daylight(float a) {
        return MathHelper.clamp(MathHelper.cos(a * (float) Math.PI * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity camera, float pt) {
        float b = daylight(world.getCelestialAngle(pt)) * 0.92F + 0.08F;
        return new Vec3d(SKY[0] * b, SKY[1] * b, SKY[2] * b);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float a, float pt) {
        float b = daylight(a) * 0.9F + 0.1F;
        return new Vec3d(FOG[0] * b, FOG[1] * b, FOG[2] * b);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraftforge.client.IRenderHandler getSkyRenderer() {
        if (super.getSkyRenderer() == null) setSkyRenderer(new com.nick1416.wormholetech.client.TalVoraSkyRenderer());
        return super.getSkyRenderer();
    }

    @SideOnly(Side.CLIENT) @Override public boolean doesXZShowFog(int x, int z) { return false; }
    @Override public void updateWeather() { WorldProviderP3X.clearWeather(world); }
    @Override public boolean isSurfaceWorld() { return true; }
    @Override public boolean canRespawnHere() { return false; }
    @Override public int getRespawnDimension(EntityPlayerMP player) { return 0; }
    @Override public String getSaveFolder() { return "DIM_TAL_VORA"; }
}
