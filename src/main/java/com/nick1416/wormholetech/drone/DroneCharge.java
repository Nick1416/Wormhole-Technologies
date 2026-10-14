package com.nick1416.wormholetech.drone;

import com.nick1416.wormholetech.ModConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Drone charge: full = {@link ModConfig#droneChargeTicks} ticks of work away from a Quantum Field
 * Modulator. Near one it refills to full in 200 ticks (10 s). Items without a Charge tag are full.
 */
public final class DroneCharge {
    private DroneCharge() {}
    public static final String TAG = "Charge";

    public static int max() { return Math.max(1, ModConfig.droneChargeTicks); }
    public static int rechargePerTick() { return Math.max(1, (max() + 199) / 200); }
    public static int percent(int c) { return (int) Math.round(100.0 * Math.max(0, Math.min(max(), c)) / max()); }

    public static int of(ItemStack s) {
        NBTTagCompound t = s.getTagCompound();
        return t != null && t.hasKey(TAG) ? Math.max(0, Math.min(max(), t.getInteger(TAG))) : max();
    }

    public static ItemStack with(ItemStack s, int c) {
        if (c >= max()) { if (s.hasTagCompound()) { s.getTagCompound().removeTag(TAG); if (s.getTagCompound().hasNoTags()) s.setTagCompound(null); } }
        else { if (!s.hasTagCompound()) s.setTagCompound(new NBTTagCompound()); s.getTagCompound().setInteger(TAG, Math.max(0, c)); }
        return s;
    }

    /** One tick: refill near a modulator, otherwise drain. Returns the new charge. */
    public static int tick(int c, boolean nearModulator) {
        return nearModulator ? Math.min(max(), c + rechargePerTick()) : Math.max(0, c - 1);
    }
}
