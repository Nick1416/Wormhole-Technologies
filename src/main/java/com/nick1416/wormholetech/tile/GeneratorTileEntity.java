package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.ModConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

/** Wormhole Energy Converter: produces RF only while connected to a wormhole. */
public class GeneratorTileEntity extends LinkedMachineTileEntity {

    // 0: Block of Refined Naquadah, 1: Unstable Block of Naquadah
    public static final int SLOT_FUEL = 0, SLOT_UNSTABLE = 1;

    private final GeneratorEnergyStorage energy = new GeneratorEnergyStorage(
            ModConfig.stableConverterBuffer, ModConfig.stableConverterMaxExtract);

    public GeneratorTileEntity() {
        super(2, SLOT_FUEL, 64, SLOT_UNSTABLE);
    }

    @Override
    protected int[] outputSlots() { return new int[] { SLOT_UNSTABLE }; }

    @Override
    protected boolean isValid(int slot, ItemStack stack) {
        return false; // the only other slot is an output
    }

    public int getEnergyStored() { return energy.getEnergyStored(); }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        if (link.tick() == WormholeLink.State.RUNNING) {
            energy.generate(ModConfig.stableConverterRfPerTick);
        }

        // push to neighbors
        for (EnumFacing face : EnumFacing.values()) {
            if (energy.getEnergyStored() <= 0) break;
            TileEntity te = world.getTileEntity(pos.offset(face));
            if (te == null) continue;
            IEnergyStorage target = te.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
            if (target == null) continue;

            int canExtract = energy.extractEnergy(ModConfig.stableConverterMaxExtract, true);
            if (canExtract <= 0) continue;

            int accepted = target.receiveEnergy(canExtract, false);
            if (accepted > 0) energy.extractEnergy(accepted, false);
        }
    }

    // ---- NBT ----
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Energy", energy.getEnergyStored());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
    }

    @Override
    protected boolean hasExtraItemState() { return energy.getEnergyStored() > 0; }

    // ---- Capability ----
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

    // energy store that only outputs
    private static class GeneratorEnergyStorage extends EnergyStorage {
        GeneratorEnergyStorage(int capacity, int maxExtract) {
            super(capacity, 0, maxExtract);
        }
        void setEnergy(int value) { this.energy = Math.max(0, Math.min(value, getMaxEnergyStored())); }
        void generate(int amount) { this.energy = Math.min(this.energy + amount, getMaxEnergyStored()); }
        @Override public boolean canReceive() { return false; }
    }
}
