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

public class RelativisticComputerTileEntity extends TileEntity implements ITickable {

    public static final int BUFFER = 2_000_000;
    public static final int MAX_RECEIVE = 100_000;
    public static final int UPKEEP_RF_PER_TICK = 50_000;

    private final CompEnergy energy = new CompEnergy(BUFFER, MAX_RECEIVE);
    private ItemStack aetherius = ItemStack.EMPTY;
    private boolean active;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        boolean was = active;
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

    public boolean insertAetherius(ItemStack held) {
        if (held.isEmpty() || held.getItem() != Registration.AETHERIUS) return false;
        if (!aetherius.isEmpty()) return false;
        aetherius = held.splitStack(1);
        markDirty();
        return true;
    }

    public ItemStack ejectAetherius() {
        if (aetherius.isEmpty()) return ItemStack.EMPTY;
        ItemStack out = aetherius;
        aetherius = ItemStack.EMPTY;
        active = false;
        markDirty();
        return out;
    }

    public void dropContents(World world, BlockPos pos) {
        if (!aetherius.isEmpty()) {
            net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), aetherius);
            aetherius = ItemStack.EMPTY;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Energy", energy.getEnergyStored());
        tag.setBoolean("Active", active);
        if (!aetherius.isEmpty()) tag.setTag("Aetherius", aetherius.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        active = tag.getBoolean("Active");
        if (tag.hasKey("Aetherius")) aetherius = new ItemStack(tag.getCompoundTag("Aetherius"));
        else aetherius = ItemStack.EMPTY;
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
