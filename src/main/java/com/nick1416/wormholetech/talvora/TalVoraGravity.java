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
 * shorter jumps that still clear one block) and more fall damage.
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

    /** Utopis: crushing gravity near the black hole. */
    public static boolean utopis(EntityLivingBase e) { return ModDimensions.isUtopis(e.world.provider.getDimension()); }

    @SubscribeEvent
    public static void onUpdate(LivingEvent.LivingUpdateEvent ev) {
        EntityLivingBase e = ev.getEntityLiving();
        if (e.onGround || e.isInWater() || e.isInLava() || e.hasNoGravity() || e.isElytraFlying()) return;
        boolean heavy = on(e), light = !heavy && moon(e), crushing = utopis(e);
        if (!heavy && !light && !crushing) return;
        if (e instanceof EntityPlayer && ((EntityPlayer) e).capabilities.isFlying) return;
        if (crushing && !(e instanceof EntityPlayer) && !(e instanceof net.minecraft.entity.passive.EntityAnimal)) return; // drones are built for it
        double g = crushing ? ModConfig.utopisGravity : heavy ? ModConfig.talVoraGravity : ModConfig.selVoraGravity;
        e.motionY -= 0.08D * (g - 1.0D);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent ev) {
        if (on(ev.getEntityLiving())) ev.setDamageMultiplier(ev.getDamageMultiplier() * (float) ModConfig.talVoraFallDamageMultiplier);
        else if (utopis(ev.getEntityLiving())) ev.setDamageMultiplier(ev.getDamageMultiplier() * ModConfig.utopisFallDamageMultiplier);
        else if (moon(ev.getEntityLiving())) ev.setDamageMultiplier(ev.getDamageMultiplier() * (float) ModConfig.selVoraFallDamageMultiplier);
    }
}
