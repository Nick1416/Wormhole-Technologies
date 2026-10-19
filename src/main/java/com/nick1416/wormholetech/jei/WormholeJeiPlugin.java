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
    private static IJeiRuntime RUNTIME;

    /** Opens JEI's recipe (uses=false) or uses (uses=true) view for the stack. Only call when JEI is loaded. */
    public static void showRecipes(ItemStack stack, boolean uses) {
        if (RUNTIME == null || stack.isEmpty()) return;
        RUNTIME.getRecipesGui().show(RUNTIME.getRecipeRegistry().createFocus(
                uses ? mezz.jei.api.recipe.IFocus.Mode.INPUT : mezz.jei.api.recipe.IFocus.Mode.OUTPUT, stack));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = helpers.getGuiHelper();
        if (com.nick1416.wormholetech.ModConfig.isCC()) registry.addRecipeCategories(new DuplicationRecipeCategory(guiHelper));
        registry.addRecipeCategories(new ExplosionRefineCategory(guiHelper));
        registry.addRecipeCategories(new OreDropCategory(guiHelper));
        registry.addRecipeCategories(new HighEnergyRefineCategory(guiHelper));
        registry.addRecipeCategories(new PressurizerCategory(guiHelper));
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
        registry.addRecipeCatalyst(new ItemStack(Registration.UNSTABLE_NAQUADAH_BLOCK), OreDropCategory.UID);

        registry.handleRecipes(PressurizerCategory.Wrapper.class, w -> w, PressurizerCategory.UID);
        registry.addRecipes(PressurizerCategory.recipes(), PressurizerCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.PRESSURIZER), PressurizerCategory.UID);
        registry.addRecipeClickArea(com.nick1416.wormholetech.pressurizer.GuiPressurizer.class, 117, 36, 24, 16, PressurizerCategory.UID);
        info(registry, Registration.ITEM_PRESSURIZER, "hydrothermal_pressurizer", 2);

        registry.handleRecipes(HighEnergyRefineRecipe.class, HighEnergyRefineWrapper::new, HighEnergyRefineCategory.UID);
        registry.addRecipes(HighEnergyRefineRecipe.createRecipes(), HighEnergyRefineCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registration.HIGH_ENERGY_REFINER), HighEnergyRefineCategory.UID);
        if (net.minecraftforge.fml.common.Loader.isModLoaded(CompactMachinesCompat.MODID))
            registry.addRecipeCatalyst(new ItemStack(Registration.MINIATURE_FACTORY), CompactMachinesCompat.JEI_CATEGORY);
        info(registry, Registration.ITEM_MINIATURE_FACTORY, "miniature_factory", 2);
        // Clicking the progress arrow in the refiner GUI opens this category.
        registry.addRecipeClickArea(com.nick1416.wormholetech.gui.GuiHighEnergyRefiner.class, 79, 36, 24, 16,
                HighEnergyRefineCategory.UID);

        info(registry, Registration.ITEM_RF_GENERATOR, "rf_generator", 0);
        info(registry, Registration.ITEM_REPOSITORY, "repository_of_knowledge", 2);
        info(registry, Registration.ITEM_REFINED_NAQUADAH_BLOCK, "refined_naquadah_block", 0);
        info(registry, Registration.ITEM_NAQUADAH_GENERATOR, "naquadah_generator", 4);
        if (com.nick1416.wormholetech.ModConfig.isCC()) {
            info(registry, Registration.ITEM_UNSTABLE_NAQUADAH_BLOCK, "unstable_naquadah_block", 2);
        } else {
            addInfo(registry, new ItemStack(Registration.ITEM_UNSTABLE_NAQUADAH_BLOCK), VanillaTypes.ITEM,
                    "tooltip.wormholetech.unstable_naquadah_block.summary",
                    "tooltip.wormholetech.unstable_naquadah_block.detail.1.normal",
                    "tooltip.wormholetech.unstable_naquadah_block.detail.2");
        }
        info(registry, Registration.ITEM_NAQUADAH_ORE, "naquadah_ore", 2);
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
        info(registry, Registration.ITEM_RELATIVISTIC_COMPUTER, "relativistic_computer", 3);
        info(registry, Registration.ITEM_COMPRESSED_NAQUADAH, "compressed_naquadah", 0);
        info(registry, Registration.ITEM_HIGH_ENERGY_REFINER, "high_energy_refiner", 0);
        info(registry, Registration.ITEM_DIALING_COMPUTER, "dialing_computer", 0);
        info(registry, Registration.TIME_DILATED_CHIP, "time_dilated_chip", 1);
        info(registry, Registration.EMERALD_NUGGET, "emerald_nugget", 1);
        addInfo(registry, com.nick1416.wormholetech.fluid.HydrothermalFluid.bucket(), VanillaTypes.FLUID,
                "jei.wormholetech.hydrothermal_fluid.info");
        addInfo(registry, net.minecraftforge.fluids.FluidUtil.getFilledBucket(com.nick1416.wormholetech.fluid.HydrothermalFluid.bucket()), VanillaTypes.ITEM,
                "jei.wormholetech.hydrothermal_fluid.info");
        info(registry, Registration.ITEM_QUANTUM_FIELD_MODULATOR, "quantum_field_modulator", 2);
        info(registry, Registration.INTERGALACTIC_COORDINATES, "intergalactic_coordinates", 1);
        info(registry, Registration.NANITE_CORE, "nanite_core", 2);
        ItemStack droneEgg = new ItemStack(net.minecraft.init.Items.SPAWN_EGG);
        net.minecraft.item.ItemMonsterPlacer.applyEntityIdToItemStack(droneEgg, new net.minecraft.util.ResourceLocation(com.nick1416.wormholetech.WormholeTech.MODID, "xenolith_drone"));
        addInfo(registry, droneEgg, VanillaTypes.ITEM, "tooltip.wormholetech.xenolith_drone.summary", "tooltip.wormholetech.xenolith_drone.detail.1");
        info(registry, Registration.ITEM_XENOLITH_ORE, "xenolith_ore", 1);
        info(registry, Registration.ITEM_TITANIUM_ORE, "titanium_ore", 1);
        info(registry, Registration.RAW_XENOLITH, "raw_xenolith", 1);
        info(registry, Registration.INACTIVE_DRONE, "inactive_xenolith_drone", 1);
        ItemStack miningEgg = new ItemStack(net.minecraft.init.Items.SPAWN_EGG);
        net.minecraft.item.ItemMonsterPlacer.applyEntityIdToItemStack(miningEgg, new net.minecraft.util.ResourceLocation(com.nick1416.wormholetech.WormholeTech.MODID, "vorani_mining_drone"));
        addInfo(registry, miningEgg, VanillaTypes.ITEM, "tooltip.wormholetech.vorani_mining_drone.summary",
                "tooltip.wormholetech.vorani_mining_drone.detail.1", "tooltip.wormholetech.vorani_mining_drone.detail.2");
        for (int m = 0; m < com.nick1416.wormholetech.drone.DroneProgram.VALUES.length; m++)
            addInfo(registry, new ItemStack(Registration.XENOLITH_CHIP, 1, m), VanillaTypes.ITEM,
                    "tooltip.wormholetech.xenolith_chip.summary", "tooltip.wormholetech.xenolith_chip.detail.1");
        for (com.nick1416.wormholetech.drone.DroneProgram p : com.nick1416.wormholetech.drone.DroneProgram.MINER_PROGRAMS)
            addInfo(registry, new ItemStack(Registration.ITEM_MINER_DRONE, 1, p.ordinal()), VanillaTypes.ITEM,
                    "tooltip.wormholetech.miner_drone.summary", "tooltip.wormholetech.miner_drone.detail.1",
                    p.whereKey(), "tooltip.wormholetech.drone_modulator");
        info(registry, Registration.ITEM_NEUTRONIUM_ORE, "neutronium_ore", 1);
        info(registry, Registration.NEUTRONIUM_INGOT, "neutronium_ingot", 0);
        info(registry, Registration.NEUTRONIUM_SUPERCONDUCTOR, "neutronium_superconductor", 0);
        addInfo(registry, new ItemStack(Registration.COMPANION_DRONE, 1, 0), VanillaTypes.ITEM,
                "tooltip.wormholetech.defensive_drone.summary", "tooltip.wormholetech.defensive_drone.detail.1", "tooltip.wormholetech.drone_modulator");
        addInfo(registry, new ItemStack(Registration.COMPANION_DRONE, 1, 1), VanillaTypes.ITEM,
                "tooltip.wormholetech.research_drone.summary", "tooltip.wormholetech.research_drone.detail.1", "tooltip.wormholetech.drone_modulator");
        info(registry, Registration.ITEM_XENOLITH, "xenolith", 1);
        info(registry, Registration.ITEM_XENOLITH_TILES, "xenolith_tiles", 1);
        info(registry, Registration.ITEM_XENO_LAMP, "xeno_lamp", 1);
        info(registry, Registration.DERELICT_COORDINATES, "derelict_coordinates", 1);
        info(registry, Registration.ITEM_ANCIENT_GATE_FRAME, "ancient_gate_frame", 0);
        info(registry, Registration.GATE_LOCATOR, "gate_locator", 0);
        info(registry, Registration.ITEM_NATURAL_NAQUADAH_ORE, "naquadah_ore_natural", 0);
        info(registry, Registration.ITEM_NAQUADAH_CRYSTAL, "naquadah_crystal", 1);
        if (!com.nick1416.wormholetech.ModConfig.isCC()) {
            addInfo(registry, new ItemStack(net.minecraft.init.Blocks.BEDROCK), VanillaTypes.ITEM,
                    "jei.wormholetech.bedrock.info");
            addInfo(registry, new ItemStack(com.nick1416.wormholetech.item.HardDiamond.PICKAXE), VanillaTypes.ITEM,
                    "jei.wormholetech.bedrock.info");
        }
        if (com.nick1416.wormholetech.ModConfig.isCC())
            for (Item hd : com.nick1416.wormholetech.item.HardDiamond.ALL)
                registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(hd));
        for (com.nick1416.wormholetech.item.ItemMineralTuner tuner : Registration.MINERAL_TUNERS) {
            if (!com.nick1416.wormholetech.ModConfig.isCC()) {
                // mode=normal: mineral tuners are not part of the game
                registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(new ItemStack(tuner));
                continue;
            }
            addInfo(registry, 
                    new ItemStack(tuner),
                    VanillaTypes.ITEM,
                    "tooltip.wormholetech.mineral_tuner.jei.1",
                    "tooltip.wormholetech.mineral_tuner.detail.1",
                    "tooltip.wormholetech.mineral_tuner.detail.2");
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        RUNTIME = runtime;
        // Hide the power-state variants of our CM3 miniaturization recipes so only one entry shows.
        IRecipeRegistry recipes = runtime.getRecipeRegistry();
        for (Object variant : CompactMachinesCompat.getHiddenVariants()) {
            IRecipeWrapper wrapper = recipes.getRecipeWrapper(variant, CompactMachinesCompat.JEI_CATEGORY);
            if (wrapper != null) {
                recipes.hideRecipe(wrapper, CompactMachinesCompat.JEI_CATEGORY);
            }
        }
    }

    /**
     * JEI shows info lines without String.format, so a lang "%%" would appear literally. Format each
     * key here (the same way item tooltips are) and hand JEI the finished text.
     */
    private static void addInfo(IModRegistry registry, ItemStack stack, Object type, String... keys) {
        registry.addIngredientInfo(stack, VanillaTypes.ITEM, lines(keys));
    }

    private static void addInfo(IModRegistry registry, net.minecraftforge.fluids.FluidStack fluid, mezz.jei.api.recipe.IIngredientType<net.minecraftforge.fluids.FluidStack> type, String... keys) {
        registry.addIngredientInfo(fluid, type, lines(keys));
    }

    private static String[] lines(String... keys) {
        String[] lines = new String[keys.length];
        for (int i = 0; i < keys.length; i++) lines[i] = net.minecraft.client.resources.I18n.format(keys[i], com.nick1416.wormholetech.util.TextArgs.args(keys[i]));
        return lines;
    }

    private static void info(IModRegistry registry, Item item, String path, int detailCount) {
        String[] keys = new String[1 + detailCount];
        keys[0] = "tooltip.wormholetech." + path + ".summary";
        for (int i = 1; i <= detailCount; i++) {
            keys[i] = "tooltip.wormholetech." + path + ".detail." + i;
        }
        addInfo(registry, new ItemStack(item), VanillaTypes.ITEM, keys);
    }
}
