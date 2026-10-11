package com.nick1416.wormholetech.item;

import com.nick1416.wormholetech.util.WormholeFuel;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/** Partially decayed blocks (Decay NBT) do not stack and show their decay as a bar. */
public class ItemRefinedNaquadahBlock extends ItemBlock {
    public ItemRefinedNaquadahBlock(Block block) {
        super(block);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return WormholeFuel.isPartial(stack) ? 1 : super.getItemStackLimit(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return WormholeFuel.isPartial(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return Math.min(1.0, WormholeFuel.decayOf(stack) / (double) WormholeFuel.lifetime());
    }
}
