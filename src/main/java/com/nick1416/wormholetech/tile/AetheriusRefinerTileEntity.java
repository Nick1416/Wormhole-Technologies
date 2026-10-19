package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Furnace-like long refine: Compressed Naquadah → Aetherius in 15 minutes (18000 ticks, configurable).
 * A Hydrothermal Fluid tank (bucket slot 2, or pipes) feeds the stone → Nether Quartz recipe.
 */
public class AetheriusRefinerTileEntity extends TileEntity implements ITickable {

    private final RefEnergy energy = new RefEnergy(
            ModConfig.highEnergyRefinerBuffer, ModConfig.highEnergyRefinerMaxReceive);
    public static final int TANK_CAPACITY = 8000;
    private final net.minecraftforge.fluids.FluidTank tank = new net.minecraftforge.fluids.FluidTank(TANK_CAPACITY) {
        @Override public boolean canFillFluidType(net.minecraftforge.fluids.FluidStack f) { return com.nick1416.wormholetech.fluid.HydrothermalFluid.is(f); }
        @Override protected void onContentsChanged() { markDirty(); }
    };
    { tank.setCanDrain(false); }

    // 0: input (compressed naquadah), 1: output (aetherius), 2: fluid bucket
    public static final int SLOT_BUCKET = 2;
    private final ItemStackHandler inv = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (slot == 0) {
                return RefinerRecipes.find(stack) != null;
            }
            if (slot == SLOT_BUCKET) return isFluidContainer(stack);
            return false; // output extract-only
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack)) return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public int getSlotLimit(int slot) { return slot == SLOT_BUCKET ? 1 : 64; }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // automation may only take the output and emptied containers
            if (slot == 0) return ItemStack.EMPTY;
            if (slot == SLOT_BUCKET && isFluidContainer(getStackInSlot(slot))) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }
    };
    /** Unrestricted view for the GUI and drops. */
    private final net.minecraftforge.items.IItemHandlerModifiable guiInv = new net.minecraftforge.items.wrapper.RangedWrapper(inv, 0, 3) {
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack s = inv.getStackInSlot(slot);
            if (s.isEmpty() || amount <= 0) return ItemStack.EMPTY;
            int n = Math.min(amount, s.getCount());
            ItemStack out = s.copy(); out.setCount(n);
            if (!simulate) { ItemStack left = s.copy(); left.shrink(n); inv.setStackInSlot(slot, left.isEmpty() ? ItemStack.EMPTY : left); }
            return out;
        }
    };
    private int cook;

    public static boolean isFluidContainer(ItemStack s) {
        return !s.isEmpty() && com.nick1416.wormholetech.fluid.HydrothermalFluid.is(net.minecraftforge.fluids.FluidUtil.getFluidContained(s));
    }

    public int getFluidAmount() { return tank.getFluidAmount(); }

    /** Empties a Hydrothermal Fluid bucket (or other container) in the bucket slot into the tank. */
    private void drainBucketSlot() {
        ItemStack s = inv.getStackInSlot(SLOT_BUCKET);
        if (s.getCount() != 1 || !isFluidContainer(s)) return;
        if (tank.getCapacity() - tank.getFluidAmount() < net.minecraftforge.fluids.Fluid.BUCKET_VOLUME) return;
        net.minecraftforge.fluids.FluidActionResult r = net.minecraftforge.fluids.FluidUtil.tryEmptyContainer(s, tank, Integer.MAX_VALUE, null, true);
        if (r.isSuccess()) inv.setStackInSlot(SLOT_BUCKET, r.getResult());
    }

    public static final int STATUS_IDLE = 0;        // no input
    public static final int STATUS_REFINING = 1;
    public static final int STATUS_NEED_RF = 2;
    public static final int STATUS_OUTPUT_FULL = 3;
    public static final int STATUS_NEED_FLUID = 4;

    private int status = STATUS_IDLE;
    /** Whether the machine is refining; synced to clients for the block's particles. */
    private boolean active;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        drainBucketSlot();
        updateStatus();

        ItemStack input = inv.getStackInSlot(0);
        ItemStack output = inv.getStackInSlot(1);

        RefinerRecipes.Recipe recipe = RefinerRecipes.find(input);
        boolean canCook = recipe != null && roomFor(recipe, output)
                && tank.getFluidAmount() >= recipe.fluidMb
                && energy.getEnergyStored() >= ModConfig.highEnergyRefinerRfPerTick;

        if (canCook) {
            energy.consume(ModConfig.highEnergyRefinerRfPerTick);
            cook++;
            if (cook >= recipe.ticks()) {
                input.shrink(1);
                if (input.getCount() <= 0) inv.setStackInSlot(0, ItemStack.EMPTY);
                if (recipe.fluidMb > 0) tank.drainInternal(recipe.fluidMb, true);
                if (output.isEmpty()) inv.setStackInSlot(1, new ItemStack(recipe.output, recipe.outputCount));
                else {
                    output.grow(recipe.outputCount);
                    inv.setStackInSlot(1, output);
                }
                cook = 0;
            }
            markDirty();
        } else if (cook > 0 && (input.isEmpty() || energy.getEnergyStored() < ModConfig.highEnergyRefinerRfPerTick)) {
            // keep progress when unpowered / empty
            markDirty();
        }
    }

    private void updateStatus() {
        ItemStack input = inv.getStackInSlot(0);
        ItemStack output = inv.getStackInSlot(1);
        int next;
        RefinerRecipes.Recipe recipe = RefinerRecipes.find(input);
        if (recipe == null) {
            next = STATUS_IDLE;
        } else if (!roomFor(recipe, output)) {
            next = STATUS_OUTPUT_FULL;
        } else if (tank.getFluidAmount() < recipe.fluidMb) {
            next = STATUS_NEED_FLUID;
        } else if (energy.getEnergyStored() < ModConfig.highEnergyRefinerRfPerTick) {
            next = STATUS_NEED_RF;
        } else {
            next = STATUS_REFINING;
        }
        status = next;
        boolean nowActive = next == STATUS_REFINING;
        if (nowActive != active) {
            active = nowActive;
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    private static boolean roomFor(RefinerRecipes.Recipe r, ItemStack out) {
        return out.isEmpty() || (out.getItem() == r.output && out.getCount() + r.outputCount <= out.getMaxStackSize());
    }

    /** One of the STATUS_* constants (server side). */
    public int getStatus() { return status; }

    /** Client and server: true while refining (client value arrives via the update packet). */
    public boolean isActive() { return active; }

    /** Ticks for the recipe currently loaded (the Aetherius time when empty). */
    public int getCookTimeTotal() {
        RefinerRecipes.Recipe r = RefinerRecipes.find(inv.getStackInSlot(0));
        return r != null ? r.ticks() : ModConfig.highEnergyRefinerCookTimeTicks;
    }

    public int getCookTime() { return cook; }

    public float getCookProgress() {
        int cookTime = getCookTimeTotal();
        if (cookTime <= 0) return 0f;
        return Math.min(1f, cook / (float) cookTime);
    }

    public int getProgressPercent() {
        int cookTime = getCookTimeTotal();
        if (cookTime <= 0) return 0;
        return Math.min(100, (cook * 100) / cookTime);
    }

    public int getCook() { return cook; }

    public int getEnergyStored() { return energy.getEnergyStored(); }

    public IItemHandlerModifiable items() { return guiInv; }

    public void dropContents(World world, BlockPos pos) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                net.minecraft.inventory.InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                inv.setStackInSlot(i, ItemStack.EMPTY);
            }
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
        tag.setInteger("Cook", cook);
        tag.setTag("Items", inv.serializeNBT());
        tag.setTag("Tank", tank.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        cook = tag.getInteger("Cook");
        tank.setFluid(null);
        if (tag.hasKey("Tank")) tank.readFromNBT(tag.getCompoundTag("Tank"));
        if (tag.hasKey("Items")) {
            // older saves had 2 slots; ItemStackHandler would shrink to the saved size, so copy across
            ItemStackHandler old = new ItemStackHandler();
            old.deserializeNBT(tag.getCompoundTag("Items"));
            for (int i = 0; i < inv.getSlots(); i++) inv.setStackInSlot(i, i < old.getSlots() ? old.getStackInSlot(i) : ItemStack.EMPTY);
        } else {
            // Migrate PR #19 Input/Output compound tags
            if (tag.hasKey("Input")) inv.setStackInSlot(0, new ItemStack(tag.getCompoundTag("Input")));
            if (tag.hasKey("Output")) inv.setStackInSlot(1, new ItemStack(tag.getCompoundTag("Output")));
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        tag.setBoolean("Active", active);
        return tag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        active = tag.getBoolean("Active");
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("Active", active);
        return new SPacketUpdateTileEntity(pos, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        active = pkt.getNbtCompound().getBoolean("Active");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY
                || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || capability == net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return (T) energy;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) inv;
        if (capability == net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return (T) tank;
        return super.getCapability(capability, facing);
    }

    private static class RefEnergy extends EnergyStorage {
        public RefEnergy(int cap, int maxReceive) { super(cap, maxReceive, 0); }
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
