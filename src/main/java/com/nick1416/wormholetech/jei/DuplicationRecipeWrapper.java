package com.nick1416.wormholetech.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DuplicationRecipeWrapper implements IRecipeWrapper {
    private final DuplicationRecipe recipe;

    public DuplicationRecipeWrapper(DuplicationRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(
                recipe.fuels,
                Collections.singletonList(recipe.template)
        ));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String label = I18n.format(recipe.unstable
                ? "jei.wormholetech.duplication.unstable"
                : "jei.wormholetech.duplication.stable");
        minecraft.fontRenderer.drawString(label, 2, 2, 0x444444);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseY < 14) {
            return Collections.singletonList(I18n.format("jei.wormholetech.duplication.catalyst_note"));
        }
        return Collections.emptyList();
    }
}
