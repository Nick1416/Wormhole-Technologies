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
        // RF Generator (block item)
        Item genItem = Item.getItemFromBlock(Registration.RF_GENERATOR);
        ModelLoader.setCustomModelResourceLocation(
            genItem, 0, new ModelResourceLocation(genItem.getRegistryName(), "inventory")
        );

        // Naquadah Ore (block item)  <-- this is the important one for your magenta icon
        Item naqItem = Item.getItemFromBlock(Registration.NAQUADAH_ORE);
        ModelLoader.setCustomModelResourceLocation(
            naqItem, 0, new ModelResourceLocation(naqItem.getRegistryName(), "inventory")
        );
    }
}
