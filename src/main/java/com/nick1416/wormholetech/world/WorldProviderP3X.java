package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
        return new ChunkGeneratorP3X(world, world.getSeed() ^ 0x5033581416L);
    }

    /** Violet sky (#9466CC), darkening at night like the overworld's. Must stay clearly non-blue. */
    public static final float[] SKY = { 0.58F, 0.40F, 0.80F };
    /** Warm rose-amber haze (#E09985). */
    public static final float[] FOG = { 0.88F, 0.60F, 0.52F };
    /** Lavender clouds. */
    public static final float[] CLOUD = { 0.88F, 0.80F, 0.96F };

    private static float daylight(float celestialAngle) {
        return MathHelper.clamp(MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity camera, float partialTicks) {
        float b = daylight(world.getCelestialAngle(partialTicks));
        return new Vec3d(SKY[0] * b, SKY[1] * b, SKY[2] * b);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        float b = daylight(celestialAngle);
        return new Vec3d(FOG[0] * (b * 0.94F + 0.06F), FOG[1] * (b * 0.94F + 0.06F), FOG[2] * (b * 0.91F + 0.09F));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getCloudColor(float partialTicks) {
        float b = daylight(world.getCelestialAngle(partialTicks)) * 0.9F + 0.1F;
        return new Vec3d(CLOUD[0] * b, CLOUD[1] * b, CLOUD[2] * b);
    }

    /** Teal-green sunrise/sunset glow instead of the vanilla orange. */
    @SideOnly(Side.CLIENT)
    @Override
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        float[] c = super.calcSunriseSunsetColors(celestialAngle, partialTicks);
        if (c == null) return null;
        float a = c[3];
        return new float[] { 0.25F, 0.85F, 0.70F, a };
    }

    /** No rain or thunder here (overworld rain must not leak in); P3X has dust storms instead, see P3XWeather. */
    @Override
    public void updateWeather() { clearWeather(world); }

    /** Zero rain/thunder on this world (server and client). */
    public static void clearWeather(net.minecraft.world.World world) {
        try {
            for (java.lang.reflect.Field f : WEATHER_FIELDS) f.setFloat(world, 0.0F);
        } catch (IllegalAccessException ignored) {
        }
    }

    // World.setRainStrength/setThunderStrength are client-only, so the fields are set directly
    private static final java.lang.reflect.Field[] WEATHER_FIELDS = {
            net.minecraftforge.fml.relauncher.ReflectionHelper.findField(net.minecraft.world.World.class, "prevRainingStrength", "field_73003_n"),
            net.minecraftforge.fml.relauncher.ReflectionHelper.findField(net.minecraft.world.World.class, "rainingStrength", "field_73004_o"),
            net.minecraftforge.fml.relauncher.ReflectionHelper.findField(net.minecraft.world.World.class, "prevThunderingStrength", "field_73018_p"),
            net.minecraftforge.fml.relauncher.ReflectionHelper.findField(net.minecraft.world.World.class, "thunderingStrength", "field_73017_q") };

    @Override public boolean isSurfaceWorld() { return true; }
    @Override public boolean canRespawnHere() { return false; }
    @Override public int getRespawnDimension(EntityPlayerMP player) { return 0; }
    @Override public String getSaveFolder() { return "DIM_P3X_1416"; }
}
