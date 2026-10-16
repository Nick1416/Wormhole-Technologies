package com.nick1416.wormholetech;

import com.nick1416.wormholetech.common.CommonProxy;
import com.nick1416.wormholetech.tile.GeneratorTileEntity;
import com.nick1416.wormholetech.tile.WormholePadTileEntity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(
        modid = WormholeTech.MODID,
        name = "Wormhole Technologies",
        version = WormholeTech.VERSION,
        dependencies = "after:immersiveengineering;after:jei;after:compactmachines3;after:galacticraftplanets;"
)
public class WormholeTech {
    public static final String MODID = "wormholetech";
    public static final String VERSION = "0.1.0";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @SidedProxy(
            clientSide = "com.nick1416.wormholetech.client.ClientProxy",
            serverSide = "com.nick1416.wormholetech.common.CommonProxy"
    )
    public static CommonProxy PROXY;

    @Mod.Instance
    public static WormholeTech INSTANCE;

    public static ItemStack DIAMOND_NUGGET = ItemStack.EMPTY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        ModConfig.load(e.getSuggestedConfigurationFile());
        com.nick1416.wormholetech.mode.ModeNetwork.register();

        GameRegistry.registerTileEntity(com.nick1416.wormholetech.drone.MinerDroneTileEntity.class,
                new net.minecraft.util.ResourceLocation(MODID, "miner_drone"));
        GameRegistry.registerTileEntity(com.nick1416.wormholetech.moon.NeutroniumReactorBlock.Tile.class,
                new ResourceLocation(MODID, "neutronium_reactor"));
        GameRegistry.registerTileEntity(com.nick1416.wormholetech.factory.MiniatureFactoryTileEntity.class,
                new ResourceLocation(MODID, "miniature_factory"));
        GameRegistry.registerTileEntity(GeneratorTileEntity.class,
                new ResourceLocation(MODID, "rf_generator"));

        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new com.nick1416.wormholetech.gui.GuiHandler());

        GameRegistry.registerTileEntity(
                com.nick1416.wormholetech.tile.WormholeDuplicatorTileEntity.class,
                new ResourceLocation(MODID, "wormhole_duplicator"));

        GameRegistry.registerTileEntity(
                com.nick1416.wormholetech.tile.UnstableWormholeDuplicatorTileEntity.class,
                new ResourceLocation(MODID, "unstable_wormhole_duplicator"));

        GameRegistry.registerTileEntity(
                com.nick1416.wormholetech.tile.NaquadahGeneratorTileEntity.class,
                new ResourceLocation(MODID, "naquadah_generator"));

        GameRegistry.registerTileEntity(
                WormholePadTileEntity.class,
                new ResourceLocation(MODID, "wormhole_pad"));

        GameRegistry.registerTileEntity(
                com.nick1416.wormholetech.tile.RelativisticComputerTileEntity.class,
                new ResourceLocation(MODID, "relativistic_computer"));

        GameRegistry.registerTileEntity(
                com.nick1416.wormholetech.tile.AetheriusRefinerTileEntity.class,
                new ResourceLocation(MODID, "high_energy_refiner"));

        GameRegistry.registerTileEntity(
                com.nick1416.wormholetech.tile.DialingComputerTileEntity.class,
                new ResourceLocation(MODID, "dialing_computer"));
        GameRegistry.registerTileEntity(
                com.nick1416.wormholetech.tile.GateNetworkComputerTileEntity.class,
                new ResourceLocation(MODID, "gate_network_computer"));
        GameRegistry.registerTileEntity(
                com.nick1416.wormholetech.derelict.QuantumFieldModulatorTileEntity.class,
                new ResourceLocation(MODID, "quantum_field_modulator"));
        GameRegistry.registerTileEntity(
                com.nick1416.wormholetech.knowledge.RepositoryTileEntity.class,
                new ResourceLocation(MODID, "repository_of_knowledge"));

        com.nick1416.wormholetech.world.ModDimensions.register();
        com.nick1416.wormholetech.world.ModDimensions.registerDerelict();
        com.nick1416.wormholetech.world.ModDimensions.registerTalVora();
        com.nick1416.wormholetech.world.ModDimensions.registerSelVora();
        com.nick1416.wormholetech.world.ModDimensions.registerElyndra();
        GameRegistry.registerWorldGenerator(new com.nick1416.wormholetech.world.P3XOreGenerator(), 10);

        PROXY.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        // Normal mode: bedrock reports the Hard Diamond Pickaxe (pickaxe, level 4) as its tool for HUDs
        // like The One Probe. Hardness stays -1, so only our custom mining can break it.
        if (!ModConfig.isCC()) net.minecraft.init.Blocks.BEDROCK.setHarvestLevel("pickaxe", 4);
        if (Loader.isModLoaded("thermalfoundation")) {
            Item tfItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermalfoundation", "diamond_nugget"));
            if (tfItem != null) {
                DIAMOND_NUGGET = new ItemStack(tfItem);
            }
        }

        if (DIAMOND_NUGGET.isEmpty()) {
            List<ItemStack> matches = OreDictionary.getOres("nuggetDiamond");
            if (!matches.isEmpty()) {
                DIAMOND_NUGGET = matches.get(0).copy();
            }
        }

        com.nick1416.wormholetech.world.BiomeNaquadahWastes.addTypes();
        net.minecraft.entity.EntitySpawnPlacementRegistry.setPlacementType(com.nick1416.wormholetech.entity.EntityEmeraldDrone.class,
                net.minecraft.entity.EntityLiving.SpawnPlacementType.ON_GROUND);
        if (ModConfig.emeraldDroneSpawnWeight > 0) {
            net.minecraftforge.fml.common.registry.EntityRegistry.addSpawn(com.nick1416.wormholetech.entity.EntityEmeraldDrone.class,
                    ModConfig.emeraldDroneSpawnWeight, 1, 2, net.minecraft.entity.EnumCreatureType.MONSTER,
                    com.nick1416.wormholetech.registry.Registration.NAQUADAH_WASTES);
        }
        if (ModConfig.xenolithDroneSpawnWeight > 0)
            com.nick1416.wormholetech.registry.Registration.TAL_VORA_BIOME.getSpawnableList(net.minecraft.entity.EnumCreatureType.AMBIENT).add(
                    new net.minecraft.world.biome.Biome.SpawnListEntry(com.nick1416.wormholetech.talvora.EntityXenolithDrone.class,
                            ModConfig.xenolithDroneSpawnWeight, 1, 1));
        GameRegistry.addSmelting(com.nick1416.wormholetech.registry.Registration.NEUTRONIUM_ORE,
                new ItemStack(com.nick1416.wormholetech.registry.Registration.NEUTRONIUM_INGOT), 1.0F);
        GameRegistry.addSmelting(com.nick1416.wormholetech.registry.Registration.TITANIUM_ORE,
                new ItemStack(com.nick1416.wormholetech.registry.Registration.TITANIUM_INGOT), 0.7F);
        net.minecraft.entity.EntitySpawnPlacementRegistry.setPlacementType(com.nick1416.wormholetech.moon.EntityRegolithCrawler.class,
                net.minecraft.entity.EntityLiving.SpawnPlacementType.ON_GROUND);
        if (ModConfig.regolithCrawlerSpawnWeight > 0)
            com.nick1416.wormholetech.registry.Registration.SEL_VORA_BIOME.getSpawnableList(net.minecraft.entity.EnumCreatureType.MONSTER).add(
                    new net.minecraft.world.biome.Biome.SpawnListEntry(com.nick1416.wormholetech.moon.EntityRegolithCrawler.class,
                            ModConfig.regolithCrawlerSpawnWeight, 1, 2));
        GameRegistry.addSmelting(com.nick1416.wormholetech.registry.Registration.RAW_XENOLITH,
                new ItemStack(com.nick1416.wormholetech.registry.Registration.XENOLITH), 0.2F);
        OreDictionary.registerOre("oreNaquadah", com.nick1416.wormholetech.registry.Registration.NATURAL_NAQUADAH_ORE);
        OreDictionary.registerOre("orePrysmian", com.nick1416.wormholetech.registry.Registration.PRYSMIAN_ORE);
        OreDictionary.registerOre("gemPrysmian", com.nick1416.wormholetech.registry.Registration.PRYSMIAN);
        net.minecraftforge.fml.common.registry.GameRegistry.addSmelting(com.nick1416.wormholetech.registry.Registration.ITEM_NATURAL_NAQUADAH_ORE,
                new ItemStack(com.nick1416.wormholetech.registry.Registration.REFINED_NAQUADAH), 1.0F);
        PROXY.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        // After Compact Machines 3 has loaded its own recipes (its init), before JEI starts.
        com.nick1416.wormholetech.compat.CompactMachinesCompat.registerMiniaturizationRecipes();
    }

    public static final int GUI_WORMHOLE_DUPLICATOR = 1;
    public static final int GUI_RELATIVISTIC_COMPUTER = 2;
    public static final int GUI_HIGH_ENERGY_REFINER = 3;
    public static final int GUI_ENERGY_CONVERTER = 4;
    public static final int GUI_DIALING_COMPUTER = 5;
    public static final int GUI_MINIATURE_FACTORY = 6;

    @Mod.EventHandler
    public void serverStarting(net.minecraftforge.fml.common.event.FMLServerStartingEvent e) {
        // oredict is final by now (CraftTweaker etc.)
        com.nick1416.wormholetech.stargate.TitaniumFrame.refresh();
        com.nick1416.wormholetech.mode.NormalModeAdvancements.apply();
        e.registerServerCommand(new com.nick1416.wormholetech.knowledge.CommandResearch());
    }
}
