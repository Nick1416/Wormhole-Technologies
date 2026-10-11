package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.UnstableWormholeDuplicatorTileEntity;
import com.nick1416.wormholetech.tile.WormholeDuplicatorTileEntity;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerWormholeDuplicator extends ContainerLinkedMachine {

    // slot item positions (keep in sync with tools/gen_machine_guis.py)
    public static final int FUEL_X = 48, TEMPLATE_X = 84, OUTPUT_X = 138;
    public static final int LINK_X = 84, UNSTABLE_X = 138;

    public ContainerWormholeDuplicator(InventoryPlayer playerInv, WormholeDuplicatorTileEntity te) {
        super(playerInv, te);
        addMachineSlot(WormholeDuplicatorTileEntity.SLOT_NUGGET, FUEL_X, ROW_A_Y, false);
        addMachineSlot(WormholeDuplicatorTileEntity.SLOT_TEMPLATE, TEMPLATE_X, ROW_A_Y, false);
        addMachineSlot(WormholeDuplicatorTileEntity.SLOT_OUTPUT, OUTPUT_X, ROW_A_Y, true);
        addMachineSlot(WormholeDuplicatorTileEntity.SLOT_FUEL, LINK_X, ROW_B_Y, false);
        addMachineSlot(WormholeDuplicatorTileEntity.SLOT_UNSTABLE, UNSTABLE_X, ROW_B_Y, true);
        addPlayerInventory(playerInv);
    }

    public ContainerWormholeDuplicator(InventoryPlayer playerInv, UnstableWormholeDuplicatorTileEntity te) {
        super(playerInv, te);
        addMachineSlot(UnstableWormholeDuplicatorTileEntity.SLOT_DIAMOND, FUEL_X, ROW_A_Y, false);
        addMachineSlot(UnstableWormholeDuplicatorTileEntity.SLOT_TEMPLATE, TEMPLATE_X, ROW_A_Y, false);
        addMachineSlot(UnstableWormholeDuplicatorTileEntity.SLOT_OUTPUT, OUTPUT_X, ROW_A_Y, true);
        addMachineSlot(UnstableWormholeDuplicatorTileEntity.SLOT_FUEL, LINK_X, ROW_B_Y, false);
        addPlayerInventory(playerInv);
    }
}
