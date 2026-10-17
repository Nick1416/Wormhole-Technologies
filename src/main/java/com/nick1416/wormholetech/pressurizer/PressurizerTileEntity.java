package com.nick1416.wormholetech.pressurizer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Hydrothermal Pressurizer: no RF. Each emerald takes 1 aluminum + 1 chromium + 1 silicon, 1 coal and
 * 1000 mB water (all used when it starts) and forms in 2 minutes.
 * Slots: 0 water container, 1 coal, 2 aluminum, 3 chromium, 4 silicon, 5 output.
 */
public class PressurizerTileEntity extends TileEntity implements ITickable {
    public static final int WATER = 0, COAL = 1, MIN0 = 2, OUT = 5;
    public static final int STATUS_IDLE = 0, STATUS_FORMING = 1, STATUS_NEED_WATER = 2, STATUS_NEED_COAL = 3, STATUS_OUTPUT_FULL = 4;

    private final FluidTank tank = new FluidTank(PressurizerRecipe.TANK) {
        @Override public boolean canFillFluidType(FluidStack f) { return f != null && f.getFluid() == FluidRegistry.WATER; }
        @Override protected void onContentsChanged() { markDirty(); }
    };

    private final ItemStackHandler inv = new ItemStackHandler(6) {
        @Override protected void onContentsChanged(int slot) { markDirty(); }
        @Override public boolean isItemValid(int slot, ItemStack s) { return canPlace(slot, s); }
        @Override public ItemStack insertItem(int slot, ItemStack s, boolean sim) {
            return canPlace(slot, s) ? super.insertItem(slot, s, sim) : s;
        }
    };

    private int cook, status;
    { tank.setCanDrain(false); }

    public static boolean valid(int slot, ItemStack s) {
        if (s.isEmpty()) return false;
        if (slot == WATER) {
            FluidStack f = FluidUtil.getFluidContained(s);
            return f != null && f.getFluid() == FluidRegistry.WATER;
        }
        if (slot == COAL) return PressurizerRecipe.isCoal(s);
        if (slot >= MIN0 && slot < OUT) return mineralType(s) >= 0;
        return false;
    }

    /** 0 aluminum, 1 chromium, 2 silicon, -1 none. */
    public static int mineralType(ItemStack s) {
        for (int t = 0; t < PressurizerRecipe.MINERALS.length; t++)
            if (PressurizerRecipe.matches(s, PressurizerRecipe.MINERALS[t])) return t;
        return -1;
    }

    /** Mineral slots take any mineral, in any order, but never a type another mineral slot already holds. */
    public boolean canPlace(int slot, ItemStack s) {
        if (!valid(slot, s)) return false;
        if (slot < MIN0 || slot >= OUT) return true;
        int t = mineralType(s);
        for (int i = MIN0; i < OUT; i++)
            if (i != slot && !inv.getStackInSlot(i).isEmpty() && mineralType(inv.getStackInSlot(i)) == t) return false;
        return true;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        drainWaterSlot();
        ItemStack out = inv.getStackInSlot(OUT);
        boolean room = out.isEmpty() || (out.getItem() == Items.EMERALD && out.getCount() < out.getMaxStackSize());
        int types = 0;
        for (int k = MIN0; k < OUT; k++) { int t = mineralType(inv.getStackInSlot(k)); if (t >= 0) types |= 1 << t; }
        boolean ready = types == 7
                && tank.getFluidAmount() >= PressurizerRecipe.WATER_PER_EMERALD
                && inv.getStackInSlot(COAL).getCount() >= PressurizerRecipe.COAL_PER_EMERALD;
        // Everything must stay in place while forming; taking anything out resets progress.
        if (!ready) {
            if (cook != 0) { cook = 0; markDirty(); }
            status = STATUS_IDLE;
            return;
        }
        if (cook < PressurizerRecipe.TICKS) { cook++; status = STATUS_FORMING; markDirty(); return; }
        if (!room) { status = STATUS_OUTPUT_FULL; return; }
        // Completed: only now are minerals, coal and water used up.
        for (int k = MIN0; k < OUT; k++) inv.extractItem(k, 1, false);
        inv.extractItem(COAL, PressurizerRecipe.COAL_PER_EMERALD, false);
        tank.drainInternal(PressurizerRecipe.WATER_PER_EMERALD, true);
        if (out.isEmpty()) inv.setStackInSlot(OUT, new ItemStack(Items.EMERALD));
        else { out.grow(1); inv.setStackInSlot(OUT, out); }
        cook = 0;
        status = STATUS_IDLE;
        markDirty();
    }

    /** Empties a water bucket (or other water container) in the water slot into the tank, leaving the empty container. */
    private void drainWaterSlot() {
        ItemStack s = inv.getStackInSlot(WATER);
        if (s.getCount() != 1 || !valid(WATER, s)) return;
        if (tank.getCapacity() - tank.getFluidAmount() < Fluid.BUCKET_VOLUME) return;
        tank.setCanFill(true);
        FluidActionResult r = FluidUtil.tryEmptyContainer(s, tank, Integer.MAX_VALUE, null, true);
        if (r.isSuccess()) inv.setStackInSlot(WATER, r.getResult());
    }

    public int getWater() { return tank.getFluidAmount(); }
    public int getCook() { return cook; }
    public int getStatus() { return status; }
    public ItemStackHandler items() { return inv; }

    public void dropContents(World world, BlockPos pos) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (!s.isEmpty()) net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), s);
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("Items", inv.serializeNBT());
        tag.setTag("Tank", tank.writeToNBT(new NBTTagCompound()));
        tag.setInteger("Cook", cook);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inv.deserializeNBT(tag.getCompoundTag("Items"));
        tank.readFromNBT(tag.getCompoundTag("Tank"));
        cook = tag.getInteger("Cook");
    }

    /** Sides and top: insert inputs. Bottom: take emeralds and empty buckets. */
    private final IItemHandler sides = new Sided(false), bottom = new Sided(true);

    private final class Sided implements IItemHandler {
        private final boolean down;
        Sided(boolean down) { this.down = down; }
        @Override public int getSlots() { return inv.getSlots(); }
        @Override public ItemStack getStackInSlot(int slot) { return inv.getStackInSlot(slot); }
        @Override public ItemStack insertItem(int slot, ItemStack s, boolean sim) { return down || slot == OUT ? s : inv.insertItem(slot, s, sim); }
        @Override public ItemStack extractItem(int slot, int amount, boolean sim) {
            if (!down) return ItemStack.EMPTY;
            if (slot == OUT) return inv.extractItem(slot, amount, sim);
            if (slot == WATER && !valid(WATER, inv.getStackInSlot(WATER))) return inv.extractItem(slot, amount, sim);
            return ItemStack.EMPTY;
        }
        @Override public int getSlotLimit(int slot) { return inv.getSlotLimit(slot); }
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing f) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(cap, f);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing f) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) (f == null ? inv : f == EnumFacing.DOWN ? bottom : sides);
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return (T) tank;
        return super.getCapability(cap, f);
    }
}
