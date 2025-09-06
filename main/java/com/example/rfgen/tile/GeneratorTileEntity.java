package com.example.rfgen.tile;

import cofh.redstoneflux.api.IEnergyProvider;
import net.minecraft.util.EnumFacing;


import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;





public class GeneratorTileEntity extends TileEntity implements ITickable, IEnergyProvider {
    private ItemStack display = ItemStack.EMPTY;
    private final GeneratorEnergyStorage energy = new GeneratorEnergyStorage(10_000_000, 200_000);

    @Override
    public void update() {
        if (world.isRemote) return;
        int rate = getRateFor(display);
        if (rate > 0) energy.generate(rate);

        for (EnumFacing f : EnumFacing.values()) {
            TileEntity te = world.getTileEntity(pos.offset(f));
            if (te == null) continue;
            IEnergyStorage target = te.getCapability(CapabilityEnergy.ENERGY, f.getOpposite());
            if (target == null) continue;
            int canExtract = energy.extractEnergy(energy.getMaxExtractRate(), true);
            if (canExtract > 0) {
                int accepted = target.receiveEnergy(canExtract, false);
                if (accepted > 0) energy.extractEnergy(accepted, false);
            }
        }
    }

    public ItemStack getDisplayItem() { return display; }
    public ItemStack setDisplayItem(ItemStack stack) {
        ItemStack prev = this.display.copy();
        this.display = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        markDirty();
        return prev;
    }

    private int getRateFor(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.getItem() == Item.getItemFromBlock(Blocks.DIAMOND_BLOCK)) return 100_000;
        if (stack.getItem() == Item.getItemFromBlock(Blocks.IRON_BLOCK)) return 10_000;
        if (stack.getItem() == Item.getItemFromBlock(Blocks.GOLD_BLOCK)) return 40_000;
        if (stack.getItem() == Item.getItemFromBlock(Blocks.EMERALD_BLOCK)) return 80_000;
        if (stack.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) return 5_000;
        return 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound item = new NBTTagCompound();
        if (!display.isEmpty()) display.writeToNBT(item);
        tag.setTag("Display", item);
        tag.setInteger("Energy", energy.getEnergyStored());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("Display")) this.display = new ItemStack(tag.getCompoundTag("Display"));
        if (tag.hasKey("Energy")) this.energy.setEnergy(tag.getInteger("Energy"));
    }

    private static class GeneratorEnergyStorage extends EnergyStorage {
        public GeneratorEnergyStorage(int capacity, int maxExtract) { super(capacity, 0, maxExtract); }
        public void setEnergy(int e) { this.energy = Math.min(e, getMaxEnergyStored()); }
        public void generate(int amount) { this.energy = Math.min(this.energy + amount, getMaxEnergyStored()); }
        public int getMaxExtractRate() { return this.maxExtract; }
        @Override public boolean canReceive() { return false; }
    }

 // --- RF API support (for Thermal Fluxducts) ---
    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true; // allow ducts on all sides
    }

    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
        return energy.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return energy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return energy.getMaxEnergyStored();
    }

    
}
