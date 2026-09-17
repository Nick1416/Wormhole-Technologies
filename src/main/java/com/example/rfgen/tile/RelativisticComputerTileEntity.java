package com.example.rfgen.tile;

import com.example.rfgen.registry.Registration;
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

public class RelativisticComputerTileEntity extends TileEntity implements ITickable {

    public static final int BUFFER = 2_000_000;
    public static final int MAX_RECEIVE = 100_000;
    public static final int UPKEEP_RF_PER_TICK = 50_000;

    private final CompEnergy energy = new CompEnergy(BUFFER, MAX_RECEIVE);
    private final ItemStackHandler inv = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 && !stack.isEmpty() && stack.getItem() == Registration.AETHERIUS;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack)) return stack;
            return super.insertItem(slot, stack, simulate);
        }
    };
    private boolean active;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        boolean was = active;
        ItemStack aetherius = inv.getStackInSlot(0);
        if (!aetherius.isEmpty() && aetherius.getItem() == Registration.AETHERIUS
                && energy.getEnergyStored() >= UPKEEP_RF_PER_TICK) {
            energy.consume(UPKEEP_RF_PER_TICK);
            active = true;
        } else {
            active = false;
        }
        if (was != active) markDirty();
        else if (active) markDirty();
    }

    public boolean isActive() { return active; }

    public int getEnergyStored() { return energy.getEnergyStored(); }

    public IItemHandlerModifiable items() { return inv; }

    public void dropContents(World world, BlockPos pos) {
        ItemStack stack = inv.getStackInSlot(0);
        if (!stack.isEmpty()) {
            net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            inv.setStackInSlot(0, ItemStack.EMPTY);
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
        tag.setBoolean("Active", active);
        tag.setTag("Items", inv.serializeNBT());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        active = tag.getBoolean("Active");
        if (tag.hasKey("Items")) {
            inv.deserializeNBT(tag.getCompoundTag("Items"));
        } else if (tag.hasKey("Aetherius")) {
            // Migrate PR #19 item/world NBT that stored a single Aetherius stack
            inv.setStackInSlot(0, new ItemStack(tag.getCompoundTag("Aetherius")));
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

    private static class CompEnergy extends EnergyStorage {
        public CompEnergy(int cap, int maxReceive) { super(cap, maxReceive, 0); }
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
