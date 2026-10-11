package com.nick1416.wormholetech.util;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Block of Refined Naquadah as wormhole fuel. A block that was taken out of a machine part-way
 * through decaying carries the elapsed ticks in its NBT ({@link #DECAY}); those never stack.
 */
public final class WormholeFuel {
    public static final String DECAY = "Decay";

    private WormholeFuel() {}

    public static int lifetime() {
        return Math.max(1, ModConfig.refinedNaquadahBlockDecayTicks);
    }

    public static boolean isFuel(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Registration.REFINED_NAQUADAH_BLOCK);
    }

    public static boolean isPartial(ItemStack stack) {
        return isFuel(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(DECAY);
    }

    /** Elapsed decay ticks stored on the stack (0 for a fresh block). */
    public static int decayOf(ItemStack stack) {
        return isPartial(stack) ? Math.max(0, stack.getTagCompound().getInteger(DECAY)) : 0;
    }

    /** One block carrying {@code decay} elapsed ticks (a fresh block when decay is 0). */
    public static ItemStack block(int decay) {
        ItemStack s = new ItemStack(Registration.REFINED_NAQUADAH_BLOCK);
        if (decay > 0) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger(DECAY, decay);
            s.setTagCompound(tag);
        }
        return s;
    }

    /** Same block without the decay tag (count kept). */
    public static ItemStack fresh(ItemStack stack) {
        ItemStack s = new ItemStack(stack.getItem(), stack.getCount(), stack.getMetadata());
        return s;
    }

    /** "mm:ss" at 20 TPS. */
    public static String formatTicks(int ticks) {
        int sec = Math.max(0, (ticks + 19) / 20);
        return String.format("%02d:%02d", sec / 60, sec % 60);
    }
}
