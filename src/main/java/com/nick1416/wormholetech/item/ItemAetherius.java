package com.nick1416.wormholetech.item;

import com.nick1416.wormholetech.ModConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Aetherius. Wears down while seated in a Relativistic Computer whose quantum clock runs at +1 and
 * recovers at the same rate at -1. The wear is shown like tool durability; fresh Aetherius has no
 * NBT so it still stacks.
 */
public class ItemAetherius extends Item {
    public static final String TAG_WEAR = "Wear";

    public static int maxWear() { return Math.max(1, ModConfig.aetheriusDecayTicks); }

    public static int getWear(ItemStack s) {
        return s.hasTagCompound() ? s.getTagCompound().getInteger(TAG_WEAR) : 0;
    }

    public static void setWear(ItemStack s, int wear) {
        wear = Math.max(0, Math.min(maxWear(), wear));
        if (wear == 0) {
            if (s.hasTagCompound()) {
                s.getTagCompound().removeTag(TAG_WEAR);
                if (s.getTagCompound().hasNoTags()) s.setTagCompound(null);
            }
            return;
        }
        if (!s.hasTagCompound()) s.setTagCompound(new NBTTagCompound());
        s.getTagCompound().setInteger(TAG_WEAR, wear);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) { return getWear(stack) > 0; }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) { return getWear(stack) / (double) maxWear(); }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        float f = (float) Math.max(0.0D, 1.0D - getDurabilityForDisplay(stack));
        return net.minecraft.util.math.MathHelper.hsvToRGB(f / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }
}
