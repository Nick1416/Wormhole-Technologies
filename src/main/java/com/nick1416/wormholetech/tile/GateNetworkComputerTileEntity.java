package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.stargate.GateNetwork;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Gate Network Computer: a Dialing Computer that can also dial any destination its user has
 * already travelled to, picked on the galaxy map. An address item in the slot always wins.
 */
public class GateNetworkComputerTileEntity extends DialingComputerTileEntity {
    private int selected = -1; // GateNetwork index

    public int getSelected() { return selected; }

    public void select(int i) {
        if (i < -1 || i >= GateNetwork.count()) return;
        if (selected == i) return;
        if (isOpen()) close(true);
        selected = i;
        markDirty();
        requestCheck();
    }

    private boolean slotHasAddress() { return isAddress(items().getStackInSlot(0)); }

    @Override protected boolean hasAddress() { return slotHasAddress() || selected >= 0; }

    @Override
    public int destination() {
        return slotHasAddress() || selected < 0 ? super.destination() : GateNetwork.dimension(selected);
    }

    @Override
    protected boolean destinationAvailable() {
        if (!slotHasAddress() && (selected == GateNetwork.TAL_VORA || selected == GateNetwork.SEL_VORA)) return GateNetwork.registered(selected);
        return slotHasAddress() || selected < 0 ? super.destinationAvailable() : GateNetwork.registered(selected);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Selected", selected);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        selected = tag.hasKey("Selected") ? tag.getInteger("Selected") : -1;
    }
}
