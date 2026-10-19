package com.nick1416.wormholetech.derelict;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.tile.DialingComputerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * A placed (unlocked) Quantum Field Modulator: endless free energy, but only gate machinery can take it.
 * Each tick it offers the Wormhole Energy Converter's rate (stableConverterRfPerTick) to adjacent Dialing
 * and Gate Network Computers. It still keeps Xenolith drones charged in range (ModulatorTracker).
 * Any other energy receiver or cable touching it gets nothing: ordinary machinery was not built for
 * that kind of energy, and nearby players are told so when one is attached.
 */
public class QuantumFieldModulatorTileEntity extends TileEntity implements ITickable {
    private int budget;
    /** Faces with a refused energy neighbour at the last scan. */
    private int refusedMask;
    private int scanTimer;

    /** Pulling (cables, other machines) is refused; gate computers are pushed to directly. */
    private final IEnergyStorage output = new IEnergyStorage() {
        @Override public int receiveEnergy(int max, boolean sim) { return 0; }
        @Override public int extractEnergy(int max, boolean sim) { return 0; }
        @Override public int getEnergyStored() { return 0; }
        @Override public int getMaxEnergyStored() { return ModConfig.stableConverterRfPerTick; }
        @Override public boolean canExtract() { return false; }
        @Override public boolean canReceive() { return false; }
    };

    @Override public void onLoad() { com.nick1416.wormholetech.drone.ModulatorTracker.add(world, pos); }
    @Override public void invalidate() { super.invalidate(); com.nick1416.wormholetech.drone.ModulatorTracker.remove(world, pos); }
    @Override public void onChunkUnload() { com.nick1416.wormholetech.drone.ModulatorTracker.remove(world, pos); }

    /** Only gate machinery can take a modulator's energy. */
    public static boolean accepts(TileEntity te) { return te instanceof DialingComputerTileEntity; }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        budget = ModConfig.stableConverterRfPerTick;
        boolean scan = ++scanTimer >= 10;
        if (scan) scanTimer = 0;
        int refused = 0;
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity te = world.getTileEntity(pos.offset(f));
            if (te == null || te instanceof QuantumFieldModulatorTileEntity) continue;
            if (!accepts(te)) {
                if (scan && te.hasCapability(CapabilityEnergy.ENERGY, f.getOpposite())) refused |= 1 << f.getIndex();
                continue;
            }
            if (budget <= 0) continue;
            IEnergyStorage t = te.getCapability(CapabilityEnergy.ENERGY, f.getOpposite());
            if (t == null || !t.canReceive()) continue;
            budget -= Math.max(0, t.receiveEnergy(budget, false));
        }
        if (!scan) return;
        if ((refused & ~refusedMask) != 0) refuse();
        refusedMask = refused;
    }

    /** A new non-gate machine or cable was attached: fizzle and tell nearby players. */
    private void refuse() {
        world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH, net.minecraft.util.SoundCategory.BLOCKS, 0.6F, 1.6F);
        if (world instanceof net.minecraft.world.WorldServer)
            ((net.minecraft.world.WorldServer) world).spawnParticle(net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 8, 0.3, 0.2, 0.3, 0.01);
        for (EntityPlayer p : world.getEntitiesWithinAABB(EntityPlayer.class, new net.minecraft.util.math.AxisAlignedBB(pos).grow(8)))
            p.sendStatusMessage(new TextComponentTranslation("message.wormholetech.modulator.overwhelm"), true);
    }

    @Override public boolean hasCapability(Capability<?> cap, EnumFacing f) { return cap == CapabilityEnergy.ENERGY || super.hasCapability(cap, f); }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing f) {
        return cap == CapabilityEnergy.ENERGY ? (T) output : super.getCapability(cap, f);
    }
}
