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
 * Ensures the root advancement is visible, then (once) completes the
 * Ancient Tablet advancement so its loot reward delivers the item.
 * Delayed so pack starter kits (Custom Starting Gear) finish first.
 * The advancement itself is the one-time gate — losing the tablet does not grant another.
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public class AdvancementBootstrap {

    /** ~2s at 20 tps — after Custom Starting Gear / similar starter kits. */
    private static final int GRANT_DELAY_TICKS = 40;

    private static final Map<UUID, Integer> PENDING_TABLET_ADV = new ConcurrentHashMap<UUID, Integer>();

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        grant(player, "root");

        Advancement tablet = getAdv(player, "ancient_tablet");
        if (tablet == null) return;
        if (player.getAdvancements().getProgress(tablet).isDone()) return;

        PENDING_TABLET_ADV.put(player.getUniqueID(), GRANT_DELAY_TICKS);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (PENDING_TABLET_ADV.isEmpty()) return;

        Iterator<Map.Entry<UUID, Integer>> it = PENDING_TABLET_ADV.entrySet().iterator();
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
            grant(player, "ancient_tablet");
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
