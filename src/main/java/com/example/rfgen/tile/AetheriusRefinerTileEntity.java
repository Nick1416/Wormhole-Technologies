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

/** Furnace-like long refine: Compressed Naquadah → Aetherius in 1 hour (72000 ticks). */
public class AetheriusRefinerTileEntity extends TileEntity implements ITickable {

    public static final int COOK_TIME = 72_000; // 1 hour @ 20 TPS
    public static final int RF_PER_TICK = 100;
    public static final int BUFFER = 1_000_000;
    public static final int MAX_RECEIVE = 10_000;

    private final RefEnergy energy = new RefEnergy(BUFFER, MAX_RECEIVE);
    private ItemStack input = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;
    private int cook;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        boolean canCook = !input.isEmpty()
                && input.getItem() == Item.getItemFromBlock(Registration.COMPRESSED_NAQUADAH)
                && (output.isEmpty() || (output.getItem() == Registration.AETHERIUS && output.getCount() < output.getMaxStackSize()))
                && energy.getEnergyStored() >= RF_PER_TICK;

        if (canCook) {
            energy.consume(RF_PER_TICK);
            cook++;
            if (cook >= COOK_TIME) {
                input.shrink(1);
                if (input.getCount() <= 0) input = ItemStack.EMPTY;
                if (output.isEmpty()) output = new ItemStack(Registration.AETHERIUS);
                else output.grow(1);
                cook = 0;
            }
            markDirty();
        } else if (cook > 0 && (input.isEmpty() || energy.getEnergyStored() < RF_PER_TICK)) {
            // lose progress slowly when unpowered / empty — keep progress (nicer)
            markDirty();
        }
    }

    public int getProgressPercent() {
        if (COOK_TIME <= 0) return 0;
        return Math.min(100, (cook * 100) / COOK_TIME);
    }

    public int getEnergyStored() { return energy.getEnergyStored(); }

    public boolean insertInput(ItemStack held) {
        if (held.isEmpty()) return false;
        if (held.getItem() != Item.getItemFromBlock(Registration.COMPRESSED_NAQUADAH)) return false;
        if (!input.isEmpty()) return false;
        input = held.splitStack(1);
        cook = 0;
        markDirty();
        return true;
    }

    public ItemStack takeOutput() {
        if (output.isEmpty()) return ItemStack.EMPTY;
        ItemStack out = output;
        output = ItemStack.EMPTY;
        markDirty();
        return out;
    }

    public ItemStack ejectAll() {
        if (!output.isEmpty()) return takeOutput();
        if (!input.isEmpty()) {
            ItemStack out = input;
            input = ItemStack.EMPTY;
            cook = 0;
            markDirty();
            return out;
        }
        return ItemStack.EMPTY;
    }

    public void dropContents(World world, BlockPos pos) {
        if (!input.isEmpty()) {
            net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), input);
            input = ItemStack.EMPTY;
        }
        if (!output.isEmpty()) {
            net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), output);
            output = ItemStack.EMPTY;
        }
    }

    @Override

    public void writeToItem(net.minecraft.item.ItemStack stack) {
        net.minecraft.nbt.NBTTagCompound tag = writeToNBT(new net.minecraft.nbt.NBTTagCompound());
        tag.removeTag("x");
        tag.removeTag("y");
        tag.removeTag("z");
        tag.removeTag("id");
        stack.setTagCompound(tag);
    }

    public void readFromItem(net.minecraft.item.ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) return;
        readFromNBT(stack.getTagCompound());
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Energy", energy.getEnergyStored());
        tag.setInteger("Cook", cook);
        if (!input.isEmpty()) tag.setTag("Input", input.writeToNBT(new NBTTagCompound()));
        if (!output.isEmpty()) tag.setTag("Output", output.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        cook = tag.getInteger("Cook");
        input = tag.hasKey("Input") ? new ItemStack(tag.getCompoundTag("Input")) : ItemStack.EMPTY;
        output = tag.hasKey("Output") ? new ItemStack(tag.getCompoundTag("Output")) : ItemStack.EMPTY;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return (T) energy;
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
