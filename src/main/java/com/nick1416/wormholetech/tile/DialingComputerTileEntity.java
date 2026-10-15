package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.block.StargatePortalBlock;
import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.stargate.GateShape;
import com.nick1416.wormholetech.stargate.TitaniumFrame;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Opens a wormhole in an adjacent titanium gate frame. Needs RF, an Ancient Tablet (the address)
 * and an adjacent active Relativistic Computer. Conditions are re-checked every 20 ticks and on
 * neighbour changes; any failure collapses the wormhole and the player has to dial again.
 */
public class DialingComputerTileEntity extends TileEntity implements ITickable {

    public static final int STATUS_DISABLED = 0, STATUS_NO_ADDRESS = 1, STATUS_NEED_COMPUTER = 2,
            STATUS_NO_FRAME = 3, STATUS_NEED_POWER = 4, STATUS_READY = 5, STATUS_OPEN = 6, STATUS_NEED_CLOCK = 7,
            STATUS_NEED_MODULATOR = 8, STATUS_UNREACHABLE = 9, STATUS_HERE = 10;
    public static final String[] STATUS_KEYS = {
            "disabled", "no_address", "need_computer", "no_frame", "need_power", "ready", "open", "need_clock",
            "need_modulator", "unreachable", "here" };

    private final Energy energy = new Energy(ModConfig.dialingComputerBuffer, ModConfig.dialingComputerMaxReceive);
    private final ItemStackHandler inv = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { markDirty(); requestCheck(); }
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return accepts(stack); }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!accepts(stack)) return stack;
            return super.insertItem(slot, stack, simulate);
        }
    };

    private boolean open;
    private GateShape gate;
    private int status = STATUS_NO_ADDRESS;
    private int timer;
    private boolean checkRequested = true;

    public static boolean isAddress(ItemStack s) {
        return !s.isEmpty() && (s.getItem() == Registration.ANCIENT_TABLET || s.getItem() == Registration.DERELICT_COORDINATES
                || s.getItem() == Registration.INTERGALACTIC_COORDINATES);
    }

    /** Intergalactic Coordinates only work in a Gate Network Computer. */
    public boolean accepts(ItemStack s) {
        return isAddress(s) && (s.getItem() != Registration.INTERGALACTIC_COORDINATES || this instanceof GateNetworkComputerTileEntity);
    }

    /** Is there an address to dial (slot item, or a selected destination in subclasses)? */
    protected boolean hasAddress() { return accepts(inv.getStackInSlot(0)); }

    /** The address points to another galaxy (no destination exists there yet). */
    protected boolean intergalactic() { return inv.getStackInSlot(0).getItem() == Registration.INTERGALACTIC_COORDINATES; }

    /** A Modulator is needed only when the destination lies in another galaxy than this gate. */
    protected boolean needsModulator() {
        return ModDimensions.galaxy(destination()) != ModDimensions.galaxy(world.provider.getDimension());
    }

    /** Dimension the address in the slot leads to. */
    public int destination() {
        if (intergalactic()) return ModDimensions.talVoraId();
        return inv.getStackInSlot(0).getItem() == Registration.DERELICT_COORDINATES ? ModDimensions.derelictId() : ModDimensions.id();
    }

    protected boolean destinationAvailable() {
        if (intergalactic()) return ModDimensions.isTalVoraRegistered();
        return inv.getStackInSlot(0).getItem() == Registration.DERELICT_COORDINATES ? ModDimensions.isDerelictRegistered() : ModDimensions.isRegistered();
    }

    /** A Quantum Field Modulator touches one of the gate's titanium frame blocks. */
    public boolean modulatorAtGate(GateShape g) {
        if (g == null) return false;
        for (int y = -1; y <= g.height; y++)
            for (int x = -1; x <= g.width; x++) {
                if (!(y == -1 || y == g.height || x == -1 || x == g.width)) continue;
                BlockPos f = g.interior(x, y);
                for (EnumFacing d : EnumFacing.VALUES)
                    if (world.getBlockState(f.offset(d)).getBlock() == Registration.QUANTUM_FIELD_MODULATOR) return true;
            }
        return false;
    }

    public void requestCheck() { checkRequested = true; }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        if (open) {
            int cost = ModConfig.dialingComputerRfPerTick();
            if (energy.getEnergyStored() < cost) {
                close(true);
                status = STATUS_NEED_POWER;
                return;
            }
            energy.consume(cost);
            markDirty();
        }
        if (checkRequested || ++timer >= 20) {
            timer = 0;
            checkRequested = false;
            evaluate();
        }
    }

    /** Recompute status; collapses the wormhole if a condition failed. */
    public void evaluate() {
        GateShape found = null;
        int s;
        if (!TitaniumFrame.available() || (!ModDimensions.isRegistered() && !ModDimensions.isDerelictRegistered() && !ModDimensions.isTalVoraRegistered())) s = STATUS_DISABLED;
        else if (!hasAddress()) s = STATUS_NO_ADDRESS;
        else if (!destinationAvailable()) s = STATUS_DISABLED;
        else if (destination() == world.provider.getDimension()) s = STATUS_HERE;
        else if (!adjacentActiveComputer()) s = STATUS_NEED_COMPUTER;
        else if (!adjacentForwardClock()) s = STATUS_NEED_CLOCK;
        else if ((found = findGate()) == null) s = STATUS_NO_FRAME;
        else if (needsModulator() && !modulatorAtGate(found)) s = STATUS_NEED_MODULATOR;
        else if (!open && energy.getEnergyStored() < ModConfig.dialingComputerRfPerTick()) s = STATUS_NEED_POWER;
        else s = open ? STATUS_OPEN : STATUS_READY;

        if (open) {
            if (s != STATUS_OPEN || !sameGate(found, gate)) {
                close(true);
                if (s == STATUS_OPEN) s = STATUS_READY;
            } else {
                fillPortal(); // re-fill cells that were knocked out but are empty again
            }
        }
        status = s;
    }

    private static boolean sameGate(GateShape a, GateShape b) {
        return a != null && b != null && a.bottomLeft.equals(b.bottomLeft) && a.axis == b.axis
                && a.width == b.width && a.height == b.height;
    }

    /** Server: handle the GUI Dial/Close button. */
    public void toggle(EntityPlayer by) {
        if (open) {
            close(true);
            evaluate();
            return;
        }
        evaluate();
        if (status == STATUS_UNREACHABLE) {
            // the far galaxy has no gate to connect to yet
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 0.5F);
            if (by != null) by.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation("message.wormholetech.dialing.unreachable"), true);
            return;
        }
        if (status != STATUS_READY) return;
        gate = findGate();
        if (gate == null) return;
        open = true;
        fillPortal();
        status = STATUS_OPEN;
        BlockPos c = gate.interior(gate.width / 2, gate.height / 2);
        world.playSound(null, c, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 0.6F);
        world.playSound(null, c, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 0.5F, 1.4F);
        markDirty();
    }

    public void close(boolean sound) {
        if (!open) return;
        open = false;
        if (gate != null && world != null) {
            IBlockState air = net.minecraft.init.Blocks.AIR.getDefaultState();
            for (int y = 0; y < gate.height; y++)
                for (int x = 0; x < gate.width; x++) {
                    BlockPos p = gate.interior(x, y);
                    if (world.getBlockState(p).getBlock() == Registration.STARGATE_PORTAL) world.setBlockState(p, air, 2);
                }
            if (sound) world.playSound(null, gate.interior(gate.width / 2, gate.height / 2),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 0.6F);
        }
        gate = null;
        markDirty();
    }

    private void fillPortal() {
        if (gate == null) return;
        IBlockState ps = Registration.STARGATE_PORTAL.getDefaultState().withProperty(StargatePortalBlock.AXIS, gate.axis);
        for (int y = 0; y < gate.height; y++)
            for (int x = 0; x < gate.width; x++) {
                BlockPos p = gate.interior(x, y);
                if (world.isAirBlock(p)) world.setBlockState(p, ps, 2);
            }
    }

    private GateShape findGate() {
        for (EnumFacing f : EnumFacing.VALUES) {
            GateShape g = GateShape.findFromFrameBlock(world, pos.offset(f), TitaniumFrame::isTitanium);
            if (g != null) { com.nick1416.wormholetech.stargate.GateRegistryData.register(world, g); return g; }
        }
        return null;
    }

    /** An adjacent active Relativistic Computer with its quantum clock at +1. */
    private boolean adjacentForwardClock() {
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity te = world.getTileEntity(pos.offset(f));
            if (te instanceof RelativisticComputerTileEntity && ((RelativisticComputerTileEntity) te).isClockActiveForward()) return true;
        }
        return false;
    }

    private boolean adjacentActiveComputer() {
        for (EnumFacing f : EnumFacing.VALUES) {
            TileEntity te = world.getTileEntity(pos.offset(f));
            if (te instanceof RelativisticComputerTileEntity && ((RelativisticComputerTileEntity) te).isActive()) return true;
        }
        return false;
    }

    public boolean isOpen() { return open; }
    public int getStatus() { return status; }
    public int getEnergyStored() { return energy.getEnergyStored(); }
    public int getMaxEnergy() { return energy.getMaxEnergyStored(); }
    public IItemHandlerModifiable items() { return inv; }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Energy", energy.getEnergyStored());
        tag.setTag("Items", inv.serializeNBT());
        tag.setBoolean("Open", open);
        if (gate != null) tag.setTag("Gate", gate.write());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        if (tag.hasKey("Items")) inv.deserializeNBT(tag.getCompoundTag("Items"));
        open = tag.getBoolean("Open");
        gate = GateShape.read(tag.getCompoundTag("Gate"));
        if (gate == null) open = false;
    }

    @Override
    public boolean hasCapability(Capability<?> cap, EnumFacing facing) {
        return cap == CapabilityEnergy.ENERGY || cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || super.hasCapability(cap, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, EnumFacing facing) {
        if (cap == CapabilityEnergy.ENERGY) return (T) energy;
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) inv;
        return super.getCapability(cap, facing);
    }

    private static class Energy extends EnergyStorage {
        Energy(int cap, int maxReceive) { super(cap, maxReceive, 0); }
        void setEnergy(int v) { this.energy = Math.min(Math.max(0, v), getMaxEnergyStored()); }
        void consume(int a) { this.energy -= Math.min(a, this.energy); }
        @Override public boolean canReceive() { return true; }
        @Override public boolean canExtract() { return false; }
    }
}
