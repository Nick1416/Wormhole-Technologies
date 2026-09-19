package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;                    // <-- add
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

// imports to add:
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;

public class UnstableGeneratorTileEntity extends TileEntity implements ITickable {


    private int ageTicks = 0;  // persisted

    public int getAgeTicks() { return ageTicks; }

    public void setAgeTicks(int age) {
        this.ageTicks = Math.max(0, age);
    }

    public int getRemainingTicks() {
        return Math.max(0, ModConfig.unstableConverterLifetimeTicks - ageTicks);
    }

    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    public void setEnergyStored(int value) {
        energy.setEnergy(value);
    }

    private final GeneratorEnergyStorage energy = new GeneratorEnergyStorage(
            ModConfig.unstableConverterBuffer, ModConfig.unstableConverterMaxExtract);

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        // if expired, convert into your Unstable Naquadah Block and stop ticking
        if (ageTicks >= ModConfig.unstableConverterLifetimeTicks) {
            // change block
            world.setBlockState(pos, Registration.UNSTABLE_NAQUADAH_BLOCK.getDefaultState(), 3);

            // remove this tile entity (the new block shouldn't have one)
            world.removeTileEntity(pos);

            return;
        }


        ageTicks++;                           // count lifetime
        energy.generate(ModConfig.unstableConverterRfPerTick);

        // push to neighbors
        for (EnumFacing face : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(face));
            if (te == null) continue;
            IEnergyStorage target = te.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
            if (target == null) continue;

            int canExtract = energy.extractEnergy(ModConfig.unstableConverterMaxExtract, true);
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
        tag.setInteger("Age", ageTicks);            // save age
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        if (tag.hasKey("Age")) ageTicks = tag.getInteger("Age");
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