package com.nick1416.wormholetech.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 * Base for machines that only run while connected to a wormhole (duplicators, energy converter).
 * Inventory and connection state are kept in the dropped machine item, like the refiner.
 */
public abstract class LinkedMachineTileEntity extends TileEntity implements ITickable {

    protected final LinkedInventory inv;
    protected final WormholeLink link;

    protected LinkedMachineTileEntity(int slots, int fuelSlot, int fuelLimit, int outSlot) {
        this.inv = new LinkedInventory(slots, fuelLimit) {
            @Override protected boolean isValidOther(int slot, ItemStack stack) {
                return !stack.isEmpty() && LinkedMachineTileEntity.this.isValid(slot, stack);
            }
            @Override protected void changed() { markDirty(); }
        };
        this.link = new WormholeLink(inv, fuelSlot, outSlot);
    }

    /** Validity for the non-fuel slots. */
    protected abstract boolean isValid(int slot, ItemStack stack);

    public LinkedInventory items() { return inv; }
    public WormholeLink getLink() { return link; }

    public boolean isUsableByPlayer(EntityPlayer player) {
        return world != null && world.getTileEntity(pos) == this
                && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("Items", inv.serializeNBT());
        tag.setTag("Link", link.write());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("Items")) inv.deserializeNBT(tag.getCompoundTag("Items"));
        if (tag.hasKey("Link")) link.read(tag.getCompoundTag("Link"));
    }

    /** Machine state into the dropped block item (only when there is something to keep). */
    public void writeToItem(ItemStack stack) {
        boolean empty = !link.isConnected() && link.getDecay() == 0;
        for (int i = 0; i < inv.getSlots() && empty; i++) empty = inv.getStackInSlot(i).isEmpty();
        if (empty && !hasExtraItemState()) return;
        NBTTagCompound tag = writeToNBT(new NBTTagCompound());
        tag.removeTag("x");
        tag.removeTag("y");
        tag.removeTag("z");
        tag.removeTag("id");
        stack.setTagCompound(tag);
    }

    /** Subclasses with more state (energy) keep the item tag even when the inventory is empty. */
    protected boolean hasExtraItemState() { return false; }

    public void readFromItem(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) return;
        readFromNBT(stack.getTagCompound());
        markDirty();
    }

    @Override
    public boolean hasCapability(Capability<?> c, EnumFacing f) {
        return c == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(c, f);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> c, EnumFacing f) {
        if (c == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) inv;
        return super.getCapability(c, f);
    }
}
