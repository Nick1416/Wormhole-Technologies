package com.nick1416.wormholetech;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
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
 * Completes the root advancement shortly after join so its Ancient Tablet
 * loot reward arrives after pack starter kits (Custom Starting Gear).
 * Root is the one-time unlock; lost tablets are restored via Advancements UI.
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
        if (root == null) return;
        if (player.getAdvancements().getProgress(root).isDone()) return;

        PENDING_ROOT.put(player.getUniqueID(), GRANT_DELAY_TICKS);
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
        }
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
