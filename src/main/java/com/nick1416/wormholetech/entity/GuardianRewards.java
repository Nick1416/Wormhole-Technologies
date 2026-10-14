package com.nick1416.wormholetech.entity;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** Rewards for defeating a temple guardian. */
public final class GuardianRewards {
    private GuardianRewards() {}

    /**
     * Called on the server when a player defeats an Emerald Sentinel (not the Giant one). The loot
     * table and the "guardian_sentinel" advancement cover the normal rewards; this adds the gate
     * address of the derelict vessel (Derelict Coordinates), dropped where the Sentinel fell.
     * Guardian temple chests can hold one too.
     */
    public static void onSentinelDefeated(EntityPlayer player, EntityEmeraldSentinel sentinel) {
        sentinel.entityDropItem(new ItemStack(Registration.DERELICT_COORDINATES), 0.5F);
    }
}
