package com.example.rfgen.tile;
import com.example.rfgen.duplication.DuplicationRules;

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
import com.example.rfgen.RFGen;

public class WormholeDuplicatorTileEntity extends TileEntity implements ITickable {

    // 0: diamond, 1: template, 2: output
    private final ItemStackHandler inv = new ItemStackHandler(3) {
        @Override protected void onContentsChanged(int slot) { markDirty(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (slot == 0) return ItemStack.areItemsEqual(stack, RFGen.DIAMOND_NUGGET);
            if (slot == 1) return isAllowedTemplate(stack);
            return false; // output
        }
    };

    private boolean isAllowedTemplate(ItemStack s) {
        Item it = s.getItem();
        if (it == net.minecraft.init.Items.DIAMOND) return false; // no duping diamonds with diamonds :)
        
        // new blacklist
        if (DuplicationRules.isBlacklisted(s)) return false;
        
        //if (s.getMaxStackSize() <= 1) return false;               // blocks tools/armor/cards, etc.
        if (s.hasTagCompound()) return false;                     // avoid NBT items for now
        return true;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        ItemStack diamonds = inv.getStackInSlot(0);
        ItemStack template = inv.getStackInSlot(1);
        ItemStack output   = inv.getStackInSlot(2);

        // need at least 1 diamond + 1 template
        if (diamonds.isEmpty() || template.isEmpty() || !isAllowedTemplate(template)) return;

        // we will produce exactly 2 of the template item
        ItemStack product = template.copy();
        product.setCount(2);

        // can the output accept the product?
        if (output.isEmpty()) {
            if (product.getCount() <= product.getMaxStackSize()) {
                diamonds.shrink(1);
                template.shrink(1);                // destroy template
                inv.setStackInSlot(2, product);    // place 2× in output
                markDirty();
            }
        } else if (ItemStack.areItemsEqual(output, product) && ItemStack.areItemStackTagsEqual(output, product)) {
            int space = output.getMaxStackSize() - output.getCount();
            if (space >= 2) {
                diamonds.shrink(1);
                template.shrink(1);
                output.grow(2);
                inv.setStackInSlot(2, output);
                markDirty();
            }
        }
    }


    // save/load
    @Override public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("Items", inv.serializeNBT());
        return tag;
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("Items")) inv.deserializeNBT(tag.getCompoundTag("Items"));
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
