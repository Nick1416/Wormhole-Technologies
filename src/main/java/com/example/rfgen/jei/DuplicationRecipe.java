package com.example.rfgen.jei;

import com.example.rfgen.RFGen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** One JEI entry: fuel + example template → 2× template. */
public final class DuplicationRecipe {
    public final boolean unstable;
    public final List<ItemStack> fuels;
    public final ItemStack template;
    public final ItemStack output;

    public DuplicationRecipe(boolean unstable, List<ItemStack> fuels, ItemStack template) {
        this.unstable = unstable;
        this.fuels = fuels;
        this.template = template;
        this.output = template.copy();
        this.output.setCount(2);
    }

    public static List<DuplicationRecipe> createRecipes() {
        List<DuplicationRecipe> list = new ArrayList<DuplicationRecipe>();
        ItemStack example = new ItemStack(Items.IRON_INGOT);

        list.add(new DuplicationRecipe(false, stableFuels(), example));
        list.add(new DuplicationRecipe(true, Collections.singletonList(new ItemStack(Items.DIAMOND)), example));
        return list;
    }

    private static List<ItemStack> stableFuels() {
        List<ItemStack> fuels = new ArrayList<ItemStack>();
        if (!RFGen.DIAMOND_NUGGET.isEmpty()) {
            fuels.add(RFGen.DIAMOND_NUGGET.copy());
        }
        for (ItemStack s : OreDictionary.getOres("nuggetDiamond")) {
            fuels.add(s.copy());
        }
        if (fuels.isEmpty()) {
            // Visible fallback so JEI still shows something before oredict resolves
            fuels.add(new ItemStack(Items.DIAMOND));
        }
        return fuels;
    }
}
