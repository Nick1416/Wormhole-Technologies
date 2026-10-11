package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.util.WormholeFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Wormhole connection shared by the duplicators and the energy converter.
 *
 * The fuel slot holds Blocks of Refined Naquadah. Once a connection is established the first
 * block decays over {@link WormholeFuel#lifetime()} game ticks; then it becomes an Unstable Block
 * of Naquadah in the output slot and the next block starts. The connection ends when the last
 * block is used up or when the decaying block is taken out (it leaves with its progress in NBT).
 * With a full output slot the finished block waits and the machine pauses until there is room.
 * Machines without an output slot (Unstable Duplicator) get {@link State#EXPIRED} instead.
 */
public final class WormholeLink {

    public enum State { IDLE, RUNNING, PAUSED, EXPIRED }

    /** Status codes synced to the GUI. */
    public static final int STATUS_NONE = 0, STATUS_CONNECTED = 1, STATUS_PAUSED = 2;

    private final LinkedInventory inv;
    private final int fuelSlot;
    private final int outSlot; // -1: no output slot
    private boolean connected;
    private boolean paused;
    private int decay; // elapsed ticks of the current (first) block in the fuel slot

    public WormholeLink(LinkedInventory inv, int fuelSlot, int outSlot) {
        this.inv = inv;
        this.fuelSlot = fuelSlot;
        this.outSlot = outSlot;
        inv.attach(this, fuelSlot);
    }

    public boolean isConnected() { return connected; }
    public int getDecay() { return decay; }

    public int getStatus() {
        if (!connected) return STATUS_NONE;
        return paused ? STATUS_PAUSED : STATUS_CONNECTED;
    }

    /** Ticks left on the current block (full lifetime for a fresh one), 0 when the slot is empty. */
    public int getRemaining() {
        if (inv.getStackInSlot(fuelSlot).isEmpty()) return 0;
        return Math.max(0, WormholeFuel.lifetime() - decay);
    }

    public boolean canEstablish() {
        return !connected && WormholeFuel.isFuel(inv.getStackInSlot(fuelSlot));
    }

    /** Server side, from the GUI button. */
    public boolean establish() {
        if (!canEstablish()) return false;
        connected = true;
        paused = false;
        inv.changed();
        return true;
    }

    /** Called once per server tick. RUNNING means the machine may work this tick. */
    public State tick() {
        if (!connected) return State.IDLE;
        ItemStack fuel = inv.getStackInSlot(fuelSlot);
        if (!WormholeFuel.isFuel(fuel)) {
            disconnect();
            return State.IDLE;
        }
        int life = WormholeFuel.lifetime();
        if (decay < life) {
            decay++;
            if (decay % 20 == 0) inv.changed();
        }
        if (decay < life) {
            paused = false;
            return State.RUNNING;
        }
        if (outSlot < 0) return State.EXPIRED;

        ItemStack out = inv.getStackInSlot(outSlot);
        Item unstable = Item.getItemFromBlock(Registration.UNSTABLE_NAQUADAH_BLOCK);
        if (!out.isEmpty() && (out.getItem() != unstable || out.getCount() >= out.getMaxStackSize())) {
            paused = true;
            return State.PAUSED;
        }
        if (out.isEmpty()) inv.setOutput(outSlot, new ItemStack(unstable));
        else inv.setOutput(outSlot, new ItemStack(unstable, out.getCount() + 1));
        paused = false;
        decay = 0;
        inv.consumeFuel(); // disconnects via onFuelEmptied when it was the last block
        return connected ? State.RUNNING : State.IDLE;
    }

    void disconnect() {
        connected = false;
        paused = false;
        decay = 0;
    }

    /** The fuel slot became empty by any route. */
    void onFuelEmptied() {
        disconnect();
    }

    /** The decaying block was taken out: it leaves with its progress; the connection ends. */
    int takeCurrent() {
        int d = decay;
        disconnect();
        return d;
    }

    /** A partially decayed block went into the empty fuel slot. */
    void adopt(int d) {
        decay = Math.max(0, Math.min(d, WormholeFuel.lifetime()));
        connected = false;
        paused = false;
    }

    public NBTTagCompound write() {
        NBTTagCompound t = new NBTTagCompound();
        t.setBoolean("Connected", connected);
        t.setInteger("Decay", decay);
        return t;
    }

    public void read(NBTTagCompound t) {
        connected = t.getBoolean("Connected");
        decay = Math.max(0, t.getInteger("Decay"));
        paused = false;
    }
}
