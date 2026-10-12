package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.duplication.DuplicationRules;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WormholeDuplicatorTileEntity extends LinkedMachineTileEntity {

    // 0: diamond nugget, 1: template, 2: output, 3: Block of Refined Naquadah, 4: Unstable Block of Naquadah
    public static final int SLOT_NUGGET = 0, SLOT_TEMPLATE = 1, SLOT_OUTPUT = 2, SLOT_FUEL = 3, SLOT_UNSTABLE = 4;

    public WormholeDuplicatorTileEntity() {
        super(5, SLOT_FUEL, 64, SLOT_UNSTABLE);
    }

    @Override
    protected int[] outputSlots() { return new int[] { SLOT_OUTPUT, SLOT_UNSTABLE }; }

    @Override
    protected boolean isValid(int slot, ItemStack stack) {
        if (slot == SLOT_NUGGET) return ItemStack.areItemsEqual(stack, WormholeTech.DIAMOND_NUGGET);
        if (slot == SLOT_TEMPLATE) return isAllowedTemplate(stack);
        return false; // outputs
    }

    private boolean isAllowedTemplate(ItemStack s) {
        Item it = s.getItem();
        if (it == net.minecraft.init.Items.DIAMOND) return false; // no duping diamonds with diamonds :)
        if (DuplicationRules.isBlacklisted(s)) return false;     // this mod's items etc.
        if (s.hasTagCompound()) return false;                     // avoid NBT items for now
        return true;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        if (!com.nick1416.wormholetech.ModConfig.isCC()) return; // inert in normal mode
        if (link.tick() != WormholeLink.State.RUNNING) return; // only works while connected

        ItemStack diamonds = inv.getStackInSlot(SLOT_NUGGET);
        ItemStack template = inv.getStackInSlot(SLOT_TEMPLATE);
        ItemStack output   = inv.getStackInSlot(SLOT_OUTPUT);
        if (diamonds.isEmpty() || template.isEmpty() || !isAllowedTemplate(template)) return;

        ItemStack product = template.copy();
        product.setCount(2);
        if (output.isEmpty()) {
            if (product.getCount() > product.getMaxStackSize()) return;
            diamonds.shrink(1);
            template.shrink(1);
            inv.setStackInSlot(SLOT_OUTPUT, product);
        } else if (ItemStack.areItemsEqual(output, product) && ItemStack.areItemStackTagsEqual(output, product)
                && output.getMaxStackSize() - output.getCount() >= 2) {
            diamonds.shrink(1);
            template.shrink(1);
            output.grow(2);
        } else {
            return;
        }
        markDirty();
    }

    /** Duplicators only exist in mode=cc. */
    @Override protected boolean isModeDisabled() { return !com.nick1416.wormholetech.ModConfig.isCC(); }
}
