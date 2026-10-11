package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.util.WormholeFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Item handler with a wormhole fuel slot. The fuel slot stores plain blocks; the decay of the
 * first block lives in the {@link WormholeLink}. Taking from the slot while that block is part-way
 * decayed hands out just that block with its progress in NBT, and a partially decayed block put
 * into the empty slot hands its progress back to the link.
 */
public abstract class LinkedInventory extends ItemStackHandler {

    private final int fuelLimit;
    private WormholeLink link;
    private int fuelSlot = -1;

    protected LinkedInventory(int size, int fuelLimit) {
        super(size);
        this.fuelLimit = fuelLimit;
    }

    void attach(WormholeLink link, int fuelSlot) {
        this.link = link;
        this.fuelSlot = fuelSlot;
    }

    public int getFuelSlot() { return fuelSlot; }

    /** Validity for the machine's other slots. */
    protected abstract boolean isValidOther(int slot, ItemStack stack);

    /** Mark the owning tile dirty. */
    protected abstract void changed();

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == fuelSlot) {
            return WormholeFuel.isFuel(stack) && (!WormholeFuel.isPartial(stack) || getStackInSlot(slot).isEmpty());
        }
        return isValidOther(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot == fuelSlot ? fuelLimit : 64;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot != fuelSlot || stack.isEmpty()) return super.insertItem(slot, stack, simulate);
        if (!isItemValid(slot, stack)) return stack;
        if (WormholeFuel.isPartial(stack)) {
            if (!simulate) {
                stacks.set(slot, WormholeFuel.fresh(new ItemStack(stack.getItem(), 1, stack.getMetadata())));
                link.adopt(WormholeFuel.decayOf(stack));
                onContentsChanged(slot);
            }
            ItemStack rest = stack.copy();
            rest.shrink(1);
            return rest;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != fuelSlot || amount <= 0 || getStackInSlot(slot).isEmpty() || link.getDecay() <= 0) {
            return super.extractItem(slot, amount, simulate);
        }
        // The first block is part-way through: only it comes out, carrying its progress.
        ItemStack out = WormholeFuel.block(link.getDecay());
        if (!simulate) {
            int d = link.takeCurrent();
            out = WormholeFuel.block(d);
            super.extractItem(slot, 1, false);
        }
        return out;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot == fuelSlot && WormholeFuel.isPartial(stack)) {
            int d = WormholeFuel.decayOf(stack);
            super.setStackInSlot(slot, WormholeFuel.fresh(stack));
            link.adopt(d);
            return;
        }
        super.setStackInSlot(slot, stack);
    }

    /** Keeps this machine's slot count when loading older saves with fewer slots. */
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagCompound copy = nbt.copy();
        copy.setInteger("Size", stacks.size());
        super.deserializeNBT(copy);
    }

    /** Machine-side output write (bypasses isItemValid). */
    void setOutput(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    /** Uses up the first fuel block. */
    void consumeFuel() {
        ItemStack s = stacks.get(fuelSlot);
        s.shrink(1);
        if (s.isEmpty()) stacks.set(fuelSlot, ItemStack.EMPTY);
        onContentsChanged(fuelSlot);
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (slot == fuelSlot && link != null && stacks.get(slot).isEmpty()) link.onFuelEmptied();
        changed();
    }
}
