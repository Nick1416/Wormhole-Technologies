package com.example.rfgen.registry;

import com.example.rfgen.block.NaquadahOreBlock;
import com.example.rfgen.block.PrysmianBlock;
import com.example.rfgen.block.UnstablePrysmianBlock;
import com.example.rfgen.block.UnstableWormholeEnergyConverter;
import com.example.rfgen.block.UnstableNaquadahBlock;
import com.example.rfgen.block.WormholeDuplicatorBlock;
import com.example.rfgen.block.UnstableWormholeDuplicatorBlock;
import com.example.rfgen.item.ItemMineralTuner;

import net.minecraft.item.Item;
import com.example.rfgen.RFGen;
import com.example.rfgen.block.GeneratorBlock;
import com.example.rfgen.block.NaquadahGenerator;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RFGen.MODID)
public class Registration {

	// -- Block of Prysmian --
	public static final Block PRYSMIAN_BLOCK = new PrysmianBlock()
			.setRegistryName(RFGen.MODID, "prysmian_block")
			.setUnlocalizedName(RFGen.MODID + ".prysmian_block");
	
	public static final Item ITEM_PRYSMIAN_BLOCK = 
			new ItemBlock(PRYSMIAN_BLOCK).setRegistryName(PRYSMIAN_BLOCK.getRegistryName());
	
	// -- Unstable Block of Prysmian --
	public static final Block UNSTABLE_PRYSMIAN_BLOCK = new UnstablePrysmianBlock()
			.setRegistryName(RFGen.MODID, "unstable_prysmian_block")
			.setUnlocalizedName(RFGen.MODID + ".unstable_prysmian_block");
	
	public static final Item ITEM_UNSTABLE_PRYSMIAN_BLOCK = 
			new ItemBlock(UNSTABLE_PRYSMIAN_BLOCK).setRegistryName(UNSTABLE_PRYSMIAN_BLOCK.getRegistryName());
			
    // --- Wormhole Energy Converter ---
    public static final Block RF_GENERATOR = new GeneratorBlock()
            .setRegistryName(RFGen.MODID, "rf_generator")
            .setUnlocalizedName(RFGen.MODID + ".rf_generator");

    public static final Item ITEM_RF_GENERATOR =
            new ItemBlock(RF_GENERATOR).setRegistryName(RF_GENERATOR.getRegistryName());
    
    // -- Wormhole Duplicator -- 
    public static final Block WORMHOLE_DUPLICATOR = new WormholeDuplicatorBlock()
    	    .setRegistryName(RFGen.MODID, "wormhole_duplicator")
    	    .setUnlocalizedName(RFGen.MODID + ".wormhole_duplicator");
    
    // -- Unstable Wormhole Duplicator -- 
    public static final Block UNSTABLE_WORMHOLE_DUPLICATOR = new UnstableWormholeDuplicatorBlock()
    	    .setRegistryName(RFGen.MODID, "unstable_wormhole_duplicator")
    	    .setUnlocalizedName(RFGen.MODID + ".unstable_wormhole_duplicator");
    
    public static final Item ITEM_UNSTABLE_WORMHOLE_DUPLICATOR =
    	    new ItemBlock(UNSTABLE_WORMHOLE_DUPLICATOR).setRegistryName(UNSTABLE_WORMHOLE_DUPLICATOR.getRegistryName());

	public static final Item ITEM_WORMHOLE_DUPLICATOR =
	    new ItemBlock(WORMHOLE_DUPLICATOR).setRegistryName(WORMHOLE_DUPLICATOR.getRegistryName());
    
    // -- Unstable Wormhole Energy Converter --
    public static final Block UNSTABLE_WORMHOLE_ENERGY_CONVERTER = new UnstableWormholeEnergyConverter()
            .setRegistryName(RFGen.MODID, "unstable_wormhole_energy_converter")
            .setUnlocalizedName(RFGen.MODID + ".unstable_wormhole_energy_converter");

    public static final Item ITEM_UNSTABLE_WORMHOLE_ENERGY_CONVERTER =
            new ItemBlock(UNSTABLE_WORMHOLE_ENERGY_CONVERTER).setRegistryName(UNSTABLE_WORMHOLE_ENERGY_CONVERTER.getRegistryName());
    
    // -- Naquadah Generator --
    public static final Block NAQUADAH_GENERATOR = new NaquadahGenerator()
    		.setRegistryName(RFGen.MODID, "naquadah_generator")
    		.setUnlocalizedName(RFGen.MODID + ".naquadah_generator");
    
    public static final Item ITEM_NAQUADAH_GENERATOR=
    		new ItemBlock(NAQUADAH_GENERATOR).setRegistryName(NAQUADAH_GENERATOR.getRegistryName());

    // --- Naquadah Ore ---
    public static final Block NAQUADAH_ORE = new NaquadahOreBlock()
            .setRegistryName(RFGen.MODID, "naquadah_ore")
            .setUnlocalizedName(RFGen.MODID + ".naquadah_ore");

    public static final Item ITEM_NAQUADAH_ORE =
            new ItemBlock(NAQUADAH_ORE).setRegistryName(NAQUADAH_ORE.getRegistryName());
    
    // -- Unstable Block of Naquadah --
    public static final Block UNSTABLE_NAQUADAH_BLOCK = new UnstableNaquadahBlock()
            .setRegistryName(RFGen.MODID, "unstable_naquadah_block")
            .setUnlocalizedName(RFGen.MODID + ".unstable_naquadah_block");

    public static final Item ITEM_UNSTABLE_NAQUADAH_BLOCK =
            new ItemBlock(UNSTABLE_NAQUADAH_BLOCK).setRegistryName(UNSTABLE_NAQUADAH_BLOCK.getRegistryName());
    
    // --- Raw Naquadah ---
    public static final Item RAW_NAQUADAH = new Item()
    		.setRegistryName(RFGen.MODID, "raw_naquadah")
    		.setUnlocalizedName(RFGen.MODID + ".raw_naquadah");

    // --- Refined Naquadah ---
    public static final Item REFINED_NAQUADAH = new Item()
    		.setRegistryName(RFGen.MODID, "refined_naquadah")
    		.setUnlocalizedName(RFGen.MODID + ".refined_naquadah");
    
    // --- Prysmian ---
    public static final Item PRYSMIAN = new Item()
    		.setRegistryName(RFGen.MODID, "prysmian")
    		.setUnlocalizedName(RFGen.MODID + ".prysmian");
    
    // --- Unstable Naquadah ---
    public static final Item UNSTABLE_NAQUADAH = new Item()
    		.setRegistryName(RFGen.MODID, "unstable_naquadah")
    		.setUnlocalizedName(RFGen.MODID + ".unstable_naquadah");
    
    // --- Quantum Glue ---
    public static final Item QUANTUM_GLUE = new Item()
    		.setRegistryName(RFGen.MODID, "quantum_glue")
    		.setUnlocalizedName(RFGen.MODID + ".quantum_glue");
    
    // --- Naquadah Reactor
    public static final Item NAQUADAH_REACTOR = new Item()
    		.setRegistryName(RFGen.MODID, "naquadah_reactor")
    		.setUnlocalizedName(RFGen.MODID + ".naquadah_reactor");
    
 // -- Mineral Tuner (coal) -- 
 	public static final Item MINERAL_TUNER_COAL = new ItemMineralTuner();
    
    // --- Unstable Naquadah Reactor
    public static final Item UNSTABLE_NAQUADAH_REACTOR = new Item()
    		.setRegistryName(RFGen.MODID, "unstable_naquadah_reactor")
    		.setUnlocalizedName(RFGen.MODID + ".unstable_naquadah_reactor");
    
    // -- Synthetic Mineral --
    public static final Item SYNTHETIC_MINERAL = new Item()
    		.setRegistryName(RFGen.MODID, "synthetic_mineral")
    		.setUnlocalizedName(RFGen.MODID + ".synthetic_mineral");
    
    // --- Registry events ---
    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().registerAll(
                RF_GENERATOR,
                NAQUADAH_ORE,
                PRYSMIAN_BLOCK,
                UNSTABLE_PRYSMIAN_BLOCK,
                UNSTABLE_WORMHOLE_ENERGY_CONVERTER,
                UNSTABLE_NAQUADAH_BLOCK,
                WORMHOLE_DUPLICATOR,
                UNSTABLE_WORMHOLE_DUPLICATOR,
                NAQUADAH_GENERATOR
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
                QUANTUM_GLUE,
                NAQUADAH_REACTOR,
                UNSTABLE_NAQUADAH_REACTOR,
                UNSTABLE_NAQUADAH,
                ITEM_UNSTABLE_PRYSMIAN_BLOCK,
                ITEM_UNSTABLE_WORMHOLE_ENERGY_CONVERTER,
                SYNTHETIC_MINERAL,
                ITEM_UNSTABLE_NAQUADAH_BLOCK,
                ITEM_WORMHOLE_DUPLICATOR,
                ITEM_UNSTABLE_WORMHOLE_DUPLICATOR,
                MINERAL_TUNER_COAL,
                ITEM_NAQUADAH_GENERATOR
        );
    }
}
