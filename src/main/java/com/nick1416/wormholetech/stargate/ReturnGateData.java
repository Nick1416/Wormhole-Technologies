package com.nick1416.wormholetech.stargate;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

/** Where the return gate on P3X-1416 stands, so every trip reuses it. */
public class ReturnGateData extends WorldSavedData {
    public static final String NAME = WormholeTech.MODID + "_return_gate";

    public GateShape gate;
    public BlockPos arrival;

    public ReturnGateData() { this(NAME); }
    public ReturnGateData(String name) { super(name); }

    public static ReturnGateData get(World world) {
        ReturnGateData d = (ReturnGateData) world.getPerWorldStorage().getOrLoadData(ReturnGateData.class, NAME);
        if (d == null) {
            d = new ReturnGateData();
            world.getPerWorldStorage().setData(NAME, d);
        }
        return d;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        gate = GateShape.read(tag.getCompoundTag("Gate"));
        arrival = tag.hasKey("Arrival") ? BlockPos.fromLong(tag.getLong("Arrival")) : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        if (gate != null) tag.setTag("Gate", gate.write());
        if (arrival != null) tag.setLong("Arrival", arrival.toLong());
        return tag;
    }
}
