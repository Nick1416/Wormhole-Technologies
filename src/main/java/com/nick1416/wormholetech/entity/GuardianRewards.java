package com.nick1416.wormholetech.entity;

import net.minecraft.entity.player.EntityPlayer;

/** Rewards for defeating a temple guardian. */
public final class GuardianRewards {
    private GuardianRewards() {}

    /**
     * Called on the server when a player defeats an Emerald Sentinel (not the Giant one). The loot
     * table and the "guardian_sentinel" advancement cover the rewards; the Ancient Vessel
     * Coordinates are guaranteed in the guardian temple chest instead of dropped here.
     */
    public static void onSentinelDefeated(EntityPlayer player, EntityEmeraldSentinel sentinel) {
    }
}
