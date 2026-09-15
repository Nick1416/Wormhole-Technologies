package com.example.rfgen.jei;

import com.example.rfgen.RFGen;
import com.example.rfgen.registry.Registration;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class WormholeJeiPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();
        registry.addRecipeCategories(new DuplicationRecipeCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(DuplicationRecipe.class, DuplicationRecipeWrapper::new, DuplicationRecipeCategory.UID);
        registry.addRecipes(DuplicationRecipe.createRecipes(), DuplicationRecipeCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(Registration.WORMHOLE_DUPLICATOR), DuplicationRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.UNSTABLE_WORMHOLE_DUPLICATOR), DuplicationRecipeCategory.UID);

        registry.addIngredientInfo(
                new ItemStack(Registration.WORMHOLE_DUPLICATOR),
                VanillaTypes.ITEM,
                "tooltip.rfgen.wormhole_duplicator.summary",
                "tooltip.rfgen.wormhole_duplicator.detail.1",
                "tooltip.rfgen.wormhole_duplicator.detail.2",
                "tooltip.rfgen.wormhole_duplicator.detail.4");

        registry.addIngredientInfo(
                new ItemStack(Registration.UNSTABLE_WORMHOLE_DUPLICATOR),
                VanillaTypes.ITEM,
                "tooltip.rfgen.unstable_wormhole_duplicator.summary",
                "tooltip.rfgen.unstable_wormhole_duplicator.detail.1",
                "tooltip.rfgen.unstable_wormhole_duplicator.detail.2",
                "tooltip.rfgen.unstable_wormhole_duplicator.detail.3");
    }
}
