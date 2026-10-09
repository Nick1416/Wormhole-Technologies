package com.nick1416.wormholetech.gui;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Container that syncs a fixed set of full 32-bit ints to the client.
 *
 * Vanilla window properties ({@code sendWindowProperty}/{@code updateProgressBar}) travel as
 * 16-bit shorts, so each int is sent as two properties (low and high half). The server keeps
 * its own "last sent" copy, re-sends both halves whenever a value changes, and also re-sends
 * everything every {@link #RESYNC_INTERVAL} ticks. The periodic resync covers the case where
 * the first packets reach the client before its container exists: previously the GUI then
 * showed 0 RF until the value happened to change or the GUI was reopened.
 */
public abstract class SyncedIntsContainer extends Container {

    private static final int RESYNC_INTERVAL = 20;

    private final int[] lastSent;
    private final int[] client;
    private int ticksSinceResync;
    private boolean everSent;

    protected SyncedIntsContainer(int count) {
        this.lastSent = new int[count];
        this.client = new int[count];
    }

    /** Server side: current values, in a fixed order, length == count. */
    protected abstract int[] readServerValues();

    /** Client side: last value received for index {@code i}. */
    protected int synced(int i) { return client[i]; }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        int[] now = readServerValues();
        for (int i = 0; i < now.length; i++) send(listener, i, now[i]);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int[] now = readServerValues();
        boolean resync = !everSent || ++ticksSinceResync >= RESYNC_INTERVAL;
        if (resync) ticksSinceResync = 0;
        for (int i = 0; i < now.length; i++) {
            if (resync || now[i] != lastSent[i]) {
                for (IContainerListener l : listeners) send(l, i, now[i]);
                lastSent[i] = now[i];
            }
        }
        everSent = true;
    }

    private void send(IContainerListener l, int index, int value) {
        l.sendWindowProperty(this, index * 2, value & 0xFFFF);
        l.sendWindowProperty(this, index * 2 + 1, (value >>> 16) & 0xFFFF);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        int index = id >> 1;
        if (index < 0 || index >= client.length) return;
        if ((id & 1) == 0) client[index] = (client[index] & 0xFFFF0000) | (data & 0xFFFF);
        else client[index] = (client[index] & 0x0000FFFF) | ((data & 0xFFFF) << 16);
    }
}
