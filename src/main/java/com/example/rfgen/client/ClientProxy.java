package com.example.rfgen.client;

import com.example.rfgen.RFGen;
import com.example.rfgen.registry.Registration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RFGen.MODID) // ensure it subscribes for your mod
public class ClientProxy extends com.example.rfgen.common.CommonProxy {

	@SubscribeEvent
	public static void onModels(ModelRegistryEvent e) {

	    Item genItem = Item.getItemFromBlock(Registration.RF_GENERATOR);
	    ModelLoader.setCustomModelResourceLocation(
	        genItem, 0, new ModelResourceLocation(genItem.getRegistryName(), "inventory"));
	    
	    Item unstableGenItem = Item.getItemFromBlock(Registration.UNSTABLE_WORMHOLE_ENERGY_CONVERTER);
	    ModelLoader.setCustomModelResourceLocation(
	    		unstableGenItem, 0, new ModelResourceLocation(unstableGenItem.getRegistryName(), "inventory"));
	    
	    Item naquadahGen = Item.getItemFromBlock(Registration.NAQUADAH_GENERATOR);
	    ModelLoader.setCustomModelResourceLocation(
	    		naquadahGen, 0, new ModelResourceLocation(naquadahGen.getRegistryName(), "inventory"));
	    
	    Item dupItem = Item.getItemFromBlock(Registration.WORMHOLE_DUPLICATOR);
	    ModelLoader.setCustomModelResourceLocation(dupItem, 0,
	        new ModelResourceLocation(dupItem.getRegistryName(), "inventory"));


	    Item naqOreItem = Item.getItemFromBlock(Registration.NAQUADAH_ORE);
	    ModelLoader.setCustomModelResourceLocation(
	        naqOreItem, 0, new ModelResourceLocation(naqOreItem.getRegistryName(), "inventory"));
	    
	    Item naqBlockUnstable = Item.getItemFromBlock(Registration.UNSTABLE_NAQUADAH_BLOCK);
	    ModelLoader.setCustomModelResourceLocation(
	    		naqBlockUnstable, 0, new ModelResourceLocation(naqBlockUnstable.getRegistryName(), "inventory"));
	    
	    Item prysBlockItem = Item.getItemFromBlock(Registration.PRYSMIAN_BLOCK);
	    ModelLoader.setCustomModelResourceLocation(
	    		prysBlockItem, 0, new ModelResourceLocation(prysBlockItem.getRegistryName(), "inventory"));
	    
	    Item prysBlockItemUnstable = Item.getItemFromBlock(Registration.UNSTABLE_PRYSMIAN_BLOCK);
	    ModelLoader.setCustomModelResourceLocation(
	    		prysBlockItemUnstable, 0, new ModelResourceLocation(prysBlockItemUnstable.getRegistryName(), "inventory"));

	    Item raw = Registration.RAW_NAQUADAH;
	    ModelLoader.setCustomModelResourceLocation(
	        raw, 0, new ModelResourceLocation(raw.getRegistryName(), "inventory"));

	    // refined_naquadah
	    Item refined = Registration.REFINED_NAQUADAH;
	    ModelLoader.setCustomModelResourceLocation(
	        refined, 0, new ModelResourceLocation(refined.getRegistryName(), "inventory"));
	    
	    // quantum glue
	    Item glue = Registration.QUANTUM_GLUE;
	    ModelLoader.setCustomModelResourceLocation(
	    		glue, 0, new ModelResourceLocation(glue.getRegistryName(), "inventory"));
	    
	    // unstable naquadah
	    Item unstableNaquada = Registration.UNSTABLE_NAQUADAH;
	    ModelLoader.setCustomModelResourceLocation(
	    		unstableNaquada, 0, new ModelResourceLocation(unstableNaquada.getRegistryName(), "inventory"));
	    
	    // naquadah reactor
	    Item reactorNaquadah = Registration.NAQUADAH_REACTOR;
	    ModelLoader.setCustomModelResourceLocation(
	    		reactorNaquadah, 0, new ModelResourceLocation(reactorNaquadah.getRegistryName(), "inventory"));
	    
	    // mineral tuners
	    for (Item tuner : Registration.MINERAL_TUNERS) {
	        ModelLoader.setCustomModelResourceLocation(
	                tuner, 0, new ModelResourceLocation(tuner.getRegistryName(), "inventory"));
	    }
	    
	    // unstable naquadah reactor
	    Item reactorNaquadahUnstable = Registration.UNSTABLE_NAQUADAH_REACTOR;
	    ModelLoader.setCustomModelResourceLocation(
	    		reactorNaquadahUnstable, 0, new ModelResourceLocation(reactorNaquadahUnstable.getRegistryName(), "inventory"));
	    
	    // synthetic mineral
	    Item syntheticMineral = Registration.SYNTHETIC_MINERAL;
	    ModelLoader.setCustomModelResourceLocation(
	    		syntheticMineral, 0, new ModelResourceLocation(syntheticMineral.getRegistryName(), "inventory"));
	   
	    // unstable wormhole duplicator
	    Item udup = Item.getItemFromBlock(Registration.UNSTABLE_WORMHOLE_DUPLICATOR);
	    ModelLoader.setCustomModelResourceLocation(udup, 0,
	        new ModelResourceLocation(udup.getRegistryName(), "inventory"));

	    
	    // prysmian
	    Item prys = Registration.PRYSMIAN;
	    ModelLoader.setCustomModelResourceLocation(
	    		prys, 0, new ModelResourceLocation(prys.getRegistryName(), "inventory"));
	}

}
