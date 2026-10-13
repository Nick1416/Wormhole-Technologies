package com.nick1416.wormholetech.client;

import com.nick1416.wormholetech.world.P3XWeather;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** Dust storm visuals on P3X-1416: dense rose-amber fog, shorter view distance and drifting dust. */
public final class P3XStormClient {
    private P3XStormClient() {}

    private static float level, prevLevel;
    /** Storm haze colour (#D98C66). */
    private static final float R = 0.85F, G = 0.55F, B = 0.40F;

    private static float level(float partial) { return prevLevel + (level - prevLevel) * partial; }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        prevLevel = level;
        World w = mc.world;
        boolean on = w != null && P3XWeather.isP3X(w) && P3XWeather.clientStorm;
        level = Math.max(0F, Math.min(1F, level + (on ? 0.01F : -0.01F))); // ~5 s fade
        if (w == null || level <= 0F || mc.isGamePaused() || mc.player == null) return;
        EntityPlayer p = mc.player;
        int n = (int) (12 * level);
        for (int i = 0; i < n; i++) {
            double x = p.posX + (w.rand.nextDouble() - 0.5) * 24, z = p.posZ + (w.rand.nextDouble() - 0.5) * 24;
            double y = p.posY + (w.rand.nextDouble() - 0.3) * 8;
            if (!w.canSeeSky(new BlockPos(x, y, z))) continue;
            // colored redstone dust; the speed arguments are its RGB
            w.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, R * 0.95, G * 0.95, B * 0.95);
        }
    }

    @SubscribeEvent
    public static void onFogColors(EntityViewRenderEvent.FogColors e) {
        float s = level((float) e.getRenderPartialTicks());
        if (s <= 0F) return;
        float k = s * 0.85F;
        e.setRed(e.getRed() * (1 - k) + R * k * dim(e));
        e.setGreen(e.getGreen() * (1 - k) + G * k * dim(e));
        e.setBlue(e.getBlue() * (1 - k) + B * k * dim(e));
    }

    /** Darker haze at night so the storm does not light up the dark. */
    private static float dim(EntityViewRenderEvent.FogColors e) {
        World w = Minecraft.getMinecraft().world;
        return w == null ? 1F : Math.max(0.12F, w.getSunBrightness((float) e.getRenderPartialTicks()));
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent e) {
        float s = level((float) e.getRenderPartialTicks());
        if (s <= 0F) return;
        float far = e.getFarPlaneDistance();
        float end = far + (Math.min(far, 28F) - far) * s; // down to ~28 blocks of visibility
        GlStateManager.setFogStart(e.getFogMode() == -1 ? 0F : end * 0.05F);
        GlStateManager.setFogEnd(end);
    }
}
