package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.duplication.DuplicationRules;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Single-use duplicator: fuelled by a full diamond, with a single Block of Refined Naquadah slot and
 * no Unstable output. Once connected it performs exactly ONE duplication, then immediately drops its
 * contents (output included) and collapses into an Unstable Block of Naquadah; the Refined Naquadah
 * block is consumed. It also collapses if that block finishes decaying before any duplication.
 */
public class UnstableWormholeDuplicatorTileEntity extends LinkedMachineTileEntity {

    // 0: diamond, 1: template, 2: output, 3: Block of Refined Naquadah (max 1)
    public static final int SLOT_DIAMOND = 0, SLOT_TEMPLATE = 1, SLOT_OUTPUT = 2, SLOT_FUEL = 3;
    private static final int COST_DIAMONDS = 1;

    public UnstableWormholeDuplicatorTileEntity() {
        super(4, SLOT_FUEL, 1, -1);
    }

    @Override
    protected int[] outputSlots() { return new int[] { SLOT_OUTPUT }; }

    @Override
    protected boolean isValid(int slot, ItemStack s) {
        if (slot == SLOT_DIAMOND) return s.getItem() == net.minecraft.init.Items.DIAMOND;
        if (slot == SLOT_TEMPLATE) return isAllowedTemplate(s);
        return false;
    }

    private boolean isAllowedTemplate(ItemStack s) {
        Item it = s.getItem();
        if (it == net.minecraft.init.Items.DIAMOND) return false;
        if (DuplicationRules.isBlacklisted(s)) return false;
        if (s.hasTagCompound()) return false;
        return true;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;
        if (!com.nick1416.wormholetech.ModConfig.isCC()) return; // inert in normal mode
        WormholeLink.State state = link.tick();
        if (state == WormholeLink.State.EXPIRED) {
            collapse();
            return;
        }
        if (state != WormholeLink.State.RUNNING) return;

        ItemStack diamonds = inv.getStackInSlot(SLOT_DIAMOND);
        ItemStack template = inv.getStackInSlot(SLOT_TEMPLATE);
        ItemStack output   = inv.getStackInSlot(SLOT_OUTPUT);
        if (diamonds.getCount() < COST_DIAMONDS) return;
        if (template.isEmpty() || !isAllowedTemplate(template)) return;

        ItemStack product = template.copy();
        product.setCount(2);
        boolean canOutput = output.isEmpty()
                || (ItemStack.areItemsEqual(output, product)
                    && ItemStack.areItemStackTagsEqual(output, product)
                    && output.getCount() <= output.getMaxStackSize() - 2);
        if (!canOutput) return;

        diamonds.shrink(COST_DIAMONDS);
        template.shrink(1);
        if (output.isEmpty()) inv.setStackInSlot(SLOT_OUTPUT, product);
        else output.grow(2);
        markDirty();
        collapse(); // single use
    }

    /** After its one duplication (or when the block decays first): drop everything except the consumed
     *  Refined Naquadah block and become an Unstable Block of Naquadah. */
    private void collapse() {
        double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
        for (int i = 0; i < inv.getSlots(); i++) {
            if (i == SLOT_FUEL) continue; // consumed by the collapse
            ItemStack s = inv.getStackInSlot(i);
            if (!s.isEmpty()) InventoryHelper.spawnItemStack(world, x, y, z, s.copy());
        }
        world.removeTileEntity(pos);
        world.setBlockState(pos, Registration.UNSTABLE_NAQUADAH_BLOCK.getDefaultState(), 3);
    }

    /** Duplicators only exist in mode=cc. */
    @Override protected boolean isModeDisabled() { return !com.nick1416.wormholetech.ModConfig.isCC(); }
}
