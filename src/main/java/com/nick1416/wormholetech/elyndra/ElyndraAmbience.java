package com.nick1416.wormholetech.elyndra;

import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

/** Client-only life on Elyndra: fireflies at night, drifting motes by day, and the odd bird call. Cheap: a few particles per tick. */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = com.nick1416.wormholetech.WormholeTech.MODID)
public final class ElyndraAmbience {
    private static final Random R = new Random();

    private ElyndraAmbience() {}

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.player;
        if (p == null || mc.world == null || mc.isGamePaused() || !ModDimensions.isElyndra(mc.world.provider.getDimension())) return;
        boolean night = !mc.world.isDaytime();
        for (int i = 0; i < (night ? 3 : 1); i++) {
            double x = p.posX + (R.nextDouble() - 0.5) * 32, z = p.posZ + (R.nextDouble() - 0.5) * 32;
            double y = p.posY + (R.nextDouble() - 0.3) * 10;
            if (!mc.world.isAirBlock(new BlockPos(x, y, z))) continue;
            if (night) // fireflies: yellow-green dust (REDSTONE takes colour from the speed values)
                mc.world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.75, 1.0, 0.25);
            else if (R.nextInt(3) == 0)
                mc.world.spawnParticle(EnumParticleTypes.END_ROD, x, y, z, (R.nextDouble() - 0.5) * 0.01, 0.005, (R.nextDouble() - 0.5) * 0.01);
        }
        if (!night && R.nextInt(400) == 0)
            mc.world.playSound(p.posX + (R.nextDouble() - 0.5) * 24, p.posY + 6, p.posZ + (R.nextDouble() - 0.5) * 24,
                    SoundEvents.ENTITY_PARROT_AMBIENT, SoundCategory.AMBIENT, 0.4F, 1.2F + R.nextFloat() * 0.4F, false);
    }
}
