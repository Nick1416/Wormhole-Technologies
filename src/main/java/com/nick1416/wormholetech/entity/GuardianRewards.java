package com.nick1416.wormholetech.entity;

import net.minecraft.entity.player.EntityPlayer;

/** Rewards for defeating a temple guardian. */
public final class GuardianRewards {
    private GuardianRewards() {}

    /**
     * Called on the server when a player defeats an Emerald Sentinel. The loot table and the
     * "guardian_sentinel" advancement already cover the normal rewards.
     */
    public static void onSentinelDefeated(EntityPlayer player, EntityEmeraldSentinel sentinel) {
        // TODO(gate-address): grant a gate-address reward here (e.g. an inscribed tablet naming another
        // world's address). Gate addresses are not implemented yet.
    }
}
