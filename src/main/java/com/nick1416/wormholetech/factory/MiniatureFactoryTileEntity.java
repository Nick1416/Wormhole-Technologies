package com.nick1416.wormholetech.factory;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.compat.CompactMachinesCompat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

/**
 * Does what a Miniaturization Field does, without building or throwing: put the blocks of any field
 * recipe into the input slots and it crafts the result (no catalyst item needed), using
 * {@link ModConfig#miniatureFactoryRfPerTick} RF per tick for the recipe's duration. Inputs can be
 * inserted and outputs extracted by hoppers and pipes from any side.
 */
public class MiniatureFactoryTileEntity extends TileEntity implements ITickable {
    public static final int INPUTS = 18, OUTPUTS = 4, SLOTS = INPUTS + OUTPUTS;
    public static final int IDLE = 0, RUNNING = 1, NEED_RF = 2, OUTPUT_FULL = 3, NO_FIELD = 4;

    private final ItemStackHandler inv = new ItemStackHandler(SLOTS) {
        @Override protected void onContentsChanged(int slot) { markDirty(); if (slot < INPUTS) check = true; }
    };
    /** Receives RF from outside; never gives any back. */
    private static class Battery extends EnergyStorage {
        Battery() { super(2_000_000, 50_000, 0); }
        void set(int v) { energy = Math.max(0, Math.min(capacity, v)); }
    }
    private final Battery energy = new Battery();
    private ItemStack crafting = ItemStack.EMPTY;
    private int progress, total, status = IDLE;
    private boolean check = true;
    private int timer;

    /** Automation view: insert into inputs only, extract from outputs only. */
    private final IItemHandler automation = new IItemHandler() {
        @Override public int getSlots() { return SLOTS; }
        @Override public ItemStack getStackInSlot(int s) { return inv.getStackInSlot(s); }
        @Override public ItemStack insertItem(int s, ItemStack st, boolean sim) { return s < INPUTS ? inv.insertItem(s, st, sim) : st; }
        @Override public ItemStack extractItem(int s, int n, boolean sim) { return s >= INPUTS ? inv.extractItem(s, n, sim) : ItemStack.EMPTY; }
        @Override public int getSlotLimit(int s) { return inv.getSlotLimit(s); }
    };

    public ItemStackHandler items() { return inv; }
    public int getEnergyStored() { return energy.getEnergyStored(); }
    public int getProgress() { return progress; }
    public int getTotal() { return total; }
    public int getStatus() { return status; }

    @Override
    public void update() {
        if (world.isRemote) return;
        int before = status;
        if (crafting.isEmpty()) {
            status = CompactMachinesCompat.fieldRecipes().isEmpty() ? NO_FIELD : IDLE;
            if ((check || ++timer >= 40) && status == IDLE) { timer = 0; check = false; tryStart(); }
        }
        if (!crafting.isEmpty()) {
            if (progress < total) {
                int cost = ModConfig.miniatureFactoryRfPerTick;
                if (energy.getEnergyStored() >= cost) {
                    energy.set(energy.getEnergyStored() - cost);
                    progress++;
                    status = RUNNING;
                } else status = NEED_RF;
                markDirty();
            }
            if (progress >= total) {
                if (fitsOutput(crafting)) {
                    ItemStack left = crafting;
                    for (int i = INPUTS; i < SLOTS && !left.isEmpty(); i++) left = inv.insertItem(i, left, false);
                    crafting = ItemStack.EMPTY; progress = 0; total = 0; status = IDLE; check = true;
                    markDirty();
                } else status = OUTPUT_FULL;
            }
        }
        if (status != before) markDirty();
    }

    private boolean fitsOutput(ItemStack s) {
        ItemStack left = s.copy();
        for (int i = INPUTS; i < SLOTS && !left.isEmpty(); i++) left = inv.insertItem(i, left, true);
        return left.isEmpty();
    }

    private static boolean same(ItemStack have, ItemStack need) {
        if (have.isEmpty() || have.getItem() != need.getItem()) return false;
        return !need.getHasSubtypes() || need.getMetadata() == net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE || have.getMetadata() == need.getMetadata();
    }

    private int count(ItemStack need) {
        int n = 0;
        for (int i = 0; i < INPUTS; i++) if (same(inv.getStackInSlot(i), need)) n += inv.getStackInSlot(i).getCount();
        return n;
    }

    private void tryStart() {
        for (Object r : CompactMachinesCompat.fieldRecipes()) {
            List<ItemStack> req = CompactMachinesCompat.required(r);
            if (req.isEmpty()) continue;
            boolean ok = true;
            for (ItemStack need : req) if (count(need) < need.getCount()) { ok = false; break; }
            if (!ok) continue;
            ItemStack out = CompactMachinesCompat.target(r);
            if (out.isEmpty()) continue;
            for (ItemStack need : req) {
                int left = need.getCount();
                for (int i = 0; i < INPUTS && left > 0; i++)
                    if (same(inv.getStackInSlot(i), need)) left -= inv.extractItem(i, left, false).getCount();
            }
            crafting = out; total = Math.max(1, CompactMachinesCompat.ticks(r)); progress = 0; status = RUNNING;
            markDirty();
            return;
        }
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing f) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || cap == CapabilityEnergy.ENERGY || super.hasCapability(cap, f);
    }

    @Override
    public <T> T getCapability(Capability<T> cap, EnumFacing f) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(automation);
        if (cap == CapabilityEnergy.ENERGY) return CapabilityEnergy.ENERGY.cast(energy);
        return super.getCapability(cap, f);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound c) {
        super.writeToNBT(c);
        c.setTag("Items", inv.serializeNBT());
        c.setInteger("Energy", energy.getEnergyStored());
        c.setInteger("Progress", progress); c.setInteger("Total", total); c.setInteger("Status", status);
        if (!crafting.isEmpty()) c.setTag("Crafting", crafting.writeToNBT(new NBTTagCompound()));
        return c;
    }

    @Override
    public void readFromNBT(NBTTagCompound c) {
        super.readFromNBT(c);
        inv.deserializeNBT(c.getCompoundTag("Items"));
        energy.set(c.getInteger("Energy"));
        progress = c.getInteger("Progress"); total = c.getInteger("Total"); status = c.getInteger("Status");
        crafting = c.hasKey("Crafting") ? new ItemStack(c.getCompoundTag("Crafting")) : ItemStack.EMPTY;
    }

    public boolean usableBy(net.minecraft.entity.player.EntityPlayer p) {
        return world.getTileEntity(pos) == this && p.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
}
