package com.nick1416.wormholetech.drone;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/** Xenolith Chip, one meta per {@link DroneProgram}. Combine with an Inactive Xenolith Drone. */
public class ItemXenolithChip extends Item {
    public ItemXenolithChip() { setHasSubtypes(true); setMaxDamage(0); }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) for (DroneProgram p : DroneProgram.VALUES) items.add(new ItemStack(this, 1, p.ordinal()));
    }

    @Override
    public String getItemStackDisplayName(ItemStack s) {
        return net.minecraft.util.text.translation.I18n.translateToLocalFormatted("item.wormholetech.xenolith_chip.name", DroneNames.program(DroneProgram.byMeta(s.getMetadata())));
    }
}
