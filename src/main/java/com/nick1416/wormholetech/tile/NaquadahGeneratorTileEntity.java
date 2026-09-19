package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class NaquadahGeneratorTileEntity extends TileEntity implements ITickable {

    private int ageTicks = 0;

    public int getAgeTicks() { return ageTicks; }

    public void setAgeTicks(int age) {
        this.ageTicks = Math.max(0, age);
    }

    public int getRemainingTicks() {
        return Math.max(0, ModConfig.naquadahGeneratorLifetimeTicks - ageTicks);
    }

    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    public void setEnergyStored(int value) {
        energy.setEnergy(value);
    }

    private final GeneratorEnergyStorage energy = new GeneratorEnergyStorage(
            ModConfig.naquadahGeneratorBuffer, ModConfig.naquadahGeneratorMaxExtract);

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        if (ageTicks >= ModConfig.naquadahGeneratorLifetimeTicks) {
            world.setBlockState(pos, Registration.UNSTABLE_NAQUADAH_BLOCK.getDefaultState(), 3);
            world.removeTileEntity(pos);
            return;
        }

        ageTicks++;
        energy.generate(ModConfig.naquadahGeneratorRfPerTick);

        for (EnumFacing face : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(face));
            if (te == null) continue;
            IEnergyStorage target = te.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
            if (target == null) continue;

            int canExtract = energy.extractEnergy(ModConfig.naquadahGeneratorMaxExtract, true);
            if (canExtract <= 0) continue;
            int accepted = target.receiveEnergy(canExtract, false);
            if (accepted > 0) energy.extractEnergy(accepted, false);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Energy", energy.getEnergyStored());
        tag.setInteger("Age", ageTicks);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        if (tag.hasKey("Age")) ageTicks = tag.getInteger("Age");
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

    private static class GeneratorEnergyStorage extends EnergyStorage {
        public GeneratorEnergyStorage(int capacity, int maxExtract) {
            super(capacity, 0, maxExtract);
        }
        public void setEnergy(int value) { this.energy = Math.min(value, getMaxEnergyStored()); }
        public void generate(int amount) { this.energy = Math.min(this.energy + amount, getMaxEnergyStored()); }
        @Override public boolean canReceive() { return false; }
    }
}
