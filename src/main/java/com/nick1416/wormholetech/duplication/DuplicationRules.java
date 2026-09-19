package com.nick1416.wormholetech.duplication;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Rules for what the Wormhole Duplicator may copy.
 * All items from this mod's namespace are disallowed (progression / machines / materials).
 * Items with NBT are rejected separately in the tile entity.
 */
public final class DuplicationRules {
    private DuplicationRules() {}

    public static boolean isBlacklisted(ItemStack stack) {
        if (stack.isEmpty()) return true;
        Item item = stack.getItem();
        ResourceLocation id = item.getRegistryName();
        if (id == null) return true;

        // Deny every item registered under this mod (strictest / safest for progression)
        if (WormholeTech.MODID.equals(id.getResourceDomain())) {
            return true;
        }

        return false;
    }
}
