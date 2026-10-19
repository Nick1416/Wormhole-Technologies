package com.nick1416.wormholetech.talvora;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Heavier gravity on Tal'Vora for players and mobs: extra downward pull while airborne (lower,
 * shorter jumps that still clear one block) and more fall damage. Utopis gravity is normal except
 * during short surges (see {@link #utopisSurge(long)}).
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class TalVoraGravity {
    private TalVoraGravity() {}

    public static boolean on(EntityLivingBase e) {
        return ModDimensions.isTalVoraRegistered() && e.world.provider.getDimension() == ModDimensions.talVoraId();
    }

    /** Sel'Vora, the moon: lighter gravity. */
    public static boolean moon(EntityLivingBase e) {
        return ModDimensions.isSelVoraRegistered() && e.world.provider.getDimension() == ModDimensions.selVoraId();
    }

    /** Utopis: unstable gravity near the black hole. */
    public static boolean utopis(EntityLivingBase e) { return ModDimensions.isUtopis(e.world.provider.getDimension()); }

    private static long mix(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }

    /**
     * Utopis gravity surge at world time {@code t}. One surge of utopisSurgeMinTicks..MaxTicks starts at a
     * random moment in every utopisSurgeCycleTicks window. Pure function of the world time, so the client
     * computes the same phase as the server and movement stays smooth without extra packets.
     */
    public static boolean utopisSurge(long t) {
        int cycle = Math.max(200, ModConfig.utopisSurgeCycleTicks);
        int min = Math.max(20, Math.min(ModConfig.utopisSurgeMinTicks, cycle / 2));
        int max = Math.max(min, Math.min(ModConfig.utopisSurgeMaxTicks, cycle / 2));
        long k = Math.floorDiv(t, cycle);
        long h = mix(k * 0x9E3779B97F4A7C15L + 1421L);
        int len = min + (int) Math.floorMod(h, (long) (max - min + 1));
        int gap = cycle / 3; // keeps at least this much normal gravity between two surges
        int start = gap / 2 + (int) Math.floorMod(h >>> 20, (long) (cycle - len - gap));
        long off = t - k * cycle;
        return off >= start && off < start + len;
    }

    public static boolean utopisSurge(net.minecraft.world.World w) { return utopisSurge(w.getTotalWorldTime()); }

    @SubscribeEvent
    public static void onUpdate(LivingEvent.LivingUpdateEvent ev) {
        EntityLivingBase e = ev.getEntityLiving();
        if (e.onGround || e.isInWater() || e.isInLava() || e.hasNoGravity() || e.isElytraFlying()) return;
        boolean heavy = on(e), light = !heavy && moon(e), crushing = utopis(e);
        if (!heavy && !light && !crushing) return;
        if (e instanceof EntityPlayer && ((EntityPlayer) e).capabilities.isFlying) return;
        if (crushing && !(e instanceof EntityPlayer) && !(e instanceof net.minecraft.entity.passive.EntityAnimal)) return; // drones are built for it
        if (crushing && !utopisSurge(e.world)) return; // normal between surges
        double g = crushing ? ModConfig.utopisGravity : heavy ? ModConfig.talVoraGravity : ModConfig.selVoraGravity;
        e.motionY -= 0.08D * (g - 1.0D);
    }

    /** Action-bar cue (and a low rumble) for players on Utopis when a surge starts or ends. */
    @SubscribeEvent
    public static void onPlayerTick(net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent ev) {
        if (ev.phase != net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END || ev.player.world.isRemote || !utopis(ev.player)) return;
        long t = ev.player.world.getTotalWorldTime();
        boolean now = utopisSurge(t), before = utopisSurge(t - 1);
        if (now == before) return;
        ev.player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(
                now ? "message.wormholetech.utopis.surge" : "message.wormholetech.utopis.steady"), true);
        if (ev.player instanceof net.minecraft.entity.player.EntityPlayerMP)
            ((net.minecraft.entity.player.EntityPlayerMP) ev.player).connection.sendPacket(new net.minecraft.network.play.server.SPacketSoundEffect(
                    now ? net.minecraft.init.SoundEvents.BLOCK_PORTAL_AMBIENT : net.minecraft.init.SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    net.minecraft.util.SoundCategory.AMBIENT, ev.player.posX, ev.player.posY, ev.player.posZ, 0.5F, now ? 0.5F : 0.8F));
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent ev) {
        if (on(ev.getEntityLiving())) ev.setDamageMultiplier(ev.getDamageMultiplier() * (float) ModConfig.talVoraFallDamageMultiplier);
        else if (utopis(ev.getEntityLiving()) && utopisSurge(ev.getEntityLiving().world)) ev.setDamageMultiplier(ev.getDamageMultiplier() * ModConfig.utopisFallDamageMultiplier);
        else if (moon(ev.getEntityLiving())) ev.setDamageMultiplier(ev.getDamageMultiplier() * (float) ModConfig.selVoraFallDamageMultiplier);
    }
}
