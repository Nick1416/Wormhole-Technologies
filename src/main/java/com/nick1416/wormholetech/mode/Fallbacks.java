package com.nick1416.wormholetech.mode;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Fallback content for packs without Galacticraft / a titanium block, or without a diamond nugget.
 * Decided once, the first time a recipe condition asks (JSON recipes load after every mod's
 * preInit), so other mods' oredict entries are already known.
 */
public final class Fallbacks {
    private Fallbacks() {}

    private static Boolean nugget, frame;

    /** Mods known to add nuggetDiamond (some register their oredict late, so ask by mod id too). */
    private static final String[] NUGGET_MODS = { "thermalfoundation" };

    private static boolean otherOre(String name) {
        for (ItemStack s : OreDictionary.getOres(name, false))
            if (s.getItem() != Registration.DIAMOND_NUGGET) return true;
        return false;
    }

    public static synchronized void decide() {
        if (nugget != null) return;
        boolean nuggetMod = false;
        for (String m : NUGGET_MODS) nuggetMod |= Loader.isModLoaded(m);
        nugget = !nuggetMod && !otherOre("nuggetDiamond");
        frame = !Loader.isModLoaded("galacticraftplanets") && !otherOre("blockTitanium");
        if (nugget) {
            OreDictionary.registerOre("nuggetDiamond", Registration.DIAMOND_NUGGET);
            Registration.DIAMOND_NUGGET.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MATERIALS);
        }
        if (frame) Registration.REINFORCED_GATE_FRAME.setCreativeTab(net.minecraft.creativetab.CreativeTabs.BUILDING_BLOCKS);
        WormholeTech.LOGGER.info("Fallbacks: diamond nugget {}, gate frame block {}",
                nugget ? "ON (no nuggetDiamond found)" : "off", frame ? "ON (no Galacticraft or blockTitanium found)" : "off");
    }

    /** True when this mod's own Diamond Nugget is the pack's nuggetDiamond. */
    public static boolean nugget() { decide(); return nugget; }

    /** True when neither Galacticraft nor any blockTitanium exists, so the Reinforced Gate Frame is craftable. */
    public static boolean frame() { decide(); return frame; }

    /** Items hidden from JEI when their fallback is off. */
    public static Item[] hidden() {
        decide();
        if (nugget && frame) return new Item[0];
        if (nugget) return new Item[] { Registration.ITEM_REINFORCED_GATE_FRAME };
        if (frame) return new Item[] { Registration.DIAMOND_NUGGET };
        return new Item[] { Registration.DIAMOND_NUGGET, Registration.ITEM_REINFORCED_GATE_FRAME };
    }
}
