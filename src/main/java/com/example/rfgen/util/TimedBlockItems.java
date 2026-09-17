package com.example.rfgen.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/** NBT helpers so lifetime generators keep remaining runtime when broken/placed. */
public final class TimedBlockItems {
    public static final String AGE = "Age";
    public static final String ENERGY = "Energy";

    private TimedBlockItems() {}

    public static void writeAgeEnergy(ItemStack stack, int age, int energy) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setInteger(AGE, Math.max(0, age));
        tag.setInteger(ENERGY, Math.max(0, energy));
        stack.setTagCompound(tag);
    }

    public static int readAge(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) return 0;
        return Math.max(0, stack.getTagCompound().getInteger(AGE));
    }

    public static int readEnergy(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) return 0;
        return Math.max(0, stack.getTagCompound().getInteger(ENERGY));
    }

    @SideOnly(Side.CLIENT)
    public static void addRemainingTooltip(List<String> tooltip, ItemStack stack, int lifetimeTicks) {
        if (stack.isEmpty() || !stack.hasTagCompound() || !stack.getTagCompound().hasKey(AGE)) return;
        int age = readAge(stack);
        int remaining = Math.max(0, lifetimeTicks - age);
        int totalSec = remaining / 20;
        int min = totalSec / 60;
        int sec = totalSec % 60;
        tooltip.add(TextFormatting.GRAY + "Remaining: " + min + "m " + sec + "s");
    }
}
