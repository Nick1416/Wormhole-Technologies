package com.nick1416.wormholetech.derelict;

import com.nick1416.wormholetech.ModConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * A placed (unlocked) Quantum Field Modulator: endless free energy. Each tick it offers the
 * Wormhole Energy Converter's rate (stableConverterRfPerTick) in total, pushed to adjacent energy
 * receivers; cables that pull can take whatever of that budget is left.
 */
public class QuantumFieldModulatorTileEntity extends TileEntity implements ITickable {
    private int budget;

    private final IEnergyStorage output = new IEnergyStorage() {
        @Override public int receiveEnergy(int max, boolean sim) { return 0; }
        @Override public int extractEnergy(int max, boolean sim) {
            int a = Math.min(max, budget);
            if (!sim) budget -= a;
            return a;
        }
        @Override public int getEnergyStored() { return ModConfig.stableConverterRfPerTick; }
        @Override public int getMaxEnergyStored() { return ModConfig.stableConverterRfPerTick; }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return false; }
    };

    @Override public void onLoad() { com.nick1416.wormholetech.drone.ModulatorTracker.add(world, pos); }
    @Override public void invalidate() { super.invalidate(); com.nick1416.wormholetech.drone.ModulatorTracker.remove(world, pos); }
    @Override public void onChunkUnload() { com.nick1416.wormholetech.drone.ModulatorTracker.remove(world, pos); }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        budget = ModConfig.stableConverterRfPerTick;
        for (EnumFacing f : EnumFacing.VALUES) {
            if (budget <= 0) break;
            TileEntity te = world.getTileEntity(pos.offset(f));
            if (te == null || te instanceof QuantumFieldModulatorTileEntity) continue;
            IEnergyStorage t = te.getCapability(CapabilityEnergy.ENERGY, f.getOpposite());
            if (t == null || !t.canReceive()) continue;
            budget -= Math.max(0, t.receiveEnergy(budget, false));
        }
    }

    @Override public boolean hasCapability(Capability<?> cap, EnumFacing f) { return cap == CapabilityEnergy.ENERGY || super.hasCapability(cap, f); }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing f) {
        return cap == CapabilityEnergy.ENERGY ? (T) output : super.getCapability(cap, f);
    }
}
