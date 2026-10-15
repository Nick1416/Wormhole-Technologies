package com.nick1416.wormholetech.moon;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.world.ModDimensions;
import com.nick1416.wormholetech.world.WorldProviderP3X;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Sel'Vora, Tal'Vora's moon: pale regolith under a black, starry sky that never lightens. */
public class WorldProviderSelVora extends WorldProvider {
    @Override
    protected void init() {
        hasSkyLight = true;
        biomeProvider = new BiomeProviderSingle(Registration.SEL_VORA_BIOME);
    }

    @Override public DimensionType getDimensionType() { return ModDimensions.SEL_VORA; }

    @Override
    public IChunkGenerator createChunkGenerator() { return new ChunkGeneratorSelVora(world, world.getSeed() ^ 0x5E1F0BA2L); }

    /** Always midnight. */
    @Override public float calculateCelestialAngle(long worldTime, float pt) { return 0.5F; }
    @Override public boolean isDaytime() { return false; }

    @SideOnly(Side.CLIENT)
    @Override public Vec3d getSkyColor(Entity camera, float pt) { return new Vec3d(0.0D, 0.0D, 0.01D); }

    @SideOnly(Side.CLIENT)
    @Override public Vec3d getFogColor(float a, float pt) { return new Vec3d(0.03D, 0.03D, 0.045D); }

    @SideOnly(Side.CLIENT)
    @Override public float getStarBrightness(float pt) { return 1.0F; }

    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraftforge.client.IRenderHandler getSkyRenderer() {
        if (super.getSkyRenderer() == null) setSkyRenderer(new com.nick1416.wormholetech.client.SelVoraSkyRenderer());
        return super.getSkyRenderer();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraftforge.client.IRenderHandler getCloudRenderer() {
        if (super.getCloudRenderer() == null) setCloudRenderer(new net.minecraftforge.client.IRenderHandler() {
            @Override public void render(float pt, net.minecraft.client.multiplayer.WorldClient w, net.minecraft.client.Minecraft mc) {}
        });
        return super.getCloudRenderer();
    }

    @SideOnly(Side.CLIENT) @Override public boolean doesXZShowFog(int x, int z) { return false; }
    @Override public void updateWeather() { WorldProviderP3X.clearWeather(world); }
    @Override public boolean isSurfaceWorld() { return true; }
    @Override public boolean canRespawnHere() { return false; }
    @Override public int getRespawnDimension(EntityPlayerMP player) { return 0; }
    @Override public String getSaveFolder() { return "DIM_SEL_VORA"; }
}
