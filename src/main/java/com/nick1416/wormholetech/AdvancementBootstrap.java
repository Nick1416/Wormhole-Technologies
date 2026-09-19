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
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Grants the root advancement and a one-time Ancient Tablet on first join.
 * The tablet gift is delayed so pack mods (e.g. Custom Starting Gear) can
 * finish rewriting the inventory first; the persist flag is only set after
 * the tablet is actually present (or dropped into the world).
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public class AdvancementBootstrap {

    private static final String NBT_TABLET = "wormholetech_got_ancient_tablet_v2";
    /** ~2s at 20 tps — after Custom Starting Gear / similar starter kits. */
    private static final int GIFT_DELAY_TICKS = 40;

    private static final Map<UUID, Integer> PENDING_TABLET = new ConcurrentHashMap<UUID, Integer>();

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        grant(player, "root");

        NBTTagCompound persist = getPersist(player);
        if (persist.getBoolean(NBT_TABLET)) return;
        if (hasTablet(player)) {
            markGotTablet(player);
            return;
        }
        PENDING_TABLET.put(player.getUniqueID(), GIFT_DELAY_TICKS);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (PENDING_TABLET.isEmpty()) return;

        Iterator<Map.Entry<UUID, Integer>> it = PENDING_TABLET.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> e = it.next();
            int left = e.getValue() - 1;
            if (left > 0) {
                e.setValue(left);
                continue;
            }
            it.remove();
            EntityPlayerMP player = getPlayer(e.getKey());
            if (player == null) continue;
            giveTabletOnce(player);
        }
    }

    private static EntityPlayerMP getPlayer(UUID id) {
        net.minecraft.server.MinecraftServer server =
                net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return null;
        return server.getPlayerList().getPlayerByUUID(id);
    }

    private static void giveTabletOnce(EntityPlayerMP player) {
        NBTTagCompound persist = getPersist(player);
        if (persist.getBoolean(NBT_TABLET)) return;
        if (hasTablet(player)) {
            markGotTablet(player);
            return;
        }

        ItemStack stack = new ItemStack(Registration.ANCIENT_TABLET);
        if (stack.isEmpty()) return;

        if (!player.inventory.addItemStackToInventory(stack.copy())) {
            player.dropItem(stack.copy(), false);
        }
        // Only persist after we attempted delivery (inventory or drop).
        markGotTablet(player);
    }

    private static boolean hasTablet(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack s = player.inventory.getStackInSlot(i);
            if (!s.isEmpty() && s.getItem() == Registration.ANCIENT_TABLET) return true;
        }
        return false;
    }

    private static NBTTagCompound getPersist(EntityPlayer player) {
        NBTTagCompound data = player.getEntityData();
        NBTTagCompound persist = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);
        }
        return persist;
    }

    private static void markGotTablet(EntityPlayer player) {
        NBTTagCompound persist = getPersist(player);
        persist.setBoolean(NBT_TABLET, true);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);
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
