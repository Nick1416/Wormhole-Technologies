package com.nick1416.wormholetech.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import com.nick1416.wormholetech.ModConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HighEnergyRefineWrapper implements IRecipeWrapper {
    private final HighEnergyRefineRecipe recipe;

    public HighEnergyRefineRecipe recipe() { return recipe; }

    public HighEnergyRefineWrapper(HighEnergyRefineRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.input));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
        if (recipe.fluid != null) ingredients.setInput(VanillaTypes.FLUID, recipe.fluid);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString(I18n.format("jei.wormholetech.high_energy_refine.hint",
                com.nick1416.wormholetech.util.TextArgs.rf(ModConfig.highEnergyRefinerRfPerTick), formatTime(recipe.recipe.ticks())),
                2, 2, 0x444444);
    }

    /** Ticks to a short human-readable duration at 20 TPS ("15 min", "40 s"). */
    static String formatTime(int ticks) {
        return com.nick1416.wormholetech.util.TextArgs.duration(ticks);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseY < 14) {
            int ticks = recipe.recipe.ticks();
            return Arrays.asList(
                    I18n.format("jei.wormholetech.high_energy_refine.tooltip"),
                    I18n.format("jei.wormholetech.high_energy_refine.time", String.format("%,d", ticks), formatTime(ticks)),
                    I18n.format("jei.wormholetech.high_energy_refine.energy",
                            String.format("%,d", (long) ticks * ModConfig.highEnergyRefinerRfPerTick)));
        }
        return Collections.emptyList();
    }
}
