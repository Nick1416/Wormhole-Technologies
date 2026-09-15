package com.example.rfgen.registry;

import com.example.rfgen.RFGen;
import com.example.rfgen.block.GeneratorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RFGen.MODID)
public class Registration {

    // --- Existing generator ---
    public static final Block RF_GENERATOR = new GeneratorBlock()
            .setRegistryName(RFGen.MODID, "rf_generator")
            .setUnlocalizedName(RFGen.MODID + ".rf_generator")
            .setCreativeTab(CreativeTabs.REDSTONE);

    public static final Item ITEM_RF_GENERATOR =
            new ItemBlock(RF_GENERATOR).setRegistryName(RF_GENERATOR.getRegistryName());

    // --- NEW: Naquadah Ore ---
    public static final Block NAQUADAH_ORE = new Block(Material.ROCK)
            .setRegistryName(RFGen.MODID, "naquadah_ore")
            .setUnlocalizedName(RFGen.MODID + ".naquadah_ore")
            .setHardness(3.0F)
            .setResistance(5.0F)
            .setCreativeTab(CreativeTabs.BUILDING_BLOCKS);

    public static final Item ITEM_NAQUADAH_ORE =
            new ItemBlock(NAQUADAH_ORE).setRegistryName(NAQUADAH_ORE.getRegistryName());

    // --- Registry events ---
    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> e) {
        e.getRegistry().registerAll(
                RF_GENERATOR,
                NAQUADAH_ORE
        );
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().registerAll(
                ITEM_RF_GENERATOR,
                ITEM_NAQUADAH_ORE
        );
    }
}
