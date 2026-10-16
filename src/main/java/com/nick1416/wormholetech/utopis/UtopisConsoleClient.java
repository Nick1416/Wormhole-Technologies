package com.nick1416.wormholetech.utopis;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Client-only entry point, kept out of the block so servers never load GUI classes. */
@SideOnly(Side.CLIENT)
public final class UtopisConsoleClient {
    private UtopisConsoleClient() {}
    public static void open(int entry) { Minecraft.getMinecraft().displayGuiScreen(new GuiUtopisConsole(entry)); }
}
