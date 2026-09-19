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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

@Mod(
        modid = WormholeTech.MODID,
        name = "Wormhole Technologies",
        version = WormholeTech.VERSION,
        dependencies = "after:immersiveengineering;after:jei;"
)
public class WormholeTech {
    public static final String MODID = "wormholetech";
    public static final String VERSION = "0.1.0";

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
                com.nick1416.wormholetech.tile.UnstableGeneratorTileEntity.class,
                new ResourceLocation(MODID, "unstable_wormhole_energy_converter"));

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

        PROXY.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
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

        PROXY.init();
    }

    public static final int GUI_WORMHOLE_DUPLICATOR = 1;
    public static final int GUI_RELATIVISTIC_COMPUTER = 2;
    public static final int GUI_HIGH_ENERGY_REFINER = 3;
}
