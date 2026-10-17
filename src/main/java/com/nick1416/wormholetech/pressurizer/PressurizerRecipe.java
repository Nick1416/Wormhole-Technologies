package com.nick1416.wormholetech.pressurizer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/** The one Hydrothermal Pressurizer recipe: aluminum + chromium + silicon (+ water, coal) -> emerald. */
public final class PressurizerRecipe {
    private PressurizerRecipe() {}

    public static final int TICKS = 2400;          // 2 minutes
    public static final int WATER_PER_EMERALD = 8000;   // 8 buckets
    public static final int COAL_PER_EMERALD = 8;
    public static final int TANK = 16000;

    public static final String[] ALUMINUM = {"ingotAluminum", "ingotAluminium", "dustAluminum", "dustAluminium"};
    public static final String[] CHROMIUM = {"gemChromium"};
    public static final String[] SILICON = {com.nick1416.wormholetech.tile.RefinerRecipes.SILICON, "silicon"};
    public static final String[] COAL = {"coal", "charcoal"};
    /** Mineral slot index (0..2) -> accepted oredict names. */
    public static final String[][] MINERALS = {ALUMINUM, CHROMIUM, SILICON};

    public static boolean matches(ItemStack s, String[] names) {
        if (s.isEmpty()) return false;
        int[] ids = OreDictionary.getOreIDs(s);
        for (String n : names) {
            if (!OreDictionary.doesOreNameExist(n)) continue;
            int id = OreDictionary.getOreID(n);
            for (int i : ids) if (i == id) return true;
        }
        return false;
    }

    public static boolean isCoal(ItemStack s) {
        return matches(s, COAL) || (!s.isEmpty() && s.getItem() == net.minecraft.init.Items.COAL);
    }

    public static java.util.List<ItemStack> stacks(String[] names) {
        java.util.List<ItemStack> l = new java.util.ArrayList<ItemStack>();
        for (String n : names) for (ItemStack s : OreDictionary.getOres(n, false)) l.add(s.copy());
        return l;
    }
}
