package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.stargate.GateNetwork;
import com.nick1416.wormholetech.tile.GateNetworkComputerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerGateNetworkComputer extends ContainerDialingComputer {
    /** Button ids: SELECT_BASE + destination index; CLEAR deselects. */
    public static final int SELECT_BASE = 10, CLEAR = 9;
    private final GateNetworkComputerTileEntity gnc;
    private final EntityPlayer player;

    public ContainerGateNetworkComputer(InventoryPlayer inv, GateNetworkComputerTileEntity te, EntityPlayer player) {
        super(inv, te, 2);
        this.gnc = te;
        this.player = player;
    }

    @Override
    protected int[] readServerValues() {
        int[] b = super.readServerValues();
        int visited = player instanceof EntityPlayerMP ? GateNetwork.visitedMask((EntityPlayerMP) player) : 0;
        return new int[] { b[0], b[1], b[2], visited, gnc.getSelected() };
    }

    public int getClientVisited() { return synced(3); }
    public int getClientSelected() { return synced(4); }

    @Override
    public boolean enchantItem(EntityPlayer p, int id) {
        if (id == CLEAR && canInteractWith(p)) { gnc.select(-1); return true; }
        int i = id - SELECT_BASE;
        if (i >= 0 && i < GateNetwork.count() && canInteractWith(p)) {
            if (p instanceof EntityPlayerMP && (GateNetwork.visitedMask((EntityPlayerMP) p) & 1 << i) != 0) gnc.select(i);
            return true;
        }
        return super.enchantItem(p, id);
    }
}
