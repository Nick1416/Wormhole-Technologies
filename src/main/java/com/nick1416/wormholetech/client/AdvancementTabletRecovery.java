package com.nick1416.wormholetech.client;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.network.MessageRequestTablet;
import com.nick1416.wormholetech.network.ModNetwork;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = WormholeTech.MODID, value = Side.CLIENT)
public class AdvancementTabletRecovery {

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        GuiScreen gui = event.getGui();
        if (!(gui instanceof GuiScreenAdvancements)) return;
        ModNetwork.CHANNEL.sendToServer(new MessageRequestTablet());
    }
}
