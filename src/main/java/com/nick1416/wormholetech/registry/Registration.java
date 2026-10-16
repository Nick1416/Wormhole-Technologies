package com.nick1416.wormholetech.registry;

import com.nick1416.wormholetech.block.NaquadahOreBlock;
import com.nick1416.wormholetech.block.PrysmianBlock;
import com.nick1416.wormholetech.block.PrysmianLogicFrameBlock;
import com.nick1416.wormholetech.block.UnstablePrysmianBlock;
import com.nick1416.wormholetech.block.UnstableNaquadahBlock;
import com.nick1416.wormholetech.block.WormholeDuplicatorBlock;
import com.nick1416.wormholetech.block.UnstableWormholeDuplicatorBlock;
import com.nick1416.wormholetech.item.ItemAncientTablet;
import com.nick1416.wormholetech.item.ItemMineralTuner;
import com.nick1416.wormholetech.item.ItemWormholeLinker;
import com.nick1416.wormholetech.block.WormholePadBlock;
import com.nick1416.wormholetech.block.RelativisticComputerBlock;
import com.nick1416.wormholetech.block.CompressedNaquadahBlock;
import com.nick1416.wormholetech.block.AetheriusRefinerBlock;

import net.minecraft.item.Item;
import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.block.GeneratorBlock;
import com.nick1416.wormholetech.block.NaquadahGenerator;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public class Registration {

	// -- Block of Prysmian --
	public static final Block PRYSMIAN_BLOCK = new PrysmianBlock()
			.setRegistryName(WormholeTech.MODID, "prysmian_block")
			.setUnlocalizedName(WormholeTech.MODID + ".prysmian_block");
	
	public static final Item ITEM_PRYSMIAN_BLOCK = 
			new ItemBlock(PRYSMIAN_BLOCK).setRegistryName(PRYSMIAN_BLOCK.getRegistryName());
	
	// -- Prysmian Logic Frame (advanced tier) --
	public static final Block PRYSMIAN_LOGIC_FRAME = new PrysmianLogicFrameBlock()
			.setRegistryName(WormholeTech.MODID, "prysmian_logic_frame")
			.setUnlocalizedName(WormholeTech.MODID + ".prysmian_logic_frame");

	public static final Item ITEM_PRYSMIAN_LOGIC_FRAME =
			new ItemBlock(PRYSMIAN_LOGIC_FRAME).setRegistryName(PRYSMIAN_LOGIC_FRAME.getRegistryName());

	// -- Unstable Block of Prysmian --
	public static final Block UNSTABLE_PRYSMIAN_BLOCK = new UnstablePrysmianBlock()
			.setRegistryName(WormholeTech.MODID, "unstable_prysmian_block")
			.setUnlocalizedName(WormholeTech.MODID + ".unstable_prysmian_block");
	
	public static final Item ITEM_UNSTABLE_PRYSMIAN_BLOCK = 
			new ItemBlock(UNSTABLE_PRYSMIAN_BLOCK).setRegistryName(UNSTABLE_PRYSMIAN_BLOCK.getRegistryName());
			
    // --- Wormhole Energy Converter ---
    public static final Block RF_GENERATOR = new GeneratorBlock()
            .setRegistryName(WormholeTech.MODID, "rf_generator")
            .setUnlocalizedName(WormholeTech.MODID + ".rf_generator");

    public static final Item ITEM_RF_GENERATOR =
            new ItemBlock(RF_GENERATOR).setRegistryName(RF_GENERATOR.getRegistryName());
    
    // -- Wormhole Duplicator -- 
    public static final Block WORMHOLE_DUPLICATOR = new WormholeDuplicatorBlock()
    	    .setRegistryName(WormholeTech.MODID, "wormhole_duplicator")
    	    .setUnlocalizedName(WormholeTech.MODID + ".wormhole_duplicator");
    
    // -- Unstable Wormhole Duplicator -- 
    public static final Block UNSTABLE_WORMHOLE_DUPLICATOR = new UnstableWormholeDuplicatorBlock()
    	    .setRegistryName(WormholeTech.MODID, "unstable_wormhole_duplicator")
    	    .setUnlocalizedName(WormholeTech.MODID + ".unstable_wormhole_duplicator");
    
    public static final Item ITEM_UNSTABLE_WORMHOLE_DUPLICATOR =
    	    new ItemBlock(UNSTABLE_WORMHOLE_DUPLICATOR).setRegistryName(UNSTABLE_WORMHOLE_DUPLICATOR.getRegistryName());

	public static final Item ITEM_WORMHOLE_DUPLICATOR =
	    new ItemBlock(WORMHOLE_DUPLICATOR).setRegistryName(WORMHOLE_DUPLICATOR.getRegistryName());
    
    
    // -- Naquadah Generator --
    public static final Block NAQUADAH_GENERATOR = new NaquadahGenerator()
    		.setRegistryName(WormholeTech.MODID, "naquadah_generator")
    		.setUnlocalizedName(WormholeTech.MODID + ".naquadah_generator");
    
    public static final Item ITEM_NAQUADAH_GENERATOR=
    		new ItemBlock(NAQUADAH_GENERATOR).setRegistryName(NAQUADAH_GENERATOR.getRegistryName());

    // --- Naquadah Ore ---
    public static final Block NAQUADAH_ORE = new NaquadahOreBlock()
            .setRegistryName(WormholeTech.MODID, "naquadah_ore")
            .setUnlocalizedName(WormholeTech.MODID + ".naquadah_ore");

    public static final Item ITEM_NAQUADAH_ORE =
            new ItemBlock(NAQUADAH_ORE).setRegistryName(NAQUADAH_ORE.getRegistryName());
    
    // -- Unstable Block of Naquadah --
    public static final Block UNSTABLE_NAQUADAH_BLOCK = new UnstableNaquadahBlock()
            .setRegistryName(WormholeTech.MODID, "unstable_naquadah_block")
            .setUnlocalizedName(WormholeTech.MODID + ".unstable_naquadah_block");

    public static final Item ITEM_UNSTABLE_NAQUADAH_BLOCK =
            new ItemBlock(UNSTABLE_NAQUADAH_BLOCK).setRegistryName(UNSTABLE_NAQUADAH_BLOCK.getRegistryName());
    
    // -- Block of Refined Naquadah (wormhole connection fuel) --
    public static final Block REFINED_NAQUADAH_BLOCK = new com.nick1416.wormholetech.block.RefinedNaquadahBlock()
            .setRegistryName(WormholeTech.MODID, "refined_naquadah_block")
            .setUnlocalizedName(WormholeTech.MODID + ".refined_naquadah_block");

    public static final Item ITEM_REFINED_NAQUADAH_BLOCK =
            new com.nick1416.wormholetech.item.ItemRefinedNaquadahBlock(REFINED_NAQUADAH_BLOCK)
                    .setRegistryName(REFINED_NAQUADAH_BLOCK.getRegistryName());

    // --- Raw Naquadah ---
    public static final Item RAW_NAQUADAH = new Item()
    		.setRegistryName(WormholeTech.MODID, "raw_naquadah")
    		.setUnlocalizedName(WormholeTech.MODID + ".raw_naquadah");

    // --- Refined Naquadah ---
    public static final Item REFINED_NAQUADAH = new Item()
    		.setRegistryName(WormholeTech.MODID, "refined_naquadah")
    		.setUnlocalizedName(WormholeTech.MODID + ".refined_naquadah");
    
    // --- Prysmian ---
    public static final Item PRYSMIAN = new Item()
    		.setRegistryName(WormholeTech.MODID, "prysmian")
    		.setUnlocalizedName(WormholeTech.MODID + ".prysmian");
    
    /** Fallback nuggetDiamond when no other mod provides one (see Fallbacks). */
    public static final Item DIAMOND_NUGGET = new Item()
            .setRegistryName(WormholeTech.MODID, "diamond_nugget")
            .setUnlocalizedName(WormholeTech.MODID + ".diamond_nugget");

    // --- Titanium (refined from silicon) ---
    public static final Item TITANIUM_INGOT = new Item()
            .setRegistryName(WormholeTech.MODID, "titanium_ingot")
            .setUnlocalizedName(WormholeTech.MODID + ".titanium_ingot");

    public static final Block TITANIUM_BLOCK = new Block(net.minecraft.block.material.Material.IRON)
            .setHardness(5.0F).setResistance(30.0F)
            .setRegistryName(WormholeTech.MODID, "titanium_block")
            .setUnlocalizedName(WormholeTech.MODID + ".titanium_block");
    public static final Item ITEM_TITANIUM_BLOCK =
            new ItemBlock(TITANIUM_BLOCK).setRegistryName(TITANIUM_BLOCK.getRegistryName());

    // --- Derelict vessel ---
    public static final Item DERELICT_COORDINATES = new Item().setMaxStackSize(1)
            .setRegistryName(WormholeTech.MODID, "derelict_coordinates")
            .setUnlocalizedName(WormholeTech.MODID + ".derelict_coordinates");

    public static final Item INTERGALACTIC_COORDINATES = new Item().setMaxStackSize(1)
            .setRegistryName(WormholeTech.MODID, "intergalactic_coordinates")
            .setUnlocalizedName(WormholeTech.MODID + ".intergalactic_coordinates");
    public static final Item NANITE_CORE = new Item()
            .setRegistryName(WormholeTech.MODID, "nanite_core")
            .setUnlocalizedName(WormholeTech.MODID + ".nanite_core");

    public static final Block HULL_PLATING = new com.nick1416.wormholetech.derelict.HullBlock(net.minecraft.block.material.MapColor.GRAY, 0F)
            .setRegistryName(WormholeTech.MODID, "hull_plating").setUnlocalizedName(WormholeTech.MODID + ".hull_plating");
    public static final Item ITEM_HULL_PLATING = new ItemBlock(HULL_PLATING).setRegistryName(HULL_PLATING.getRegistryName());
    public static final Block HULL_BULKHEAD = new com.nick1416.wormholetech.derelict.HullBlock(net.minecraft.block.material.MapColor.BLACK, 0F)
            .setRegistryName(WormholeTech.MODID, "hull_bulkhead").setUnlocalizedName(WormholeTech.MODID + ".hull_bulkhead");
    public static final Item ITEM_HULL_BULKHEAD = new ItemBlock(HULL_BULKHEAD).setRegistryName(HULL_BULKHEAD.getRegistryName());
    public static final Block HULL_LAMP = new com.nick1416.wormholetech.derelict.HullBlock(net.minecraft.block.material.MapColor.SNOW, 1.0F)
            .setRegistryName(WormholeTech.MODID, "hull_lamp").setUnlocalizedName(WormholeTech.MODID + ".hull_lamp");
    public static final Item ITEM_HULL_LAMP = new ItemBlock(HULL_LAMP).setRegistryName(HULL_LAMP.getRegistryName());
    public static final Block HULL_LAMP_GOLD = new com.nick1416.wormholetech.derelict.HullBlock(net.minecraft.block.material.MapColor.GOLD, 1.0F)
            .setRegistryName(WormholeTech.MODID, "hull_lamp_gold").setUnlocalizedName(WormholeTech.MODID + ".hull_lamp_gold");
    public static final Item ITEM_HULL_LAMP_GOLD = new ItemBlock(HULL_LAMP_GOLD).setRegistryName(HULL_LAMP_GOLD.getRegistryName());
    public static final Block HULL_WINDOW = new com.nick1416.wormholetech.derelict.HullWindowBlock()
            .setRegistryName(WormholeTech.MODID, "hull_window").setUnlocalizedName(WormholeTech.MODID + ".hull_window");
    public static final Item ITEM_HULL_WINDOW = new ItemBlock(HULL_WINDOW).setRegistryName(HULL_WINDOW.getRegistryName());
    public static final Block MODULATOR_CRADLE = new com.nick1416.wormholetech.derelict.HullBlock(net.minecraft.block.material.MapColor.GOLD, 0.4F)
            .setRegistryName(WormholeTech.MODID, "modulator_cradle").setUnlocalizedName(WormholeTech.MODID + ".modulator_cradle");
    public static final Item ITEM_MODULATOR_CRADLE = new ItemBlock(MODULATOR_CRADLE).setRegistryName(MODULATOR_CRADLE.getRegistryName());
    public static final Block QUANTUM_FIELD_MODULATOR = new com.nick1416.wormholetech.derelict.QuantumFieldModulatorBlock()
            .setRegistryName(WormholeTech.MODID, "quantum_field_modulator").setUnlocalizedName(WormholeTech.MODID + ".quantum_field_modulator");
    public static final Block VORANI_MODULATOR = new com.nick1416.wormholetech.talvora.VoraniModulatorBlock()
            .setRegistryName(WormholeTech.MODID, "vorani_modulator").setUnlocalizedName(WormholeTech.MODID + ".vorani_modulator");
    public static final Block XENOLITH_ORE = new com.nick1416.wormholetech.drone.XenolithOreBlock()
            .setRegistryName(WormholeTech.MODID, "xenolith_ore").setUnlocalizedName(WormholeTech.MODID + ".xenolith_ore");
    public static final Item ITEM_XENOLITH_ORE = new ItemBlock(XENOLITH_ORE).setRegistryName(XENOLITH_ORE.getRegistryName());
    public static final Item RAW_XENOLITH = new Item().setRegistryName(WormholeTech.MODID, "raw_xenolith").setUnlocalizedName(WormholeTech.MODID + ".raw_xenolith");
    public static final Item INACTIVE_DRONE = new Item().setMaxStackSize(16)
            .setRegistryName(WormholeTech.MODID, "inactive_xenolith_drone").setUnlocalizedName(WormholeTech.MODID + ".inactive_xenolith_drone");
    public static final Item XENOLITH_CHIP = new com.nick1416.wormholetech.drone.ItemXenolithChip()
            .setRegistryName(WormholeTech.MODID, "xenolith_chip").setUnlocalizedName(WormholeTech.MODID + ".xenolith_chip");
    public static final Block MINER_DRONE = new com.nick1416.wormholetech.drone.MinerDroneBlock()
            .setRegistryName(WormholeTech.MODID, "miner_drone").setUnlocalizedName(WormholeTech.MODID + ".miner_drone");
    public static final Item ITEM_MINER_DRONE = new com.nick1416.wormholetech.drone.ItemMinerDrone(MINER_DRONE).setRegistryName(MINER_DRONE.getRegistryName());
    public static final Item COMPANION_DRONE = new com.nick1416.wormholetech.drone.ItemCompanionDrone()
            .setRegistryName(WormholeTech.MODID, "companion_drone").setUnlocalizedName(WormholeTech.MODID + ".companion_drone");
    public static final Item ITEM_QUANTUM_FIELD_MODULATOR = new ItemBlock(QUANTUM_FIELD_MODULATOR).setMaxStackSize(1)
            .setRegistryName(QUANTUM_FIELD_MODULATOR.getRegistryName());

    // --- Sel'Vora (Tal'Vora's moon) ---
    public static final Block MOON_REGOLITH = new com.nick1416.wormholetech.moon.MoonBlocks.Regolith()
            .setRegistryName(WormholeTech.MODID, "moon_regolith").setUnlocalizedName(WormholeTech.MODID + ".moon_regolith");
    public static final Item ITEM_MOON_REGOLITH = new ItemBlock(MOON_REGOLITH).setRegistryName(MOON_REGOLITH.getRegistryName());
    public static final Block TITANIUM_ORE = new com.nick1416.wormholetech.moon.MoonBlocks.TitaniumOre()
            .setRegistryName(WormholeTech.MODID, "titanium_ore").setUnlocalizedName(WormholeTech.MODID + ".titanium_ore");
    public static final Item ITEM_TITANIUM_ORE = new ItemBlock(TITANIUM_ORE).setRegistryName(TITANIUM_ORE.getRegistryName());
    public static final Block RESEARCH_CONSOLE = new com.nick1416.wormholetech.moon.MoonBlocks.ResearchConsole()
            .setRegistryName(WormholeTech.MODID, "research_console").setUnlocalizedName(WormholeTech.MODID + ".research_console");
    public static final Block NEUTRONIUM_ORE = new com.nick1416.wormholetech.moon.MoonBlocks.TitaniumOre()
            .setRegistryName(WormholeTech.MODID, "neutronium_ore").setUnlocalizedName(WormholeTech.MODID + ".neutronium_ore");
    public static final Item ITEM_NEUTRONIUM_ORE = new ItemBlock(NEUTRONIUM_ORE).setRegistryName(NEUTRONIUM_ORE.getRegistryName());
    public static final Item NEUTRONIUM_INGOT = new Item()
            .setRegistryName(WormholeTech.MODID, "neutronium_ingot").setUnlocalizedName(WormholeTech.MODID + ".neutronium_ingot");
    public static final Block NEUTRONIUM_SUPERCONDUCTOR_BLOCK = new Block(net.minecraft.block.material.Material.IRON, net.minecraft.block.material.MapColor.LIGHT_BLUE)
            .setHardness(4.0F).setResistance(20.0F).setLightLevel(0.4F)
            .setRegistryName(WormholeTech.MODID, "neutronium_superconductor").setUnlocalizedName(WormholeTech.MODID + ".neutronium_superconductor");
    public static final Item NEUTRONIUM_SUPERCONDUCTOR = new ItemBlock(NEUTRONIUM_SUPERCONDUCTOR_BLOCK).setRegistryName(NEUTRONIUM_SUPERCONDUCTOR_BLOCK.getRegistryName());
    public static final Block NEUTRONIUM_REACTOR = new com.nick1416.wormholetech.moon.NeutroniumReactorBlock()
            .setRegistryName(WormholeTech.MODID, "neutronium_reactor").setUnlocalizedName(WormholeTech.MODID + ".neutronium_reactor");
    public static final Block MINIATURE_FACTORY = new com.nick1416.wormholetech.factory.MiniatureFactoryBlock()
            .setRegistryName(WormholeTech.MODID, "miniature_factory").setUnlocalizedName(WormholeTech.MODID + ".miniature_factory");
    public static final Item ITEM_MINIATURE_FACTORY = new ItemBlock(MINIATURE_FACTORY).setRegistryName(MINIATURE_FACTORY.getRegistryName());
    public static final net.minecraft.world.biome.Biome ELYNDRA_BIOME =
            new com.nick1416.wormholetech.elyndra.BiomeElyndra().setRegistryName(WormholeTech.MODID, "elyndra_wilds");
    public static final net.minecraft.world.biome.Biome SEL_VORA_BIOME =
            new com.nick1416.wormholetech.moon.BiomeSelVora().setRegistryName(WormholeTech.MODID, "sel_vora_highlands");

    public static final net.minecraft.world.biome.Biome TAL_VORA_BIOME =
            new com.nick1416.wormholetech.talvora.BiomeTalVora().setRegistryName(WormholeTech.MODID, "tal_vora_dunes");
    public static final Block XENOLITH = new Block(net.minecraft.block.material.Material.ROCK, net.minecraft.block.material.MapColor.CYAN)
            .setHardness(2.0F).setResistance(12.0F).setRegistryName(WormholeTech.MODID, "xenolith").setUnlocalizedName(WormholeTech.MODID + ".xenolith");
    public static final Item ITEM_XENOLITH = new ItemBlock(XENOLITH).setRegistryName(XENOLITH.getRegistryName());
    public static final Block XENOLITH_TILES = new Block(net.minecraft.block.material.Material.ROCK, net.minecraft.block.material.MapColor.CYAN)
            .setHardness(2.0F).setResistance(12.0F).setRegistryName(WormholeTech.MODID, "xenolith_tiles").setUnlocalizedName(WormholeTech.MODID + ".xenolith_tiles");
    public static final Item ITEM_XENOLITH_TILES = new ItemBlock(XENOLITH_TILES).setRegistryName(XENOLITH_TILES.getRegistryName());
    public static final Block REPOSITORY = new com.nick1416.wormholetech.knowledge.RepositoryBlock()
            .setRegistryName(WormholeTech.MODID, "repository_of_knowledge").setUnlocalizedName(WormholeTech.MODID + ".repository_of_knowledge");
    public static final Item ITEM_REPOSITORY = new ItemBlock(REPOSITORY).setRegistryName(REPOSITORY.getRegistryName());
    public static final Block XENO_LAMP = new Block(net.minecraft.block.material.Material.GLASS, net.minecraft.block.material.MapColor.PURPLE)
            .setHardness(0.6F).setLightLevel(1.0F).setRegistryName(WormholeTech.MODID, "xeno_lamp").setUnlocalizedName(WormholeTech.MODID + ".xeno_lamp");
    public static final Item ITEM_XENO_LAMP = new ItemBlock(XENO_LAMP).setRegistryName(XENO_LAMP.getRegistryName());

    public static final net.minecraft.world.biome.Biome DERELICT_BIOME =
            new com.nick1416.wormholetech.derelict.BiomeDerelict().setRegistryName(WormholeTech.MODID, "derelict_vessel");

    // --- Unstable Naquadah ---
    public static final Item UNSTABLE_NAQUADAH = new Item()
    		.setRegistryName(WormholeTech.MODID, "unstable_naquadah")
    		.setUnlocalizedName(WormholeTech.MODID + ".unstable_naquadah");
    
    
    // --- Quantum Circuit (Compact Machines miniaturization) ---
    public static final Item QUANTUM_CIRCUIT = new Item()
    		.setRegistryName(WormholeTech.MODID, "quantum_circuit")
    		.setUnlocalizedName(WormholeTech.MODID + ".quantum_circuit")
    		.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MATERIALS);

    // --- Naquadah Reactor
    public static final Item NAQUADAH_REACTOR = new Item()
    		.setRegistryName(WormholeTech.MODID, "naquadah_reactor")
    		.setUnlocalizedName(WormholeTech.MODID + ".naquadah_reactor");
    

    // -- Mineral Tuners (IE excavator) --
    public static final ItemMineralTuner MINERAL_TUNER_COAL = new ItemMineralTuner("coal", "Coal");
    public static final ItemMineralTuner MINERAL_TUNER_IRON = new ItemMineralTuner("iron", "Iron");
    public static final ItemMineralTuner MINERAL_TUNER_MAGNETITE = new ItemMineralTuner("magnetite", "Magnetite");
    public static final ItemMineralTuner MINERAL_TUNER_PYRITE = new ItemMineralTuner("pyrite", "Pyrite");
    public static final ItemMineralTuner MINERAL_TUNER_BAUXITE = new ItemMineralTuner("bauxite", "Bauxite");
    public static final ItemMineralTuner MINERAL_TUNER_COPPER = new ItemMineralTuner("copper", "Copper");
    public static final ItemMineralTuner MINERAL_TUNER_CASSITERITE = new ItemMineralTuner("cassiterite", "Cassiterite");
    public static final ItemMineralTuner MINERAL_TUNER_GOLD = new ItemMineralTuner("gold", "Gold");
    public static final ItemMineralTuner MINERAL_TUNER_NICKEL = new ItemMineralTuner("nickel", "Nickel");
    public static final ItemMineralTuner MINERAL_TUNER_PLATINUM = new ItemMineralTuner("platinum", "Platinum");
    public static final ItemMineralTuner MINERAL_TUNER_URANIUM = new ItemMineralTuner("uranium", "Uranium");
    public static final ItemMineralTuner MINERAL_TUNER_QUARTZITE = new ItemMineralTuner("quartzite", "Quartzite");
    public static final ItemMineralTuner MINERAL_TUNER_GALENA = new ItemMineralTuner("galena", "Galena");
    public static final ItemMineralTuner MINERAL_TUNER_LEAD = new ItemMineralTuner("lead", "Lead");
    public static final ItemMineralTuner MINERAL_TUNER_SILVER = new ItemMineralTuner("silver", "Silver");
    public static final ItemMineralTuner MINERAL_TUNER_LAPIS = new ItemMineralTuner("lapis", "Lapis");
    public static final ItemMineralTuner MINERAL_TUNER_CINNABAR = new ItemMineralTuner("cinnabar", "Cinnabar");
    public static final ItemMineralTuner MINERAL_TUNER_SILT = new ItemMineralTuner("silt", "Silt");

    public static final ItemMineralTuner[] MINERAL_TUNERS = {
            MINERAL_TUNER_COAL, MINERAL_TUNER_IRON, MINERAL_TUNER_MAGNETITE, MINERAL_TUNER_PYRITE, MINERAL_TUNER_BAUXITE, MINERAL_TUNER_COPPER, MINERAL_TUNER_CASSITERITE, MINERAL_TUNER_GOLD, MINERAL_TUNER_NICKEL, MINERAL_TUNER_PLATINUM, MINERAL_TUNER_URANIUM, MINERAL_TUNER_QUARTZITE, MINERAL_TUNER_GALENA, MINERAL_TUNER_LEAD, MINERAL_TUNER_SILVER, MINERAL_TUNER_LAPIS, MINERAL_TUNER_CINNABAR, MINERAL_TUNER_SILT
    };

    
    // --- Unstable Naquadah Reactor
    public static final Item UNSTABLE_NAQUADAH_REACTOR = new Item()
    		.setRegistryName(WormholeTech.MODID, "unstable_naquadah_reactor")
    		.setUnlocalizedName(WormholeTech.MODID + ".unstable_naquadah_reactor");
    
    // -- Ancient Tablet --
    public static final Item ANCIENT_TABLET = new ItemAncientTablet();

    public static final Item GATE_LOCATOR = new com.nick1416.wormholetech.item.ItemGateLocator();

    // -- Wormhole Teleport Pad --
    public static final Block WORMHOLE_PAD = new WormholePadBlock()
            .setRegistryName(WormholeTech.MODID, "wormhole_pad")
            .setUnlocalizedName(WormholeTech.MODID + ".wormhole_pad");

    public static final Item ITEM_WORMHOLE_PAD =
            new ItemBlock(WORMHOLE_PAD).setRegistryName(WORMHOLE_PAD.getRegistryName());

    // -- Wormhole Linker --
    public static final Item WORMHOLE_LINKER = new ItemWormholeLinker();

    // -- Aetherius path --
    public static final Item AETHERIUS = new com.nick1416.wormholetech.item.ItemAetherius()
            .setRegistryName(WormholeTech.MODID, "aetherius")
            .setUnlocalizedName(WormholeTech.MODID + ".aetherius")
            .setCreativeTab(net.minecraft.creativetab.CreativeTabs.MATERIALS);

    public static final Block COMPRESSED_NAQUADAH = new CompressedNaquadahBlock()
            .setRegistryName(WormholeTech.MODID, "compressed_naquadah")
            .setUnlocalizedName(WormholeTech.MODID + ".compressed_naquadah");

    public static final Item ITEM_COMPRESSED_NAQUADAH =
            new ItemBlock(COMPRESSED_NAQUADAH).setRegistryName(COMPRESSED_NAQUADAH.getRegistryName());

    public static final Block RELATIVISTIC_COMPUTER = new RelativisticComputerBlock()
            .setRegistryName(WormholeTech.MODID, "relativistic_computer")
            .setUnlocalizedName(WormholeTech.MODID + ".relativistic_computer");

    public static final Item ITEM_RELATIVISTIC_COMPUTER =
            new ItemBlock(RELATIVISTIC_COMPUTER).setRegistryName(RELATIVISTIC_COMPUTER.getRegistryName());

    public static final Block HIGH_ENERGY_REFINER = new AetheriusRefinerBlock()
            .setRegistryName(WormholeTech.MODID, "high_energy_refiner")
            .setUnlocalizedName(WormholeTech.MODID + ".high_energy_refiner");

    public static final Item ITEM_HIGH_ENERGY_REFINER =
            new ItemBlock(HIGH_ENERGY_REFINER).setRegistryName(HIGH_ENERGY_REFINER.getRegistryName());


    // -- Stargate --
    public static final Block DIALING_COMPUTER = new com.nick1416.wormholetech.block.DialingComputerBlock()
            .setRegistryName(WormholeTech.MODID, "dialing_computer")
            .setUnlocalizedName(WormholeTech.MODID + ".dialing_computer");

    public static final Item ITEM_DIALING_COMPUTER =
            new ItemBlock(DIALING_COMPUTER).setRegistryName(DIALING_COMPUTER.getRegistryName());

    public static final Block GATE_NETWORK_COMPUTER = new com.nick1416.wormholetech.block.GateNetworkComputerBlock()
            .setRegistryName(WormholeTech.MODID, "gate_network_computer")
            .setUnlocalizedName(WormholeTech.MODID + ".gate_network_computer");
    public static final Item ITEM_GATE_NETWORK_COMPUTER =
            new ItemBlock(GATE_NETWORK_COMPUTER).setRegistryName(GATE_NETWORK_COMPUTER.getRegistryName());

    public static final Block STARGATE_PORTAL = new com.nick1416.wormholetech.block.StargatePortalBlock()
            .setRegistryName(WormholeTech.MODID, "stargate_portal")
            .setUnlocalizedName(WormholeTech.MODID + ".stargate_portal");

    public static final Block ANCIENT_GATE_FRAME = new com.nick1416.wormholetech.block.AncientGateFrameBlock()
            .setRegistryName(WormholeTech.MODID, "ancient_gate_frame")
            .setUnlocalizedName(WormholeTech.MODID + ".ancient_gate_frame");

    public static final Item ITEM_ANCIENT_GATE_FRAME =
            new ItemBlock(ANCIENT_GATE_FRAME).setRegistryName(ANCIENT_GATE_FRAME.getRegistryName());

    /** Fallback gate frame when neither Galacticraft nor any blockTitanium exists (see Fallbacks). */
    public static final Block REINFORCED_GATE_FRAME = new Block(net.minecraft.block.material.Material.IRON)
            .setHardness(5.0F).setResistance(30.0F)
            .setRegistryName(WormholeTech.MODID, "reinforced_gate_frame")
            .setUnlocalizedName(WormholeTech.MODID + ".reinforced_gate_frame");
    public static final Item ITEM_REINFORCED_GATE_FRAME =
            new ItemBlock(REINFORCED_GATE_FRAME).setRegistryName(REINFORCED_GATE_FRAME.getRegistryName());

    // -- P3X-1416 blocks --
    public static final Block NATURAL_NAQUADAH_ORE = new com.nick1416.wormholetech.block.NaturalNaquadahOreBlock()
            .setRegistryName(WormholeTech.MODID, "naquadah_ore_natural")
            .setUnlocalizedName(WormholeTech.MODID + ".naquadah_ore_natural")
            .setCreativeTab(net.minecraft.creativetab.CreativeTabs.BUILDING_BLOCKS);
    public static final Item ITEM_NATURAL_NAQUADAH_ORE =
            new ItemBlock(NATURAL_NAQUADAH_ORE).setRegistryName(NATURAL_NAQUADAH_ORE.getRegistryName());

    public static final Block PRYSMIAN_ORE = new com.nick1416.wormholetech.block.PrysmianOreBlock()
            .setRegistryName(WormholeTech.MODID, "prysmian_ore")
            .setUnlocalizedName(WormholeTech.MODID + ".prysmian_ore")
            .setCreativeTab(net.minecraft.creativetab.CreativeTabs.BUILDING_BLOCKS);
    public static final Item ITEM_PRYSMIAN_ORE =
            new ItemBlock(PRYSMIAN_ORE).setRegistryName(PRYSMIAN_ORE.getRegistryName());

    public static final Block NAQUADRIC_STONE = new com.nick1416.wormholetech.block.NaquadricStoneBlock(1.5F,
            net.minecraft.block.material.MapColor.BLUE_STAINED_HARDENED_CLAY)
            .setRegistryName(WormholeTech.MODID, "naquadric_stone")
            .setUnlocalizedName(WormholeTech.MODID + ".naquadric_stone");
    public static final Item ITEM_NAQUADRIC_STONE =
            new ItemBlock(NAQUADRIC_STONE).setRegistryName(NAQUADRIC_STONE.getRegistryName());

    public static final Block ASHEN_REGOLITH = new com.nick1416.wormholetech.block.AshenRegolithBlock()
            .setRegistryName(WormholeTech.MODID, "ashen_regolith")
            .setUnlocalizedName(WormholeTech.MODID + ".ashen_regolith");
    public static final Item ITEM_ASHEN_REGOLITH =
            new ItemBlock(ASHEN_REGOLITH).setRegistryName(ASHEN_REGOLITH.getRegistryName());

    public static final Block NAQUADAH_CRYSTAL = new com.nick1416.wormholetech.block.NaquadahCrystalBlock()
            .setRegistryName(WormholeTech.MODID, "naquadah_crystal")
            .setUnlocalizedName(WormholeTech.MODID + ".naquadah_crystal");
    public static final Item ITEM_NAQUADAH_CRYSTAL =
            new ItemBlock(NAQUADAH_CRYSTAL).setRegistryName(NAQUADAH_CRYSTAL.getRegistryName());

    public static final Block ANCIENT_STONE = new com.nick1416.wormholetech.block.NaquadricStoneBlock(2.0F,
            net.minecraft.block.material.MapColor.CYAN)
            .setRegistryName(WormholeTech.MODID, "ancient_stone")
            .setUnlocalizedName(WormholeTech.MODID + ".ancient_stone");
    public static final Item ITEM_ANCIENT_STONE =
            new ItemBlock(ANCIENT_STONE).setRegistryName(ANCIENT_STONE.getRegistryName());

    public static final net.minecraft.world.biome.Biome NAQUADAH_WASTES =
            new com.nick1416.wormholetech.world.BiomeNaquadahWastes()
                    .setRegistryName(WormholeTech.MODID, "naquadah_wastes");

    @SubscribeEvent
    public static void onRegisterBiomes(RegistryEvent.Register<net.minecraft.world.biome.Biome> e) {
        e.getRegistry().register(NAQUADAH_WASTES);
        e.getRegistry().register(DERELICT_BIOME);
        e.getRegistry().register(TAL_VORA_BIOME);
        e.getRegistry().register(SEL_VORA_BIOME);
        e.getRegistry().register(ELYNDRA_BIOME);
        net.minecraftforge.common.BiomeDictionary.addTypes(ELYNDRA_BIOME, net.minecraftforge.common.BiomeDictionary.Type.FOREST,
                net.minecraftforge.common.BiomeDictionary.Type.MAGICAL);
        net.minecraftforge.common.BiomeDictionary.addTypes(SEL_VORA_BIOME, net.minecraftforge.common.BiomeDictionary.Type.COLD,
                net.minecraftforge.common.BiomeDictionary.Type.DEAD, net.minecraftforge.common.BiomeDictionary.Type.WASTELAND);
        net.minecraftforge.common.BiomeDictionary.addTypes(TAL_VORA_BIOME, net.minecraftforge.common.BiomeDictionary.Type.HOT,
                net.minecraftforge.common.BiomeDictionary.Type.DRY, net.minecraftforge.common.BiomeDictionary.Type.SANDY);
    }

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<net.minecraftforge.fml.common.registry.EntityEntry> e) {
        e.getRegistry().register(net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
                .entity(com.nick1416.wormholetech.entity.EntityEmeraldDrone.class)
                .id(new net.minecraft.util.ResourceLocation(WormholeTech.MODID, "emerald_drone"), 0)
                .name(WormholeTech.MODID + ".emerald_drone")
                .tracker(64, 3, true)
                .egg(0x2E3A36, 0x3FD27A)
                .build());
        e.getRegistry().register(net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
                .entity(com.nick1416.wormholetech.entity.EntityEmeraldSentinel.class)
                .id(new net.minecraft.util.ResourceLocation(WormholeTech.MODID, "emerald_sentinel"), 1)
                .name(WormholeTech.MODID + ".emerald_sentinel")
                .tracker(80, 3, true)
                .egg(0x1A1D21, 0x3FD27A)
                .build());
        e.getRegistry().register(net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
                .entity(com.nick1416.wormholetech.entity.EntityEmeraldWisp.class)
                .id(new net.minecraft.util.ResourceLocation(WormholeTech.MODID, "emerald_wisp"), 2)
                .name(WormholeTech.MODID + ".emerald_wisp")
                .tracker(64, 3, true)
                .egg(0x22302A, 0xE03030)
                .build());
        e.getRegistry().register(net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
                .entity(com.nick1416.wormholetech.entity.EntityGiantEmeraldSentinel.class)
                .id(new net.minecraft.util.ResourceLocation(WormholeTech.MODID, "giant_emerald_sentinel"), 3)
                .name(WormholeTech.MODID + ".giant_emerald_sentinel")
                .tracker(80, 3, true)
                .egg(0x1A1D21, 0xE0A030)
                .build());
        e.getRegistry().register(net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
                .entity(com.nick1416.wormholetech.talvora.EntityAlienVillager.class)
                .id(new net.minecraft.util.ResourceLocation(WormholeTech.MODID, "alien_villager"), 4)
                .name(WormholeTech.MODID + ".alien_villager")
                .tracker(80, 3, true)
                .egg(0x2A6E78, 0xC9A0FF)
                .build());
        e.getRegistry().register(net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
                .entity(com.nick1416.wormholetech.talvora.EntityXenolithDrone.class)
                .id(new net.minecraft.util.ResourceLocation(WormholeTech.MODID, "xenolith_drone"), 5)
                .name(WormholeTech.MODID + ".xenolith_drone")
                .tracker(80, 2, true)
                .egg(0x1F4A55, 0x46C8BE)
                .build());
        e.getRegistry().register(net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
                .entity(com.nick1416.wormholetech.drone.EntityCompanionDrone.class)
                .id(new net.minecraft.util.ResourceLocation(WormholeTech.MODID, "companion_drone"), 6)
                .name(WormholeTech.MODID + ".companion_drone")
                .tracker(80, 2, true)
                .build());
        e.getRegistry().register(net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
                .entity(com.nick1416.wormholetech.moon.EntityRegolithCrawler.class)
                .id(new net.minecraft.util.ResourceLocation(WormholeTech.MODID, "regolith_crawler"), 7)
                .name(WormholeTech.MODID + ".regolith_crawler")
                .tracker(64, 3, true)
                .egg(0xB8B6B0, 0x46C8BE)
                .build());
        e.getRegistry().register(net.minecraftforge.fml.common.registry.EntityEntryBuilder.create()
                .entity(com.nick1416.wormholetech.elyndra.EntityAncientSpirit.class)
                .id(new net.minecraft.util.ResourceLocation(WormholeTech.MODID, "ancient_spirit"), 8)
                .name(WormholeTech.MODID + ".ancient_spirit")
                .tracker(64, 3, false)
                .build());
    }

    /**
     * Vanilla creative search only lists items that sit in a creative tab. Runs after the mode config
     * is read (preInit), so the duplicators only get a tab in cc mode.
     */
    private static void assignCreativeTabs() {
        net.minecraft.creativetab.CreativeTabs b = net.minecraft.creativetab.CreativeTabs.BUILDING_BLOCKS,
                r = net.minecraft.creativetab.CreativeTabs.REDSTONE, m = net.minecraft.creativetab.CreativeTabs.MATERIALS;
        RF_GENERATOR.setCreativeTab(r);
        NAQUADAH_GENERATOR.setCreativeTab(r);
        if (com.nick1416.wormholetech.ModConfig.isCC()) {
            WORMHOLE_DUPLICATOR.setCreativeTab(r);
            UNSTABLE_WORMHOLE_DUPLICATOR.setCreativeTab(r);
            for (Item t : MINERAL_TUNERS) t.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);
        }
        NAQUADAH_ORE.setCreativeTab(b);
        UNSTABLE_NAQUADAH_BLOCK.setCreativeTab(b);
        PRYSMIAN_BLOCK.setCreativeTab(b);
        UNSTABLE_PRYSMIAN_BLOCK.setCreativeTab(b);
        ANCIENT_GATE_FRAME.setCreativeTab(b);
        TITANIUM_BLOCK.setCreativeTab(b);
        MOON_REGOLITH.setCreativeTab(b);
        TITANIUM_ORE.setCreativeTab(b);
        NEUTRONIUM_ORE.setCreativeTab(b);
        NEUTRONIUM_INGOT.setCreativeTab(m);
        NEUTRONIUM_SUPERCONDUCTOR_BLOCK.setCreativeTab(b);
        MINIATURE_FACTORY.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);
        for (Block blk : new Block[]{HULL_PLATING, HULL_BULKHEAD, HULL_LAMP, HULL_LAMP_GOLD, HULL_WINDOW, MODULATOR_CRADLE, XENOLITH, XENOLITH_TILES, XENO_LAMP}) blk.setCreativeTab(b);
        QUANTUM_FIELD_MODULATOR.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);
        DERELICT_COORDINATES.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);
        INTERGALACTIC_COORDINATES.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);
        NANITE_CORE.setCreativeTab(m);
        com.nick1416.wormholetech.item.HardDiamond.assignTabs();
        XENOLITH_ORE.setCreativeTab(b);
        for (Item i : new Item[]{RAW_XENOLITH, INACTIVE_DRONE, XENOLITH_CHIP}) i.setCreativeTab(m);
        MINER_DRONE.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);
        REPOSITORY.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);
        COMPANION_DRONE.setCreativeTab(net.minecraft.creativetab.CreativeTabs.MISC);
        TITANIUM_INGOT.setCreativeTab(m);
        for (Item i : new Item[]{RAW_NAQUADAH, REFINED_NAQUADAH, PRYSMIAN, UNSTABLE_NAQUADAH,
                NAQUADAH_REACTOR, UNSTABLE_NAQUADAH_REACTOR}) i.setCreativeTab(m);
    }

    // --- Registry events ---
    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().registerAll(
                RF_GENERATOR,
                NAQUADAH_ORE,
                PRYSMIAN_BLOCK,
                UNSTABLE_PRYSMIAN_BLOCK,
                PRYSMIAN_LOGIC_FRAME,
                UNSTABLE_NAQUADAH_BLOCK,
                REFINED_NAQUADAH_BLOCK,
                WORMHOLE_DUPLICATOR,
                UNSTABLE_WORMHOLE_DUPLICATOR,
                NAQUADAH_GENERATOR,
                WORMHOLE_PAD,
                COMPRESSED_NAQUADAH,
                RELATIVISTIC_COMPUTER,
                HIGH_ENERGY_REFINER,
                DIALING_COMPUTER,
                GATE_NETWORK_COMPUTER,
                STARGATE_PORTAL,
                ANCIENT_GATE_FRAME,
                REINFORCED_GATE_FRAME,
                HULL_PLATING, HULL_BULKHEAD, HULL_LAMP, HULL_LAMP_GOLD, HULL_WINDOW, MODULATOR_CRADLE, QUANTUM_FIELD_MODULATOR, VORANI_MODULATOR, XENOLITH, XENOLITH_TILES, XENO_LAMP, XENOLITH_ORE, MINER_DRONE, REPOSITORY,
                TITANIUM_BLOCK,
                MOON_REGOLITH, TITANIUM_ORE, RESEARCH_CONSOLE, NEUTRONIUM_ORE, NEUTRONIUM_REACTOR, NEUTRONIUM_SUPERCONDUCTOR_BLOCK, MINIATURE_FACTORY,
                NATURAL_NAQUADAH_ORE,
                PRYSMIAN_ORE,
                NAQUADRIC_STONE,
                ASHEN_REGOLITH,
                NAQUADAH_CRYSTAL,
                ANCIENT_STONE
        );
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> e) {
        assignCreativeTabs();
        e.getRegistry().registerAll(
                ITEM_RF_GENERATOR,
                ITEM_NAQUADAH_ORE,
                RAW_NAQUADAH,
                REFINED_NAQUADAH,
                PRYSMIAN,
                DIAMOND_NUGGET,
                ITEM_PRYSMIAN_BLOCK,
                QUANTUM_CIRCUIT,
                ITEM_PRYSMIAN_LOGIC_FRAME,
                NAQUADAH_REACTOR,
                UNSTABLE_NAQUADAH_REACTOR,
                UNSTABLE_NAQUADAH,
                ITEM_UNSTABLE_PRYSMIAN_BLOCK,
                ITEM_UNSTABLE_NAQUADAH_BLOCK,
                ITEM_REFINED_NAQUADAH_BLOCK,
                ITEM_WORMHOLE_DUPLICATOR,
                ITEM_UNSTABLE_WORMHOLE_DUPLICATOR,
                MINERAL_TUNER_COAL,
                MINERAL_TUNER_IRON,
                MINERAL_TUNER_MAGNETITE,
                MINERAL_TUNER_PYRITE,
                MINERAL_TUNER_BAUXITE,
                MINERAL_TUNER_COPPER,
                MINERAL_TUNER_CASSITERITE,
                MINERAL_TUNER_GOLD,
                MINERAL_TUNER_NICKEL,
                MINERAL_TUNER_PLATINUM,
                MINERAL_TUNER_URANIUM,
                MINERAL_TUNER_QUARTZITE,
                MINERAL_TUNER_GALENA,
                MINERAL_TUNER_LEAD,
                MINERAL_TUNER_SILVER,
                MINERAL_TUNER_LAPIS,
                MINERAL_TUNER_CINNABAR,
                MINERAL_TUNER_SILT,
                ANCIENT_TABLET,
                ITEM_NAQUADAH_GENERATOR,
                ITEM_WORMHOLE_PAD,
                WORMHOLE_LINKER,
                AETHERIUS,
                com.nick1416.wormholetech.item.HardDiamond.GEM, com.nick1416.wormholetech.item.HardDiamond.PICKAXE, com.nick1416.wormholetech.item.HardDiamond.SWORD, com.nick1416.wormholetech.item.HardDiamond.AXE, com.nick1416.wormholetech.item.HardDiamond.SHOVEL,
                ITEM_COMPRESSED_NAQUADAH,
                ITEM_RELATIVISTIC_COMPUTER,
                ITEM_HIGH_ENERGY_REFINER,
                ITEM_DIALING_COMPUTER,
                ITEM_GATE_NETWORK_COMPUTER,
                ITEM_ANCIENT_GATE_FRAME,
                ITEM_REINFORCED_GATE_FRAME,
                ITEM_HULL_PLATING, ITEM_HULL_BULKHEAD, ITEM_HULL_LAMP, ITEM_HULL_LAMP_GOLD, ITEM_HULL_WINDOW, ITEM_MODULATOR_CRADLE, ITEM_QUANTUM_FIELD_MODULATOR, ITEM_XENOLITH, ITEM_XENOLITH_TILES, ITEM_XENO_LAMP,
                ITEM_XENOLITH_ORE, RAW_XENOLITH, INACTIVE_DRONE, XENOLITH_CHIP, ITEM_MINER_DRONE, COMPANION_DRONE, ITEM_REPOSITORY,
                DERELICT_COORDINATES,
                INTERGALACTIC_COORDINATES,
                NANITE_CORE,
                ITEM_TITANIUM_BLOCK,
                ITEM_MOON_REGOLITH, ITEM_TITANIUM_ORE, ITEM_NEUTRONIUM_ORE, NEUTRONIUM_INGOT, NEUTRONIUM_SUPERCONDUCTOR, ITEM_MINIATURE_FACTORY,
                TITANIUM_INGOT,
                GATE_LOCATOR,
                ITEM_NATURAL_NAQUADAH_ORE,
                ITEM_PRYSMIAN_ORE,
                ITEM_NAQUADRIC_STONE,
                ITEM_ASHEN_REGOLITH,
                ITEM_NAQUADAH_CRYSTAL,
                ITEM_ANCIENT_STONE
        );
        net.minecraftforge.oredict.OreDictionary.registerOre("ingotTitanium", TITANIUM_INGOT);
        net.minecraftforge.oredict.OreDictionary.registerOre("oreTitanium", TITANIUM_ORE);
        net.minecraftforge.oredict.OreDictionary.registerOre("oreNeutronium", NEUTRONIUM_ORE);
        net.minecraftforge.oredict.OreDictionary.registerOre("ingotNeutronium", NEUTRONIUM_INGOT);
        net.minecraftforge.oredict.OreDictionary.registerOre("oreXenolith", XENOLITH_ORE);
        net.minecraftforge.oredict.OreDictionary.registerOre("gemXenolith", RAW_XENOLITH);
        net.minecraftforge.oredict.OreDictionary.registerOre("blockTitanium", TITANIUM_BLOCK);
    }
}
