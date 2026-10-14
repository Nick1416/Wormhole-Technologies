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

    @SubscribeEvent
    public static void onUpdate(LivingEvent.LivingUpdateEvent ev) {
        EntityLivingBase e = ev.getEntityLiving();
        if (e.onGround || e.isInWater() || e.isInLava() || e.hasNoGravity() || e.isElytraFlying() || !on(e)) return;
        if (e instanceof EntityPlayer && ((EntityPlayer) e).capabilities.isFlying) return;
        e.motionY -= 0.08D * (ModConfig.talVoraGravity - 1.0D);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent ev) {
        if (on(ev.getEntityLiving())) ev.setDamageMultiplier(ev.getDamageMultiplier() * (float) ModConfig.talVoraFallDamageMultiplier);
    }
}
