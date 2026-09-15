package com.example.rfgen;

import com.example.rfgen.common.CommonProxy;
import com.example.rfgen.registry.Registration;
import com.example.rfgen.tile.GeneratorTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = RFGen.MODID, name = "RF Generator", version = RFGen.VERSION)
public class RFGen {
    public static final String MODID = "rfgen";
    public static final String VERSION = "1.0.0";

    @SidedProxy(clientSide = "com.example.rfgen.client.ClientProxy", serverSide = "com.example.rfgen.common.CommonProxy")
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        GameRegistry.registerTileEntity(GeneratorTileEntity.class, new ResourceLocation(MODID, "rf_generator"));
        PROXY.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        PROXY.init();
    }
}
