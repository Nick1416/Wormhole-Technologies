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

    public static final Block STARGATE_PORTAL = new com.nick1416.wormholetech.block.StargatePortalBlock()
            .setRegistryName(WormholeTech.MODID, "stargate_portal")
            .setUnlocalizedName(WormholeTech.MODID + ".stargate_portal");

    public static final Block ANCIENT_GATE_FRAME = new com.nick1416.wormholetech.block.AncientGateFrameBlock()
            .setRegistryName(WormholeTech.MODID, "ancient_gate_frame")
            .setUnlocalizedName(WormholeTech.MODID + ".ancient_gate_frame");

    public static final Item ITEM_ANCIENT_GATE_FRAME =
            new ItemBlock(ANCIENT_GATE_FRAME).setRegistryName(ANCIENT_GATE_FRAME.getRegistryName());

    // -- P3X-1416 blocks --
    public static final Block NATURAL_NAQUADAH_ORE = new com.nick1416.wormholetech.block.NaturalNaquadahOreBlock()
            .setRegistryName(WormholeTech.MODID, "naquadah_ore_natural")
            .setUnlocalizedName(WormholeTech.MODID + ".naquadah_ore_natural")
            .setCreativeTab(net.minecraft.creativetab.CreativeTabs.BUILDING_BLOCKS);
    public static final Item ITEM_NATURAL_NAQUADAH_ORE =
            new ItemBlock(NATURAL_NAQUADAH_ORE).setRegistryName(NATURAL_NAQUADAH_ORE.getRegistryName());

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
                STARGATE_PORTAL,
                ANCIENT_GATE_FRAME,
                NATURAL_NAQUADAH_ORE,
                NAQUADRIC_STONE,
                ASHEN_REGOLITH,
                NAQUADAH_CRYSTAL,
                ANCIENT_STONE
        );
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().registerAll(
                ITEM_RF_GENERATOR,
                ITEM_NAQUADAH_ORE,
                RAW_NAQUADAH,
                REFINED_NAQUADAH,
                PRYSMIAN,
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
                ITEM_COMPRESSED_NAQUADAH,
                ITEM_RELATIVISTIC_COMPUTER,
                ITEM_HIGH_ENERGY_REFINER,
                ITEM_DIALING_COMPUTER,
                ITEM_ANCIENT_GATE_FRAME,
                GATE_LOCATOR,
                ITEM_NATURAL_NAQUADAH_ORE,
                ITEM_NAQUADRIC_STONE,
                ITEM_ASHEN_REGOLITH,
                ITEM_NAQUADAH_CRYSTAL,
                ITEM_ANCIENT_STONE
        );
    }
}
