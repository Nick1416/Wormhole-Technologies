package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.ModConfig;
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
import net.minecraftforge.items.ItemStackHandler;

/**
 * Chrono Field Generator (registry name relativistic_computer, kept for old worlds). While switched on
 * and powered it gives every adjacent ticking machine chronoFieldExtraTicks extra ticks per tick (twice
 * as fast by default) for chronoFieldRfPerBlockPerTick RF each. Works on this mod's machines and on other
 * ticking blocks such as vanilla furnaces. Gate machinery, pads, modulators, the Repository and other
 * field generators are left alone.
 */
public class RelativisticComputerTileEntity extends TileEntity implements ITickable {

    private final CompEnergy energy = new CompEnergy(
            ModConfig.relativisticComputerBuffer, ModConfig.relativisticComputerMaxReceive);
    /** Old Relativistic Computers held Aetherius: it is handed back (dropped on top) once. */
    private final ItemStackHandler legacy = new ItemStackHandler(1);
    private boolean enabled = true;
    /** Machines sped up during the last tick (server; synced to the GUI). */
    private int boosted;

    public static boolean canBoost(TileEntity te) {
        return te instanceof ITickable && !te.isInvalid()
                && !(te instanceof RelativisticComputerTileEntity)
                && !(te instanceof DialingComputerTileEntity)
                && !(te instanceof WormholePadTileEntity)
                && !(te instanceof com.nick1416.wormholetech.derelict.QuantumFieldModulatorTileEntity)
                && !(te instanceof com.nick1416.wormholetech.knowledge.RepositoryTileEntity);
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        ItemStack old = legacy.getStackInSlot(0);
        if (!old.isEmpty()) {
            net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, old);
            legacy.setStackInSlot(0, ItemStack.EMPTY);
            markDirty();
        }
        int was = boosted;
        boosted = 0;
        if (enabled) {
            int cost = ModConfig.chronoFieldRfPerBlockPerTick, extra = Math.max(1, ModConfig.chronoFieldExtraTicks);
            for (EnumFacing f : EnumFacing.VALUES) {
                BlockPos p = pos.offset(f);
                if (!world.isBlockLoaded(p)) continue;
                TileEntity te = world.getTileEntity(p);
                if (te == null || !canBoost(te)) continue;
                if (energy.getEnergyStored() < cost) break;
                energy.consume(cost);
                boosted++;
                for (int k = 0; k < extra && !te.isInvalid(); k++) ((ITickable) te).update();
            }
            if (boosted > 0 && world.rand.nextInt(20) == 0 && world instanceof net.minecraft.world.WorldServer)
                ((net.minecraft.world.WorldServer) world).spawnParticle(net.minecraft.util.EnumParticleTypes.PORTAL,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6, 0.6, 0.6, 0.6, 0.2);
        }
        if (boosted > 0 || was != boosted) markDirty();
    }

    public boolean isEnabled() { return enabled; }
    public int getBoosted() { return boosted; }
    public boolean isActive() { return boosted > 0; }

    public void toggle() {
        enabled = !enabled;
        markDirty();
    }

    public int getEnergyStored() { return energy.getEnergyStored(); }

    public void dropContents(World world, BlockPos pos) {
        ItemStack stack = legacy.getStackInSlot(0);
        if (!stack.isEmpty()) {
            net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            legacy.setStackInSlot(0, ItemStack.EMPTY);
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
        tag.setBoolean("Enabled", enabled);
        tag.setTag("Items", legacy.serializeNBT());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        enabled = !tag.hasKey("Enabled") || tag.getBoolean("Enabled");
        if (tag.hasKey("Items")) {
            legacy.deserializeNBT(tag.getCompoundTag("Items"));
            if (legacy.getSlots() != 1) legacy.setSize(1);
        } else if (tag.hasKey("Aetherius")) {
            legacy.setStackInSlot(0, new ItemStack(tag.getCompoundTag("Aetherius")));
        }
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
