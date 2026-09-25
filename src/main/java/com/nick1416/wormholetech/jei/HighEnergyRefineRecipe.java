package com.nick1416.wormholetech.jei;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public final class HighEnergyRefineRecipe {
    public final ItemStack input;
    public final ItemStack output;

    public HighEnergyRefineRecipe(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public static List<HighEnergyRefineRecipe> createRecipes() {
        return Collections.singletonList(new HighEnergyRefineRecipe(
                new ItemStack(Registration.ITEM_COMPRESSED_NAQUADAH),
                new ItemStack(Registration.AETHERIUS)
        ));
    }
}
