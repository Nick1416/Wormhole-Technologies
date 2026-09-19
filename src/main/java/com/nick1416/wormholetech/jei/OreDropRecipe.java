package com.nick1416.wormholetech.jei;

import com.nick1416.wormholetech.registry.Registration;
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
                        "jei.wormholetech.ore_drop.hint.chance",
                        "jei.wormholetech.ore_drop.tooltip.raw"),
                new OreDropRecipe(
                        new ItemStack(Registration.UNSTABLE_NAQUADAH_BLOCK),
                        new ItemStack(Registration.UNSTABLE_NAQUADAH, 4),
                        "jei.wormholetech.ore_drop.hint.always",
                        "jei.wormholetech.ore_drop.tooltip.unstable")
        );
    }
}
