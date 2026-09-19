package com.nick1416.wormholetech.jei;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public final class ExplosionRefineRecipe {
    public final ItemStack input;
    public final ItemStack output;

    public ExplosionRefineRecipe(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public static List<ExplosionRefineRecipe> createRecipes() {
        return Collections.singletonList(new ExplosionRefineRecipe(
                new ItemStack(Registration.RAW_NAQUADAH),
                new ItemStack(Registration.REFINED_NAQUADAH)
        ));
    }
}
