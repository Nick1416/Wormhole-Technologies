package com.example.rfgen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = RFGen.MODID)
public class AdvancementBootstrap {

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        grant(player, "root");
    }

    private static void grant(EntityPlayerMP player, String path) {
        Advancement adv = player.getServerWorld().getAdvancementManager()
                .getAdvancement(new ResourceLocation(RFGen.MODID, path));
        if (adv == null) return;
        AdvancementProgress progress = player.getAdvancements().getProgress(adv);
        if (progress.isDone()) return;
        for (String criterion : progress.getRemaningCriteria()) {
            player.getAdvancements().grantCriterion(adv, criterion);
        }
    }
}
