package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.tile.RelativisticComputerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerRelativisticComputer extends SyncedIntsContainer {

    /** Background height; the player inventory is laid out from it (vanilla offsets). */
    public static final int GUI_HEIGHT = 166;

    // Item position (the drawn 18x18 box in relativistic_computer.png sits at x-1, y-1)
    public static final int SLOT_X = 80, SLOT_Y = 36;
    public static final int PLAYER_INV_Y = GUI_HEIGHT - 82;   // 84
    public static final int HOTBAR_Y = PLAYER_INV_Y + 58;      // 142

    private static final int ENERGY = 0, ACTIVE = 1;

    private final RelativisticComputerTileEntity te;

    public ContainerRelativisticComputer(InventoryPlayer playerInv, RelativisticComputerTileEntity te) {
        super(2);
        this.te = te;
        IItemHandlerModifiable inv = te.items();

        addSlotToContainer(new SlotItemHandler(inv, 0, SLOT_X, SLOT_Y));

        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, PLAYER_INV_Y + r * 18));
        for (int c = 0; c < 9; c++)
            addSlotToContainer(new Slot(playerInv, c, 8 + c * 18, HOTBAR_Y));
    }

    public RelativisticComputerTileEntity getTe() { return te; }

    @Override
    protected int[] readServerValues() {
        return new int[] { te.getEnergyStored(), te.isActive() ? 1 : 0 };
    }

    /** Client-synced energy (full int). */
    public int getClientEnergy() { return synced(ENERGY); }

    public boolean isClientActive() { return synced(ACTIVE) != 0; }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack in = slot.getStack();
        ret = in.copy();

        final int teSlots = 1;
        if (index < teSlots) {
            if (!this.mergeItemStack(in, teSlots, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
            slot.onSlotChange(in, ret);
        } else {
            if (in.getItem() == Registration.AETHERIUS) {
                if (!this.mergeItemStack(in, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (in.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (in.getCount() == ret.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, in);
        return ret;
    }
}
