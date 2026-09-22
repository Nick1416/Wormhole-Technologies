package com.nick1416.wormholetech;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
 * Root grant (delayed for pack starter kits) plus reliable unlocks for
 * inventory/craft item advancements (JSON inventory_changed alone can miss
 * JEI/creative picks and already-held stacks).
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public class AdvancementBootstrap {

    private static final int GRANT_DELAY_TICKS = 40;
    private static final Map<UUID, Integer> PENDING_ROOT = new ConcurrentHashMap<UUID, Integer>();

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;

        Advancement root = getAdv(player, "root");
        if (root != null && !player.getAdvancements().getProgress(root).isDone()) {
            PENDING_ROOT.put(player.getUniqueID(), GRANT_DELAY_TICKS);
        }

        // After a short delay root may complete; sync item advs now and again later via pending
        syncItemAdvancements(player);
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        ItemStack out = event.crafting;
        if (out.isEmpty()) return;
        Item item = out.getItem();
        if (item == Registration.ITEM_NAQUADAH_ORE) {
            grant(player, "craft_synthetic_ore");
        } else if (item == Registration.RAW_NAQUADAH) {
            grant(player, "get_raw_naquadah");
        } else if (item == Registration.REFINED_NAQUADAH) {
            grant(player, "refined_naquadah");
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (PENDING_ROOT.isEmpty()) return;

        Iterator<Map.Entry<UUID, Integer>> it = PENDING_ROOT.entrySet().iterator();
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
            grant(player, "root");
            syncItemAdvancements(player);
        }
    }

    private static void syncItemAdvancements(EntityPlayerMP player) {
        if (hasItem(player, Registration.ITEM_NAQUADAH_ORE)) {
            grant(player, "craft_synthetic_ore");
        }
        if (hasItem(player, Registration.RAW_NAQUADAH)) {
            grant(player, "get_raw_naquadah");
        }
        if (hasItem(player, Registration.REFINED_NAQUADAH)) {
            grant(player, "refined_naquadah");
        }
    }

    private static boolean hasItem(EntityPlayerMP player, Item item) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack s = player.inventory.getStackInSlot(i);
            if (!s.isEmpty() && s.getItem() == item) return true;
        }
        return false;
    }

    private static EntityPlayerMP getPlayer(UUID id) {
        net.minecraft.server.MinecraftServer server =
                net.minecraftforge.fml.common.FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) return null;
        return server.getPlayerList().getPlayerByUUID(id);
    }

    private static Advancement getAdv(EntityPlayerMP player, String path) {
        return player.getServerWorld().getAdvancementManager()
                .getAdvancement(new ResourceLocation(WormholeTech.MODID, path));
    }

    private static void grant(EntityPlayerMP player, String path) {
        Advancement adv = getAdv(player, path);
        if (adv == null) return;
        AdvancementProgress progress = player.getAdvancements().getProgress(adv);
        if (progress.isDone()) return;
        for (String criterion : progress.getRemaningCriteria()) {
            player.getAdvancements().grantCriterion(adv, criterion);
        }
    }
}
