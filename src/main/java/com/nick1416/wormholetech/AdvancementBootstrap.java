package com.nick1416.wormholetech;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * One-time Ancient Tablet at the start of a world (delayed so Custom Starting
 * Gear can finish). Having the tablet unlocks the root advancement. Extra
 * copies: craft paper + raw naquadah.
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public class AdvancementBootstrap {

    private static final String NBT_STARTER = "wormholetech_starter_tablet_v1";
    private static final int GIFT_DELAY_TICKS = 40;

    private static final Map<UUID, Integer> PENDING = new ConcurrentHashMap<UUID, Integer>();

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        NBTTagCompound persist = getPersist(player);
        if (persist.getBoolean(NBT_STARTER)) return;
        if (hasTablet(player)) {
            markStarter(player);
            return;
        }
        PENDING.put(player.getUniqueID(), GIFT_DELAY_TICKS);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (PENDING.isEmpty()) return;

        Iterator<Map.Entry<UUID, Integer>> it = PENDING.entrySet().iterator();
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
            giveStarterTablet(player);
        }
    }

    private static void giveStarterTablet(EntityPlayerMP player) {
        NBTTagCompound persist = getPersist(player);
        if (persist.getBoolean(NBT_STARTER)) return;
        if (hasTablet(player)) {
            markStarter(player);
            return;
        }

        ItemStack stack = new ItemStack(Registration.ANCIENT_TABLET);
        if (stack.isEmpty()) return;
        if (!player.inventory.addItemStackToInventory(stack.copy())) {
            player.dropItem(stack.copy(), false);
        }
        markStarter(player);
        // Root advancement unlocks via inventory_changed when the tablet is held.
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

    private static void markStarter(EntityPlayer player) {
        NBTTagCompound persist = getPersist(player);
        persist.setBoolean(NBT_STARTER, true);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);
    }

    private static EntityPlayerMP getPlayer(UUID id) {
        net.minecraft.server.MinecraftServer server =
                net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return null;
        return server.getPlayerList().getPlayerByUUID(id);
    }
}
