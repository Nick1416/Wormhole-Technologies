package com.example.rfgen.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.Collections;
import java.util.List;

public class ExplosionRefineWrapper implements IRecipeWrapper {
    private final ExplosionRefineRecipe recipe;

    public ExplosionRefineWrapper(ExplosionRefineRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.input);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString(I18n.format("jei.rfgen.explosion_refine.hint"), 2, 2, 0x444444);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseY < 14) {
            return Collections.singletonList(I18n.format("jei.rfgen.explosion_refine.tooltip"));
        }
        return Collections.emptyList();
    }
}
