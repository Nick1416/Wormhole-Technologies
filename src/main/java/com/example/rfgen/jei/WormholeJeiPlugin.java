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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class WormholeJeiPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();
        registry.addRecipeCategories(new DuplicationRecipeCategory(guiHelper));
        registry.addRecipeCategories(new ExplosionRefineCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(DuplicationRecipe.class, DuplicationRecipeWrapper::new, DuplicationRecipeCategory.UID);
        registry.addRecipes(DuplicationRecipe.createRecipes(), DuplicationRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.WORMHOLE_DUPLICATOR), DuplicationRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.UNSTABLE_WORMHOLE_DUPLICATOR), DuplicationRecipeCategory.UID);

        registry.handleRecipes(ExplosionRefineRecipe.class, ExplosionRefineWrapper::new, ExplosionRefineCategory.UID);
        registry.addRecipes(ExplosionRefineRecipe.createRecipes(), ExplosionRefineCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(net.minecraft.init.Blocks.TNT), ExplosionRefineCategory.UID);

        info(registry, Registration.ITEM_WORMHOLE_DUPLICATOR, "wormhole_duplicator", 4);
        info(registry, Registration.ITEM_UNSTABLE_WORMHOLE_DUPLICATOR, "unstable_wormhole_duplicator", 4);
        info(registry, Registration.ITEM_RF_GENERATOR, "rf_generator", 3);
        info(registry, Registration.ITEM_UNSTABLE_WORMHOLE_ENERGY_CONVERTER, "unstable_wormhole_energy_converter", 3);
        info(registry, Registration.ITEM_NAQUADAH_GENERATOR, "naquadah_generator", 3);
        info(registry, Registration.ITEM_NAQUADAH_ORE, "naquadah_ore", 2);
        info(registry, Registration.ITEM_UNSTABLE_NAQUADAH_BLOCK, "unstable_naquadah_block", 2);
        info(registry, Registration.RAW_NAQUADAH, "raw_naquadah", 2);
        info(registry, Registration.REFINED_NAQUADAH, "refined_naquadah", 2);
        info(registry, Registration.UNSTABLE_NAQUADAH, "unstable_naquadah", 2);
        info(registry, Registration.PRYSMIAN, "prysmian", 0);
        info(registry, Registration.ITEM_PRYSMIAN_BLOCK, "prysmian_block", 0);
        info(registry, Registration.ITEM_UNSTABLE_PRYSMIAN_BLOCK, "unstable_prysmian_block", 0);
        info(registry, Registration.QUANTUM_GLUE, "quantum_glue", 1);
        info(registry, Registration.NAQUADAH_REACTOR, "naquadah_reactor", 1);
        info(registry, Registration.UNSTABLE_NAQUADAH_REACTOR, "unstable_naquadah_reactor", 0);
        info(registry, Registration.SYNTHETIC_MINERAL, "synthetic_mineral", 1);
        info(registry, Registration.MINERAL_TUNER_COAL, "mineral_tuner_coal", 3);
    }

    private static void info(IModRegistry registry, Item item, String path, int detailCount) {
        String[] keys = new String[1 + detailCount];
        keys[0] = "tooltip.rfgen." + path + ".summary";
        for (int i = 1; i <= detailCount; i++) {
            keys[i] = "tooltip.rfgen." + path + ".detail." + i;
        }
        registry.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM, keys);
    }
}
