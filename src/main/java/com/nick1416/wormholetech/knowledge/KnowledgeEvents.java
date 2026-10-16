package com.nick1416.wormholetech.knowledge;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** Every 5 s: note the planet the player stands on and any Ancient Technology they carry. */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class KnowledgeEvents {
    private KnowledgeEvents() {}
    public static final String[] TECH = { "quantum_field_modulator", "nanite_core" };

    static int techOf(Item i) {
        if (i == Registration.ITEM_QUANTUM_FIELD_MODULATOR) return 0;
        if (i == Registration.NANITE_CORE) return 1;
        return -1;
    }

    /** Address reveals driven by research percent (also used by /wormholetech research). */
    public static void checkUnlocks(EntityPlayer p, KnowledgeData d) {
        // Tal'Vora research past 50% reveals the moon's gate address (once)
        if (d.percent(p.getUniqueID(), Planets.TAL_VORA) > 50 && com.nick1416.wormholetech.world.ModDimensions.isSelVoraRegistered()) {
            if (d.discover(p.getUniqueID(), "sel_vora_address")) d.findPlanet(p.getUniqueID(), Planets.SEL_VORA);
            grant(p, "sel_vora_address"); // idempotent; also covers players who unlocked it before this advancement existed
        }
        // P3X-1416 research past 50% reveals the far address of Elyndra (once, silently: it appears on the map)
        if (d.percent(p.getUniqueID(), Planets.P3X) >= 50 && com.nick1416.wormholetech.world.ModDimensions.isElyndraRegistered()) {
            if (d.discover(p.getUniqueID(), "elyndra_address")) d.findPlanet(p.getUniqueID(), Planets.ELYNDRA);
            grant(p, "elyndra_address");
        }
    }

    private static void grant(EntityPlayer p, String id) {
        if (p instanceof EntityPlayerMP)
            com.nick1416.wormholetech.stargate.StargateTravel.grant((EntityPlayerMP) p, new net.minecraft.util.ResourceLocation(com.nick1416.wormholetech.WormholeTech.MODID, id));
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e) {
        EntityPlayer p = e.player;
        if (e.phase != TickEvent.Phase.END || p.world.isRemote || !(p instanceof EntityPlayerMP) || p.ticksExisted % 100 != 0) return;
        KnowledgeData d = KnowledgeData.get(p.world);
        int planet = Planets.indexOf(p.world.provider.getDimension());
        if (planet >= 0) d.findPlanet(p.getUniqueID(), planet);
        checkUnlocks(p, d);
        for (int s = 0; s < p.inventory.getSizeInventory(); s++) {
            ItemStack st = p.inventory.getStackInSlot(s);
            if (st.isEmpty()) continue;
            int t = techOf(st.getItem());
            if (t >= 0 && d.discover(p.getUniqueID(), TECH[t]))
                p.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation("message.wormholetech.knowledge.discovered",
                        new net.minecraft.util.text.TextComponentTranslation("gui.wormholetech.repository.tech." + TECH[t])), true);
        }
    }
}
