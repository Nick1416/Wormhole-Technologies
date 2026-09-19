package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.tile.AetheriusRefinerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerHighEnergyRefiner extends Container {

    private static final int INPUT_X = 56, INPUT_Y = 35;
    private static final int OUTPUT_X = 116, OUTPUT_Y = 35;

    private final AetheriusRefinerTileEntity te;
    private int clientEnergy;
    private int clientProgress;

    public ContainerHighEnergyRefiner(InventoryPlayer playerInv, AetheriusRefinerTileEntity te) {
        this.te = te;
        IItemHandlerModifiable inv = te.items();

        addSlotToContainer(new SlotItemHandler(inv, 0, INPUT_X, INPUT_Y));
        addSlotToContainer(new SlotItemHandler(inv, 1, OUTPUT_X, OUTPUT_Y) {
            @Override public boolean isItemValid(ItemStack stack) { return false; }
        });

        int startY = 84;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, startY + r * 18));
        for (int c = 0; c < 9; c++)
            addSlotToContainer(new Slot(playerInv, c, 8 + c * 18, startY + 58));
    }

    public AetheriusRefinerTileEntity getTe() { return te; }

    public int getClientEnergy() { return clientEnergy; }

    public int getClientProgress() { return clientProgress; }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 0, te.getEnergyStored() & 0xFFFF);
        listener.sendWindowProperty(this, 1, (te.getEnergyStored() >> 16) & 0xFFFF);
        listener.sendWindowProperty(this, 2, te.getProgressPercent());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int energy = te.getEnergyStored();
        int progress = te.getProgressPercent();
        for (IContainerListener listener : listeners) {
            if ((clientEnergy & 0xFFFF) != (energy & 0xFFFF)) {
                listener.sendWindowProperty(this, 0, energy & 0xFFFF);
            }
            if (((clientEnergy >> 16) & 0xFFFF) != ((energy >> 16) & 0xFFFF)) {
                listener.sendWindowProperty(this, 1, (energy >> 16) & 0xFFFF);
            }
            if (clientProgress != progress) {
                listener.sendWindowProperty(this, 2, progress);
            }
        }
        clientEnergy = energy;
        clientProgress = progress;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        if (id == 0) clientEnergy = (clientEnergy & ~0xFFFF) | (data & 0xFFFF);
        else if (id == 1) clientEnergy = (clientEnergy & 0xFFFF) | ((data & 0xFFFF) << 16);
        else if (id == 2) clientProgress = data;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack in = slot.getStack();
        ret = in.copy();

        final int teSlots = 2;
        if (index < teSlots) {
            if (!this.mergeItemStack(in, teSlots, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
            slot.onSlotChange(in, ret);
        } else {
            if (in.getItem() == Item.getItemFromBlock(Registration.COMPRESSED_NAQUADAH)) {
                if (!this.mergeItemStack(in, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (in.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (in.getCount() == ret.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, in);
        return ret;
    }
}
