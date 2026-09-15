package com.example.rfgen.gui;

import com.example.rfgen.tile.UnstableWormholeDuplicatorTileEntity;
import com.example.rfgen.tile.WormholeDuplicatorTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerWormholeDuplicator extends Container {

    // slot positions (aligned to your GUI)
    private static final int DIAMOND_X = 44, DIAMOND_Y = 36;
    private static final int TEMPLATE_X = 80, TEMPLATE_Y = 36;
    private static final int OUTPUT_X  =116, OUTPUT_Y  = 36;

    // Overloads for both tile types
    public ContainerWormholeDuplicator(InventoryPlayer playerInv, WormholeDuplicatorTileEntity te) {
        this(playerInv, te.items());
    }
    public ContainerWormholeDuplicator(InventoryPlayer playerInv, UnstableWormholeDuplicatorTileEntity te) {
        this(playerInv, te.items());
    }

    // Common constructor that builds from a generic item handler
    private ContainerWormholeDuplicator(InventoryPlayer playerInv, IItemHandlerModifiable inv) {
        // machine slots
        addSlotToContainer(new SlotItemHandler(inv, 0, DIAMOND_X,  DIAMOND_Y));                  // diamonds
        addSlotToContainer(new SlotItemHandler(inv, 1, TEMPLATE_X, TEMPLATE_Y));                 // template
        addSlotToContainer(new SlotItemHandler(inv, 2, OUTPUT_X,   OUTPUT_Y) {                   // output only
            @Override public boolean isItemValid(ItemStack stack) { return false; }
        });

        // player inventory (3 rows)
        int startY = 84;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, startY + r * 18));
        // hotbar
        for (int c = 0; c < 9; c++)
            addSlotToContainer(new Slot(playerInv, c, 8 + c * 18, startY + 58));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }

    // standard shift-click logic
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack in = slot.getStack();
        ret = in.copy();

        final int teSlots = 3;
        if (index < teSlots) {
            // move from machine -> player
            if (!this.mergeItemStack(in, teSlots, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
            slot.onSlotChange(in, ret);
        } else {
            // move from player -> machine (prefer diamonds to slot 0, else template to slot 1)
            if (in.getItem() == net.minecraft.init.Items.DIAMOND) {
                if (!this.mergeItemStack(in, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                if (!this.mergeItemStack(in, 1, 2, false)) return ItemStack.EMPTY;
            }
        }

        if (in.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (in.getCount() == ret.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, in);
        return ret;
    }
}
