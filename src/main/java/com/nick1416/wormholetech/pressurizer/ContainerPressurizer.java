package com.nick1416.wormholetech.pressurizer;

import com.nick1416.wormholetech.gui.SyncedIntsContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerPressurizer extends SyncedIntsContainer {
    public static final int GUI_HEIGHT = 180;
    static final int[][] POS = {{30, 22}, {30, 52}, {56, 36}, {74, 36}, {92, 36}, {146, 36}};
    private final PressurizerTileEntity te;

    public ContainerPressurizer(InventoryPlayer playerInv, PressurizerTileEntity te) {
        super(3);
        this.te = te;
        for (int i = 0; i < 6; i++) {
            final int idx = i;
            addSlotToContainer(new SlotItemHandler(te.items(), i, POS[i][0], POS[i][1]) {
                @Override public boolean isItemValid(ItemStack s) { return idx != PressurizerTileEntity.OUT && super.isItemValid(s); }
            });
        }
        int invY = GUI_HEIGHT - 82;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, invY + r * 18));
        for (int c = 0; c < 9; c++) addSlotToContainer(new Slot(playerInv, c, 8 + c * 18, invY + 58));
    }

    @Override protected int[] readServerValues() { return new int[] { te.getWater(), te.getCook(), te.getStatus() }; }
    public int water() { return synced(0); }
    public int cook() { return synced(1); }
    public int status() { return synced(2); }

    @Override public boolean canInteractWith(EntityPlayer p) { return !p.isSpectator() && p.getDistanceSq(te.getPos()) < 64; }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack in = slot.getStack(), ret = in.copy();
        if (index < 6) {
            if (!mergeItemStack(in, 6, inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else {
            boolean moved = false;
            for (int i = 0; i < 5 && !moved; i++)
                if (PressurizerTileEntity.valid(i, in)) moved = mergeItemStack(in, i, i + 1, false);
            if (!moved) return ItemStack.EMPTY;
        }
        if (in.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (in.getCount() == ret.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, in);
        return ret;
    }
}
