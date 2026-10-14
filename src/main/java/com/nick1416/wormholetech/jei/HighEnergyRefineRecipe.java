package com.nick1416.wormholetech.jei;

import com.nick1416.wormholetech.tile.RefinerRecipes;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class HighEnergyRefineRecipe {
    public final List<ItemStack> input;
    public final ItemStack output;
    public final RefinerRecipes.Recipe recipe;

    public HighEnergyRefineRecipe(RefinerRecipes.Recipe recipe) {
        this.recipe = recipe;
        this.input = recipe.inputs();
        this.output = new ItemStack(recipe.output);
    }

    /** One entry per refiner recipe that has at least one usable input in this pack. */
    public static List<HighEnergyRefineRecipe> createRecipes() {
        List<HighEnergyRefineRecipe> l = new ArrayList<HighEnergyRefineRecipe>();
        for (RefinerRecipes.Recipe r : RefinerRecipes.all()) {
            HighEnergyRefineRecipe h = new HighEnergyRefineRecipe(r);
            if (!h.input.isEmpty()) l.add(h);
        }
        return l;
    }
}
