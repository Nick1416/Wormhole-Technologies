package com.example.rfgen.tile;
import com.example.rfgen.duplication.DuplicationRules;

import com.example.rfgen.registry.Registration;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class UnstableWormholeDuplicatorTileEntity extends TileEntity implements ITickable {

    // 0: diamonds, 1: template, 2: output
    private final ItemStackHandler inv = new ItemStackHandler(3) {
        @Override protected void onContentsChanged(int slot) { markDirty(); }
        @Override public boolean isItemValid(int slot, ItemStack s) {
            if (s.isEmpty()) return false;
            if (slot == 0) return s.getItem() == net.minecraft.init.Items.DIAMOND;
            if (slot == 1) return isAllowedTemplate(s);
            return false; // output is machine-only
        }
    };

    private static final int COST_DIAMONDS = 1;
    private boolean used = false; // single-shot

    private boolean isAllowedTemplate(ItemStack s) {
        Item it = s.getItem();
        if (it == net.minecraft.init.Items.DIAMOND) return false;
        
        // new blacklist
        if (DuplicationRules.isBlacklisted(s)) return false;
        
        //if (s.getMaxStackSize() <= 1) return false;   // block tools/armor/containers
        if (s.hasTagCompound()) return false;         // skip NBT items for now
        return true;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote || used) return;

        ItemStack diamonds = inv.getStackInSlot(0);
        ItemStack template = inv.getStackInSlot(1);
        ItemStack output   = inv.getStackInSlot(2);

        if (diamonds.getCount() < COST_DIAMONDS) return;
        if (template.isEmpty() || !isAllowedTemplate(template)) return;

        // produce 2x of the template, consume the template
        ItemStack product = template.copy(); product.setCount(2);

        boolean canOutput = output.isEmpty()
                || (ItemStack.areItemsEqual(output, product)
                    && ItemStack.areItemStackTagsEqual(output, product)
                    && output.getCount() <= output.getMaxStackSize() - 2);
        if (!canOutput) return;

        diamonds.shrink(COST_DIAMONDS);
        template.shrink(1);

        if (output.isEmpty()) inv.setStackInSlot(2, product);
        else { output.grow(2); inv.setStackInSlot(2, output); }

        markDirty();
        used = true;

        // drop everything still inside
        dropAll();

        // morph into your chosen block (placeholder: UNSTABLE_NAQUADAH_BLOCK)
        world.setBlockState(pos, Registration.UNSTABLE_NAQUADAH_BLOCK.getDefaultState(), 3);
        world.removeTileEntity(pos);
    }

    private void dropAll() {
        double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (!s.isEmpty()) {
                InventoryHelper.spawnItemStack(world, x, y, z, s);
                inv.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    // NBT
    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("Items", inv.serializeNBT());
        tag.setBoolean("Used", used);
        return tag;
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("Items")) inv.deserializeNBT(tag.getCompoundTag("Items"));
        used = tag.getBoolean("Used");
    }

    // caps
    private final IItemHandlerModifiable cap = inv;
    @Override public boolean hasCapability(Capability<?> c, EnumFacing f) {
        return c == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(c, f);
    }
    @Override @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> c, EnumFacing f) {
        if (c == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) cap;
        return super.getCapability(c, f);
    }

    public IItemHandlerModifiable items() { return inv; }
}
