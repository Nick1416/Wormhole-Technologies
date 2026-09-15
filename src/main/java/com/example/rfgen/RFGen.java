package com.example.rfgen;

import com.example.rfgen.common.CommonProxy;
import com.example.rfgen.registry.Registration;
import com.example.rfgen.tile.GeneratorTileEntity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

@Mod(modid = RFGen.MODID, name = "Wormhole Technologies", version = RFGen.VERSION, dependencies = "after:jei;")
public class RFGen {
    public static final String MODID = "rfgen";
    public static final String VERSION = "1.0.0";

    @SidedProxy(clientSide = "com.example.rfgen.client.ClientProxy", serverSide = "com.example.rfgen.common.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.Instance
    public static RFGen INSTANCE;

    // Expose this if you want to use the nugget elsewhere in your mod
    public static ItemStack DIAMOND_NUGGET = ItemStack.EMPTY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        GameRegistry.registerTileEntity(GeneratorTileEntity.class,
                new ResourceLocation(MODID, "rf_generator"));

        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new com.example.rfgen.gui.GuiHandler());

        GameRegistry.registerTileEntity(
                com.example.rfgen.tile.WormholeDuplicatorTileEntity.class,
                new ResourceLocation(MODID, "wormhole_duplicator"));

        GameRegistry.registerTileEntity(
                com.example.rfgen.tile.UnstableWormholeDuplicatorTileEntity.class,
                new ResourceLocation(MODID, "unstable_wormhole_duplicator"));

        GameRegistry.registerTileEntity(
                com.example.rfgen.tile.UnstableGeneratorTileEntity.class,
                new ResourceLocation(MODID, "unstable_wormhole_energy_converter"));

        GameRegistry.registerTileEntity(
                com.example.rfgen.tile.NaquadahGeneratorTileEntity.class,
                new ResourceLocation(MODID, "naquadah_generator"));

        PROXY.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        // Prefer the specific TF item id if TF is present
        if (Loader.isModLoaded("thermalfoundation")) {
            Item tfItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation("thermalfoundation", "diamond_nugget"));
            if (tfItem != null) {
                DIAMOND_NUGGET = new ItemStack(tfItem);
            }
        }

        // Fallback: any mod that provides a diamond nugget via OreDictionary
        if (DIAMOND_NUGGET.isEmpty()) {
            List<ItemStack> matches = OreDictionary.getOres("nuggetDiamond");
            if (!matches.isEmpty()) {
                DIAMOND_NUGGET = matches.get(0).copy();
            }
        }


        PROXY.init();
    }

    public static final int GUI_WORMHOLE_DUPLICATOR = 1;
}
