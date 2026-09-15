package com.example.rfgen.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;                    // <-- add
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class GeneratorTileEntity extends TileEntity implements ITickable {   // <-- implements ITickable

    private static final int RATE_PER_TICK = 100_000;   // 100k RF/t
    private static final int BUFFER = 10_000_000;       // 10M RF
    private static final int MAX_EXTRACT = 100_000;     // per tick cap

    private final GeneratorEnergyStorage energy = new GeneratorEnergyStorage(BUFFER, MAX_EXTRACT);

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        // generate
        energy.generate(RATE_PER_TICK);

        // push to neighbors
        for (EnumFacing face : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(face));
            if (te == null) continue;
            IEnergyStorage target = te.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
            if (target == null) continue;

            int canExtract = energy.extractEnergy(MAX_EXTRACT, true);   // <-- use constant
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
        public GeneratorEnergyStorage(int capacity, int maxExtract) {
            super(capacity, 0, maxExtract);
        }
        public void setEnergy(int value) { this.energy = Math.min(value, getMaxEnergyStored()); }
        public void generate(int amount) { this.energy = Math.min(this.energy + amount, getMaxEnergyStored()); }
        @Override public boolean canReceive() { return false; }
        // (optional) if you want a getter:
        // public int getMaxExtractRate() { return this.maxExtract; }
    }
}
