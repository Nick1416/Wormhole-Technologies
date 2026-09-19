package com.nick1416.wormholetech;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public class AdvancementBootstrap {

    private static final String NBT_TABLET = "wormholetech_got_ancient_tablet";

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        grant(player, "root");
        giveTabletOnce(player);
    }

    private static void giveTabletOnce(EntityPlayerMP player) {
        NBTTagCompound persist = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (persist.getBoolean(NBT_TABLET)) return;
        persist.setBoolean(NBT_TABLET, true);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);
        if (!player.inventory.addItemStackToInventory(new ItemStack(Registration.ANCIENT_TABLET))) {
            player.dropItem(new ItemStack(Registration.ANCIENT_TABLET), false);
        }
    }

    private static void grant(EntityPlayerMP player, String path) {
        Advancement adv = player.getServerWorld().getAdvancementManager()
                .getAdvancement(new ResourceLocation(WormholeTech.MODID, path));
        if (adv == null) return;
        AdvancementProgress progress = player.getAdvancements().getProgress(adv);
        if (progress.isDone()) return;
        for (String criterion : progress.getRemaningCriteria()) {
            player.getAdvancements().grantCriterion(adv, criterion);
        }
    }
}
