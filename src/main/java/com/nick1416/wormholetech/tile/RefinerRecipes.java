package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** High Energy Refiner recipes: one input item makes one output item after a number of powered ticks. */
public final class RefinerRecipes {
    private RefinerRecipes() {}

    public static final String SILICON = "itemSilicon";

    public static final class Recipe {
        /** Exact input item, or null when {@link #ore} is used. */
        public final Item item;
        public final String ore;
        public final Item output;
        private final boolean titanium;
        private int fixedTicks;

        Recipe(Item item, String ore, Item output, boolean titanium) {
            this.item = item; this.ore = ore; this.output = output; this.titanium = titanium;
        }

        public int ticks() {
            if (fixedTicks > 0) return fixedTicks;
            return titanium ? ModConfig.highEnergyRefinerTitaniumTicks : ModConfig.highEnergyRefinerCookTimeTicks;
        }

        public boolean matches(ItemStack s) {
            if (s.isEmpty()) return false;
            if (item != null) return s.getItem() == item;
            int id = OreDictionary.getOreID(ore);
            for (int i : OreDictionary.getOreIDs(s)) if (i == id) return true;
            return false;
        }

        /** Every stack this recipe accepts (for JEI). */
        public List<ItemStack> inputs() {
            if (item != null) return Collections.singletonList(new ItemStack(item));
            List<ItemStack> l = new ArrayList<ItemStack>();
            for (ItemStack s : OreDictionary.getOres(ore, false)) l.add(s.copy());
            return l;
        }
    }

    private static List<Recipe> all;

    public static synchronized List<Recipe> all() {
        if (all == null) {
            List<Recipe> l = new ArrayList<Recipe>();
            l.add(new Recipe(Item.getItemFromBlock(Registration.COMPRESSED_NAQUADAH), null, Registration.AETHERIUS, false));
            l.add(new Recipe(null, SILICON, Registration.TITANIUM_INGOT, true));
            if (!ModConfig.isCC()) {
                Recipe hd = new Recipe(net.minecraft.init.Items.DIAMOND, null, com.nick1416.wormholetech.item.HardDiamond.GEM, false);
                hd.fixedTicks = 6000;
                l.add(hd);
            }
            all = l;
        }
        return all;
    }

    public static Recipe find(ItemStack s) {
        for (Recipe r : all()) if (r.matches(s)) return r;
        return null;
    }

    /** True when some loaded mod provides oredict itemSilicon. */
    public static boolean siliconExists() {
        return !OreDictionary.getOres(SILICON, false).isEmpty();
    }
}
