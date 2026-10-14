package com.nick1416.wormholetech.common;

public class CommonProxy {
    public void preInit() {}
    public void init() {}

    /** Client opens the Ancient Tablet GUI; server no-op. */
    public void openAncientTabletGui() {}
    public void openRepositoryGui(net.minecraft.nbt.NBTTagCompound data) {}
}
