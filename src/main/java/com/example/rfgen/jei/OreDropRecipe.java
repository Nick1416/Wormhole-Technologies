package com.example.rfgen.jei;

import com.example.rfgen.registry.Registration;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public final class OreDropRecipe {
    public final ItemStack block;
    public final ItemStack drop;
    public final String chanceKey;
    public final String tipKey;

    public OreDropRecipe(ItemStack block, ItemStack drop, String chanceKey, String tipKey) {
        this.block = block;
        this.drop = drop;
        this.chanceKey = chanceKey;
        this.tipKey = tipKey;
    }

    public static List<OreDropRecipe> createRecipes() {
        return Arrays.asList(
                new OreDropRecipe(
                        new ItemStack(Registration.NAQUADAH_ORE),
                        new ItemStack(Registration.RAW_NAQUADAH),
                        "jei.rfgen.ore_drop.hint.chance",
                        "jei.rfgen.ore_drop.tooltip.raw"),
                new OreDropRecipe(
                        new ItemStack(Registration.UNSTABLE_NAQUADAH_BLOCK),
                        new ItemStack(Registration.UNSTABLE_NAQUADAH, 4),
                        "jei.rfgen.ore_drop.hint.always",
                        "jei.rfgen.ore_drop.tooltip.unstable")
        );
    }
}
