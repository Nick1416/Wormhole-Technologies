package com.nick1416.wormholetech.jei;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.registry.Registration;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.IRecipeWrapper;
import com.nick1416.wormholetech.compat.CompactMachinesCompat;
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
        if (com.nick1416.wormholetech.ModConfig.isCC()) registry.addRecipeCategories(new DuplicationRecipeCategory(guiHelper));
        registry.addRecipeCategories(new ExplosionRefineCategory(guiHelper));
        registry.addRecipeCategories(new OreDropCategory(guiHelper));
        registry.addRecipeCategories(new HighEnergyRefineCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        for (Item hide : com.nick1416.wormholetech.mode.Fallbacks.hidden())
            registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(hide));
        if (com.nick1416.wormholetech.ModConfig.isCC()) {
            registry.handleRecipes(DuplicationRecipe.class, DuplicationRecipeWrapper::new, DuplicationRecipeCategory.UID);
            registry.addRecipes(DuplicationRecipe.createRecipes(), DuplicationRecipeCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(Registration.WORMHOLE_DUPLICATOR), DuplicationRecipeCategory.UID);
            registry.addRecipeCatalyst(new ItemStack(Registration.UNSTABLE_WORMHOLE_DUPLICATOR), DuplicationRecipeCategory.UID);
            info(registry, Registration.ITEM_WORMHOLE_DUPLICATOR, "wormhole_duplicator", 0);
            info(registry, Registration.ITEM_UNSTABLE_WORMHOLE_DUPLICATOR, "unstable_wormhole_duplicator", 0);
        } else {
            // mode=normal: duplicators are not part of the game
            registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(Registration.ITEM_WORMHOLE_DUPLICATOR));
            registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(Registration.ITEM_UNSTABLE_WORMHOLE_DUPLICATOR));
        }

        registry.handleRecipes(ExplosionRefineRecipe.class, ExplosionRefineWrapper::new, ExplosionRefineCategory.UID);
        registry.addRecipes(ExplosionRefineRecipe.createRecipes(), ExplosionRefineCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(net.minecraft.init.Blocks.TNT), ExplosionRefineCategory.UID);

        registry.handleRecipes(OreDropRecipe.class, OreDropWrapper::new, OreDropCategory.UID);
        registry.addRecipes(OreDropRecipe.createRecipes(), OreDropCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.NAQUADAH_ORE), OreDropCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.UNSTABLE_NAQUADAH_BLOCK), OreDropCategory.UID);

        registry.handleRecipes(HighEnergyRefineRecipe.class, HighEnergyRefineWrapper::new, HighEnergyRefineCategory.UID);
        registry.addRecipes(HighEnergyRefineRecipe.createRecipes(), HighEnergyRefineCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.HIGH_ENERGY_REFINER), HighEnergyRefineCategory.UID);
        // Clicking the progress arrow in the refiner GUI opens this category.
        registry.addRecipeClickArea(com.nick1416.wormholetech.gui.GuiHighEnergyRefiner.class, 79, 36, 24, 16,
                HighEnergyRefineCategory.UID);

        info(registry, Registration.ITEM_RF_GENERATOR, "rf_generator", 0);
        info(registry, Registration.ITEM_REFINED_NAQUADAH_BLOCK, "refined_naquadah_block", 0);
        info(registry, Registration.ITEM_NAQUADAH_GENERATOR, "naquadah_generator", 4);
        if (com.nick1416.wormholetech.ModConfig.isCC()) {
            info(registry, Registration.ITEM_UNSTABLE_NAQUADAH_BLOCK, "unstable_naquadah_block", 2);
        } else {
            registry.addIngredientInfo(new ItemStack(Registration.ITEM_UNSTABLE_NAQUADAH_BLOCK), VanillaTypes.ITEM,
                    "tooltip.wormholetech.unstable_naquadah_block.summary",
                    "tooltip.wormholetech.unstable_naquadah_block.detail.1.normal",
                    "tooltip.wormholetech.unstable_naquadah_block.detail.2");
        }
        info(registry, Registration.RAW_NAQUADAH, "raw_naquadah", 2);
        info(registry, Registration.REFINED_NAQUADAH, "refined_naquadah", 0);
        info(registry, Registration.UNSTABLE_NAQUADAH, "unstable_naquadah", 2);
        info(registry, Registration.PRYSMIAN, "prysmian", 0);
        info(registry, Registration.ITEM_PRYSMIAN_BLOCK, "prysmian_block", 0);
        info(registry, Registration.ITEM_UNSTABLE_PRYSMIAN_BLOCK, "unstable_prysmian_block", 0);
        info(registry, Registration.QUANTUM_CIRCUIT, "quantum_circuit", 0);
        info(registry, Registration.ITEM_PRYSMIAN_LOGIC_FRAME, "prysmian_logic_frame", 0);
        info(registry, Registration.NAQUADAH_REACTOR, "naquadah_reactor", 1);
        info(registry, Registration.UNSTABLE_NAQUADAH_REACTOR, "unstable_naquadah_reactor", 0);
        info(registry, Registration.AETHERIUS, "aetherius", 1);
        info(registry, Registration.ITEM_RELATIVISTIC_COMPUTER, "relativistic_computer", 1);
        info(registry, Registration.ITEM_COMPRESSED_NAQUADAH, "compressed_naquadah", 0);
        info(registry, Registration.ITEM_HIGH_ENERGY_REFINER, "high_energy_refiner", 0);
        info(registry, Registration.ITEM_DIALING_COMPUTER, "dialing_computer", 0);
        info(registry, Registration.ITEM_GATE_NETWORK_COMPUTER, "gate_network_computer", 0);
        info(registry, Registration.ITEM_QUANTUM_FIELD_MODULATOR, "quantum_field_modulator", 1);
        info(registry, Registration.INTERGALACTIC_COORDINATES, "intergalactic_coordinates", 1);
        info(registry, Registration.NANITE_CORE, "nanite_core", 2);
        ItemStack droneEgg = new ItemStack(net.minecraft.init.Items.SPAWN_EGG);
        net.minecraft.item.ItemMonsterPlacer.applyEntityIdToItemStack(droneEgg, new net.minecraft.util.ResourceLocation(com.nick1416.wormholetech.WormholeTech.MODID, "xenolith_drone"));
        registry.addIngredientInfo(droneEgg, VanillaTypes.ITEM, "tooltip.wormholetech.xenolith_drone.summary", "tooltip.wormholetech.xenolith_drone.detail.1");
        info(registry, Registration.ITEM_XENOLITH_ORE, "xenolith_ore", 1);
        info(registry, Registration.RAW_XENOLITH, "raw_xenolith", 1);
        info(registry, Registration.INACTIVE_DRONE, "inactive_xenolith_drone", 1);
        for (int m = 0; m < com.nick1416.wormholetech.drone.DroneProgram.VALUES.length; m++)
            registry.addIngredientInfo(new ItemStack(Registration.XENOLITH_CHIP, 1, m), VanillaTypes.ITEM,
                    "tooltip.wormholetech.xenolith_chip.summary", "tooltip.wormholetech.xenolith_chip.detail.1");
        for (int m = 0; m < com.nick1416.wormholetech.drone.DroneProgram.MINERS; m++)
            registry.addIngredientInfo(new ItemStack(Registration.ITEM_MINER_DRONE, 1, m), VanillaTypes.ITEM,
                    "tooltip.wormholetech.miner_drone.summary", "tooltip.wormholetech.miner_drone.detail.1",
                    m == 0 ? "tooltip.wormholetech.miner_drone.naquadah" : "tooltip.wormholetech.miner_drone.overworld");
        registry.addIngredientInfo(new ItemStack(Registration.COMPANION_DRONE, 1, 0), VanillaTypes.ITEM,
                "tooltip.wormholetech.defensive_drone.summary", "tooltip.wormholetech.defensive_drone.detail.1");
        registry.addIngredientInfo(new ItemStack(Registration.COMPANION_DRONE, 1, 1), VanillaTypes.ITEM,
                "tooltip.wormholetech.research_drone.summary", "tooltip.wormholetech.research_drone.detail.1");
        info(registry, Registration.ITEM_XENOLITH, "xenolith", 1);
        info(registry, Registration.ITEM_XENOLITH_TILES, "xenolith_tiles", 1);
        info(registry, Registration.ITEM_XENO_LAMP, "xeno_lamp", 1);
        info(registry, Registration.DERELICT_COORDINATES, "derelict_coordinates", 1);
        info(registry, Registration.ITEM_ANCIENT_GATE_FRAME, "ancient_gate_frame", 0);
        info(registry, Registration.GATE_LOCATOR, "gate_locator", 0);
        info(registry, Registration.ITEM_NATURAL_NAQUADAH_ORE, "naquadah_ore_natural", 0);
        info(registry, Registration.ITEM_NAQUADAH_CRYSTAL, "naquadah_crystal", 1);
        for (com.nick1416.wormholetech.item.ItemMineralTuner tuner : Registration.MINERAL_TUNERS) {
            registry.addIngredientInfo(
                    new ItemStack(tuner),
                    VanillaTypes.ITEM,
                    "tooltip.wormholetech.mineral_tuner.jei.1",
                    "tooltip.wormholetech.mineral_tuner.detail.1",
                    "tooltip.wormholetech.mineral_tuner.detail.2");
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        // Hide the power-state variants of our CM3 miniaturization recipes so only one entry shows.
        IRecipeRegistry recipes = runtime.getRecipeRegistry();
        for (Object variant : CompactMachinesCompat.getHiddenVariants()) {
            IRecipeWrapper wrapper = recipes.getRecipeWrapper(variant, CompactMachinesCompat.JEI_CATEGORY);
            if (wrapper != null) {
                recipes.hideRecipe(wrapper, CompactMachinesCompat.JEI_CATEGORY);
            }
        }
    }

    private static void info(IModRegistry registry, Item item, String path, int detailCount) {
        String[] keys = new String[1 + detailCount];
        keys[0] = "tooltip.wormholetech." + path + ".summary";
        for (int i = 1; i <= detailCount; i++) {
            keys[i] = "tooltip.wormholetech." + path + ".detail." + i;
        }
        registry.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM, keys);
    }
}
