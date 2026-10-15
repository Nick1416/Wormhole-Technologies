package com.nick1416.wormholetech.mode;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;
import java.util.Set;

/**
 * Normal mode: removes the "Echo Chamber" (Wormhole Duplicator) and "Seeking Veins" (Mineral Tuner)
 * advancements, and re-parents the
 * High Energy Refiner advancement onto the Energy Converter, so the rest of the tree stays intact.
 * Applied after the server loads advancements and again after /reload.
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class NormalModeAdvancements {
    private NormalModeAdvancements() {}

    static final ResourceLocation DUPLICATOR = new ResourceLocation(WormholeTech.MODID, "wormhole_duplicator");
    static final ResourceLocation REFINER = new ResourceLocation(WormholeTech.MODID, "high_energy_refiner");
    static final ResourceLocation MINERAL_TUNER = new ResourceLocation(WormholeTech.MODID, "mineral_tuner");
    static final ResourceLocation CONVERTER = new ResourceLocation(WormholeTech.MODID, "wormhole_energy_converter");
    private static int counter;

    /** @return true when the tree was changed */
    public static boolean apply() {
        if (ModConfig.isCC()) return false;
        try {
            AdvancementList list = ObfuscationReflectionHelper.getPrivateValue(AdvancementManager.class, null, "field_192784_c");
            boolean changed = false;
            Advancement dup = list.getAdvancement(DUPLICATOR);
            if (dup != null) {
                Advancement refiner = list.getAdvancement(REFINER), conv = list.getAdvancement(CONVERTER);
                if (refiner != null && conv != null && refiner.getParent() == dup) reparent(refiner, dup, conv);
                remove(list, dup);
                WormholeTech.LOGGER.info("Normal mode: removed advancement {}, {} now follows {}", DUPLICATOR, REFINER, CONVERTER);
                changed = true;
            }
            // mineral tuners are cc-only; any child of the tuner advancement moves up to its parent
            Advancement tuner = list.getAdvancement(MINERAL_TUNER);
            if (tuner != null) {
                for (Advancement child : new java.util.ArrayList<>(childrenOf(tuner)))
                    if (tuner.getParent() != null) reparent(child, tuner, tuner.getParent());
                remove(list, tuner);
                WormholeTech.LOGGER.info("Normal mode: removed advancement {}", MINERAL_TUNER);
                changed = true;
            }
            return changed;
        } catch (RuntimeException ex) {
            WormholeTech.LOGGER.error("Normal mode: could not adjust advancements", ex);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<Advancement> childrenOf(Advancement a) {
        return (Set<Advancement>) ObfuscationReflectionHelper.getPrivateValue(Advancement.class, a, "field_192082_g");
    }

    private static void reparent(Advancement child, Advancement oldParent, Advancement newParent) {
        ObfuscationReflectionHelper.setPrivateValue(Advancement.class, child, newParent, "field_192076_a");
        childrenOf(oldParent).remove(child);
        newParent.addChild(child);
    }

    /** AdvancementList.removeAll/remove are client-only, so detach it by hand. */
    @SuppressWarnings("unchecked")
    private static void remove(AdvancementList list, Advancement adv) {
        if (adv.getParent() != null) childrenOf(adv.getParent()).remove(adv);
        ((Map<ResourceLocation, Advancement>) ObfuscationReflectionHelper.getPrivateValue(AdvancementList.class, list, "field_192092_b")).remove(adv.getId());
        ((Set<Advancement>) ObfuscationReflectionHelper.getPrivateValue(AdvancementList.class, list, "field_192093_c")).remove(adv);
        ((Set<Advancement>) ObfuscationReflectionHelper.getPrivateValue(AdvancementList.class, list, "field_192094_d")).remove(adv);
    }

    /** Catch /reload, which reloads the JSON tree. */
    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || ModConfig.isCC() || ++counter % 20 != 0) return;
        if (!apply()) return;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) for (EntityPlayerMP p : server.getPlayerList().getPlayers()) p.getAdvancements().reload();
    }
}
