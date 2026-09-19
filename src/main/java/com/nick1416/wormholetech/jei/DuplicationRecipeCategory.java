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
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class DuplicationRecipeCategory implements IRecipeCategory<DuplicationRecipeWrapper> {

    public static final String UID = WormholeTech.MODID + ".duplication";
    private static final ResourceLocation BG =
            new ResourceLocation(WormholeTech.MODID, "textures/gui/wormhole_duplicator.png");

    private final IDrawable background;
    private final IDrawable icon;

    public DuplicationRecipeCategory(IGuiHelper guiHelper) {
        // Crop the machine-slot band from the duplicator GUI texture
        this.background = guiHelper.createDrawable(BG, 25, 16, 126, 54);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Registration.WORMHOLE_DUPLICATOR));
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return I18n.format("jei.wormholetech.duplication");
    }

    @Override
    public String getModName() {
        return "Wormhole Technologies";
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DuplicationRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        // Texture crop starts at (25,16); container slots at (44,35), (80,35), (116,35)
        // Relative to crop: (19,19), (55,19), (91,19)
        stacks.init(0, true, 19, 19);  // fuel
        stacks.init(1, true, 55, 19);  // template
        stacks.init(2, false, 91, 19); // output

        stacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        stacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(1));
        stacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
