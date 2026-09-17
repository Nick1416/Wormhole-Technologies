package com.example.rfgen.tile;

import com.example.rfgen.registry.Registration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

/** Furnace-like long refine: Compressed Naquadah → Aetherius in 1 hour (72000 ticks). */
public class AetheriusRefinerTileEntity extends TileEntity implements ITickable {

    public static final int COOK_TIME = 72_000; // 1 hour @ 20 TPS
    public static final int RF_PER_TICK = 100;
    public static final int BUFFER = 1_000_000;
    public static final int MAX_RECEIVE = 10_000;

    private final RefEnergy energy = new RefEnergy(BUFFER, MAX_RECEIVE);
    // 0: input (compressed naquadah), 1: output (aetherius)
    private final ItemStackHandler inv = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (slot == 0) {
                return stack.getItem() == Item.getItemFromBlock(Registration.COMPRESSED_NAQUADAH);
            }
            return false; // output extract-only
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack)) return stack;
            return super.insertItem(slot, stack, simulate);
        }
    };
    private int cook;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        ItemStack input = inv.getStackInSlot(0);
        ItemStack output = inv.getStackInSlot(1);

        boolean canCook = !input.isEmpty()
                && input.getItem() == Item.getItemFromBlock(Registration.COMPRESSED_NAQUADAH)
                && (output.isEmpty() || (output.getItem() == Registration.AETHERIUS && output.getCount() < output.getMaxStackSize()))
                && energy.getEnergyStored() >= RF_PER_TICK;

        if (canCook) {
            energy.consume(RF_PER_TICK);
            cook++;
            if (cook >= COOK_TIME) {
                input.shrink(1);
                if (input.getCount() <= 0) inv.setStackInSlot(0, ItemStack.EMPTY);
                if (output.isEmpty()) inv.setStackInSlot(1, new ItemStack(Registration.AETHERIUS));
                else {
                    output.grow(1);
                    inv.setStackInSlot(1, output);
                }
                cook = 0;
            }
            markDirty();
        } else if (cook > 0 && (input.isEmpty() || energy.getEnergyStored() < RF_PER_TICK)) {
            // keep progress when unpowered / empty
            markDirty();
        }
    }

    public int getCookTime() { return cook; }

    public float getCookProgress() {
        if (COOK_TIME <= 0) return 0f;
        return Math.min(1f, cook / (float) COOK_TIME);
    }

    public int getProgressPercent() {
        if (COOK_TIME <= 0) return 0;
        return Math.min(100, (cook * 100) / COOK_TIME);
    }

    public int getCook() { return cook; }

    public int getEnergyStored() { return energy.getEnergyStored(); }

    public IItemHandlerModifiable items() { return inv; }

    public void dropContents(World world, BlockPos pos) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                inv.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    public void writeToItem(ItemStack stack) {
        NBTTagCompound tag = writeToNBT(new NBTTagCompound());
        tag.removeTag("x");
        tag.removeTag("y");
        tag.removeTag("z");
        tag.removeTag("id");
        stack.setTagCompound(tag);
    }

    public void readFromItem(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) return;
        readFromNBT(stack.getTagCompound());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Energy", energy.getEnergyStored());
        tag.setInteger("Cook", cook);
        tag.setTag("Items", inv.serializeNBT());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        cook = tag.getInteger("Cook");
        if (tag.hasKey("Items")) {
            inv.deserializeNBT(tag.getCompoundTag("Items"));
        } else {
            // Migrate PR #19 Input/Output compound tags
            if (tag.hasKey("Input")) inv.setStackInSlot(0, new ItemStack(tag.getCompoundTag("Input")));
            if (tag.hasKey("Output")) inv.setStackInSlot(1, new ItemStack(tag.getCompoundTag("Output")));
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY
                || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return (T) energy;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) inv;
        return super.getCapability(capability, facing);
    }

    private static class RefEnergy extends EnergyStorage {
        public RefEnergy(int cap, int maxReceive) { super(cap, maxReceive, 0); }
        public void setEnergy(int v) { this.energy = Math.min(Math.max(0, v), getMaxEnergyStored()); }
        public int consume(int amount) {
            int t = Math.min(amount, this.energy);
            this.energy -= t;
            return t;
        }
        @Override public boolean canReceive() { return true; }
        @Override public boolean canExtract() { return false; }
    }
}
