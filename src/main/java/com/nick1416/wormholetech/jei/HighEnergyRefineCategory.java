package com.nick1416.wormholetech.jei;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.registry.Registration;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class HighEnergyRefineCategory implements IRecipeCategory<HighEnergyRefineWrapper> {

    public static final String UID = WormholeTech.MODID + ".high_energy_refine";

    private final IDrawable background;
    private final IDrawable icon;

    public HighEnergyRefineCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(120, 40);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Registration.HIGH_ENERGY_REFINER));
    }

    @Override public String getUid() { return UID; }
    @Override public String getTitle() { return I18n.format("jei.wormholetech.high_energy_refine"); }
    @Override public String getModName() { return "Wormhole Technologies"; }
    @Override public IDrawable getBackground() { return background; }
    @Nullable @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, HighEnergyRefineWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        stacks.init(0, true, 20, 16);
        stacks.init(1, false, 84, 16);
        stacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        stacks.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
