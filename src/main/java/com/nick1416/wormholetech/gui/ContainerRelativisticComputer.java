package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.RelativisticComputerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** Chrono Field Generator: the Aetherius slot, RF, status and the on/off switch. */
public class ContainerRelativisticComputer extends SyncedIntsContainer {

    /** Background height; the player inventory is laid out from it (vanilla offsets). */
    public static final int GUI_HEIGHT = 166;
    /** The drawn 18x18 box in relativistic_computer.png (item position): the Aetherius slot. */
    public static final int SLOT_X = 80, SLOT_Y = 36;
    public static final int PLAYER_INV_Y = GUI_HEIGHT - 82;   // 84
    public static final int HOTBAR_Y = PLAYER_INV_Y + 58;      // 142

    private static final int ENERGY = 0, BOOSTED = 1, ENABLED = 2, AETHERIUS_LEFT = 3;

    private final RelativisticComputerTileEntity te;

    public ContainerRelativisticComputer(InventoryPlayer playerInv, RelativisticComputerTileEntity te) {
        super(4);
        this.te = te;
        addSlotToContainer(new net.minecraftforge.items.SlotItemHandler(te.items(), 0, SLOT_X, SLOT_Y));
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, PLAYER_INV_Y + r * 18));
        for (int c = 0; c < 9; c++)
            addSlotToContainer(new Slot(playerInv, c, 8 + c * 18, HOTBAR_Y));
    }

    public RelativisticComputerTileEntity getTe() { return te; }

    @Override
    protected int[] readServerValues() {
        return new int[] { te.getEnergyStored(), te.getBoosted(), te.isEnabled() ? 1 : 0, te.getAetheriusLeft() };
    }

    public int getClientEnergy() { return synced(ENERGY); }
    public int getClientBoosted() { return synced(BOOSTED); }
    public boolean isClientEnabled() { return synced(ENABLED) != 0; }
    /** Ticks of acceleration left in the seated Aetherius (0 = none). */
    public int getClientAetheriusLeft() { return synced(AETHERIUS_LEFT); }

    /** Button 0 switches the field on/off (sent with the vanilla enchant-item packet). */
    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == 0) {
            te.toggle();
            return true;
        }
        return false;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack in = slot.getStack(), ret = in.copy();
        if (index == 0) {
            if (!mergeItemStack(in, 1, inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (RelativisticComputerTileEntity.isAetherius(in)) {
            if (!mergeItemStack(in, 0, 1, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
        if (in.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        return ret;
    }
}
