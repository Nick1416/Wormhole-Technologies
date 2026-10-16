package com.nick1416.wormholetech.factory;

import com.nick1416.wormholetech.gui.SyncedIntsContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMiniatureFactory extends SyncedIntsContainer {
    public static final int GUI_W = 176, GUI_H = 204;
    public static final int IN_X = 8, IN_Y = 30, OUT_X = 98, OUT_Y = 84, PLAYER_Y = GUI_H - 82;

    private final MiniatureFactoryTileEntity te;

    public ContainerMiniatureFactory(InventoryPlayer playerInv, MiniatureFactoryTileEntity te) {
        super(4);
        this.te = te;
        for (int r = 0; r < 2; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new SlotItemHandler(te.items(), c + r * 9, IN_X + c * 18, IN_Y + r * 18));
        for (int i = 0; i < MiniatureFactoryTileEntity.OUTPUTS; i++)
            addSlotToContainer(new SlotItemHandler(te.items(), MiniatureFactoryTileEntity.INPUTS + i, OUT_X + i * 18, OUT_Y) {
                @Override public boolean isItemValid(ItemStack s) { return false; }
            });
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, PLAYER_Y + r * 18));
        for (int c = 0; c < 9; c++) addSlotToContainer(new Slot(playerInv, c, 8 + c * 18, PLAYER_Y + 58));
    }

    @Override protected int[] readServerValues() { return new int[] { te.getEnergyStored(), te.getProgress(), te.getTotal(), te.getStatus() }; }
    public int energy() { return synced(0); }
    public double progress() { int t = synced(2); return t <= 0 ? 0 : Math.min(1, synced(1) / (double) t); }
    public int status() { return synced(3); }

    @Override public boolean canInteractWith(EntityPlayer p) { return te.usableBy(p); }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack in = slot.getStack(), ret = in.copy();
        int te = MiniatureFactoryTileEntity.SLOTS;
        if (index < te) { if (!mergeItemStack(in, te, inventorySlots.size(), true)) return ItemStack.EMPTY; }
        else if (!mergeItemStack(in, 0, MiniatureFactoryTileEntity.INPUTS, false)) return ItemStack.EMPTY;
        if (in.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (in.getCount() == ret.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, in);
        return ret;
    }
}
