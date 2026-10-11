package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.GeneratorTileEntity;
import com.nick1416.wormholetech.tile.LinkedMachineTileEntity;
import com.nick1416.wormholetech.tile.WormholeLink;
import com.nick1416.wormholetech.util.WormholeFuel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Container for machines with a wormhole connection. Syncs status, remaining decay ticks, the
 * server's decay lifetime and stored RF as full ints. Button id {@link #BUTTON_ESTABLISH} arrives
 * through the vanilla "enchant item" packet and is validated here on the server.
 */
public abstract class ContainerLinkedMachine extends SyncedIntsContainer {

    public static final int GUI_WIDTH = 204, GUI_HEIGHT = 190;
    public static final int INV_X = 22, INV_Y = 110, HOTBAR_Y = 168;
    public static final int ROW_A_Y = 18, ROW_B_Y = 42;
    public static final int BUTTON_ESTABLISH = 0;

    protected final LinkedMachineTileEntity te;
    private int machineSlots;
    private int fuelIndex = -1;

    protected ContainerLinkedMachine(InventoryPlayer playerInv, LinkedMachineTileEntity te) {
        super(4);
        this.te = te;
    }

    protected void addMachineSlot(int index, int x, int y, boolean outputOnly) {
        IItemHandler h = te.items();
        Slot s;
        if (index == te.items().getFuelSlot()) {
            fuelIndex = inventorySlots.size();
            s = new FuelSlot(h, index, x, y);
        } else if (outputOnly) {
            s = new SlotItemHandler(h, index, x, y) {
                @Override public boolean isItemValid(ItemStack stack) { return false; }
            };
        } else {
            s = new SlotItemHandler(h, index, x, y);
        }
        addSlotToContainer(s);
        machineSlots++;
    }

    protected void addPlayerInventory(InventoryPlayer playerInv) {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, INV_X + c * 18, INV_Y + r * 18));
        for (int c = 0; c < 9; c++)
            addSlotToContainer(new Slot(playerInv, c, INV_X + c * 18, HOTBAR_Y));
    }

    public int getFuelContainerIndex() { return fuelIndex; }

    // ---- synced values ----
    @Override
    protected int[] readServerValues() {
        WormholeLink link = te.getLink();
        int energy = te instanceof GeneratorTileEntity ? ((GeneratorTileEntity) te).getEnergyStored() : 0;
        return new int[] { link.getStatus(), link.getRemaining(), WormholeFuel.lifetime(), energy };
    }

    public int getStatus() { return synced(0); }
    public int getRemaining() { return synced(1); }
    public int getLifetime() { return Math.max(1, synced(2)); }
    public int getEnergy() { return synced(3); }

    /** Client-side check that mirrors {@link WormholeLink#canEstablish()} for the button state. */
    public boolean canEstablishClient() {
        return getStatus() == WormholeLink.STATUS_NONE && fuelIndex >= 0
                && WormholeFuel.isFuel(inventorySlots.get(fuelIndex).getStack());
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id != BUTTON_ESTABLISH || !te.isUsableByPlayer(player)) return false;
        te.getLink().establish();
        return true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isSpectator() && te.isUsableByPlayer(player);
    }

    // ---- fuel slot protection ----
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        // Hotbar-key swaps replace the slot without going through extract; refuse them on the fuel slot.
        if (slotId == fuelIndex && fuelIndex >= 0 && clickType == ClickType.SWAP) return ItemStack.EMPTY;
        return super.slotClick(slotId, dragType, clickType, player);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return slot.slotNumber != fuelIndex && super.canMergeSlot(stack, slot);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        int end = inventorySlots.size();

        if (index == fuelIndex) {
            // Goes through extract so a part-decayed block leaves with its progress.
            IItemHandler h = te.items();
            int f = te.items().getFuelSlot();
            ItemStack sim = h.extractItem(f, 64, true);
            if (sim.isEmpty()) return ItemStack.EMPTY;
            ItemStack moving = sim.copy();
            mergeItemStack(moving, machineSlots, end, true);
            int moved = sim.getCount() - moving.getCount();
            if (moved > 0) h.extractItem(f, moved, false);
            return ItemStack.EMPTY;
        }

        ItemStack in = slot.getStack();
        ItemStack ret = in.copy();
        if (index < machineSlots) {
            if (!mergeItemStack(in, machineSlots, end, true)) return ItemStack.EMPTY;
            slot.onSlotChange(in, ret);
        } else {
            boolean moved = false;
            if (WormholeFuel.isFuel(in) && fuelIndex >= 0) {
                moved = mergeItemStack(in, fuelIndex, fuelIndex + 1, false);
            } else {
                for (int i = 0; i < machineSlots && !in.isEmpty(); i++) {
                    if (i != fuelIndex && inventorySlots.get(i).isItemValid(in)) {
                        moved |= mergeItemStack(in, i, i + 1, false);
                    }
                }
            }
            if (!moved) return ItemStack.EMPTY;
        }
        if (in.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (in.getCount() == ret.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, in);
        return ret;
    }

    /**
     * Fuel slot. Forge's SlotItemHandler validity checks briefly empty the slot, which would end
     * the connection, so validity and limits are answered directly.
     */
    static class FuelSlot extends SlotItemHandler {
        FuelSlot(IItemHandler h, int index, int x, int y) { super(h, index, x, y); }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return getItemHandler().isItemValid(getSlotIndex(), stack);
        }

        @Override
        public int getItemStackLimit(ItemStack stack) {
            return Math.min(getItemHandler().getSlotLimit(getSlotIndex()), stack.getMaxStackSize());
        }
    }
}
