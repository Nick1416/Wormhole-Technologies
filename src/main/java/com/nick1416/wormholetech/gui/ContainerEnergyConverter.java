package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.GeneratorTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerEnergyConverter extends ContainerLinkedMachine {

    public ContainerEnergyConverter(InventoryPlayer playerInv, GeneratorTileEntity te) {
        super(playerInv, te);
        addMachineSlot(GeneratorTileEntity.SLOT_FUEL, ContainerWormholeDuplicator.LINK_X, ROW_B_Y, false);
        addMachineSlot(GeneratorTileEntity.SLOT_UNSTABLE, ContainerWormholeDuplicator.UNSTABLE_X, ROW_B_Y, true);
        addPlayerInventory(playerInv);
    }
}
