package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.DialingComputerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDialingComputer extends SyncedIntsContainer {

    public static final int GUI_HEIGHT = 184;
    public static final int SLOT_X = 80, SLOT_Y = 30;
    /** Upgrade slot (right) and the Gate Network Computer's Aetherius slot (left), same row as the address. */
    public static final int UPGRADE_X = 152, AETHERIUS_X = 8;
    public static final int PLAYER_INV_Y = GUI_HEIGHT - 82;   // 102
    public static final int HOTBAR_Y = PLAYER_INV_Y + 58;      // 160
    public static final int BUTTON_ID = 0;

    private static final int ENERGY = 0, STATUS = 1, OPEN = 2;

    protected final DialingComputerTileEntity te;
    /** Machine slots before the player inventory. */
    public final int teSlots;

    public DialingComputerTileEntity getTe() { return te; }

    public ContainerDialingComputer(InventoryPlayer playerInv, DialingComputerTileEntity te) { this(playerInv, te, 0); }

    protected ContainerDialingComputer(InventoryPlayer playerInv, DialingComputerTileEntity te, int extraInts) {
        super(3 + extraInts);
        this.te = te;
        addSlotToContainer(new SlotItemHandler(te.items(), DialingComputerTileEntity.SLOT_ADDRESS, SLOT_X, SLOT_Y));
        addSlotToContainer(new SlotItemHandler(te.items(), DialingComputerTileEntity.SLOT_UPGRADE, UPGRADE_X, SLOT_Y));
        if (te.isNetwork()) addSlotToContainer(new SlotItemHandler(te.items(), DialingComputerTileEntity.SLOT_AETHERIUS, AETHERIUS_X, SLOT_Y));
        teSlots = te.isNetwork() ? 3 : 2;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, PLAYER_INV_Y + r * 18));
        for (int c = 0; c < 9; c++)
            addSlotToContainer(new Slot(playerInv, c, 8 + c * 18, HOTBAR_Y));
    }

    @Override
    protected int[] readServerValues() {
        return new int[] { te.getEnergyStored(), te.getStatus(), te.isOpen() ? 1 : 0 };
    }

    public int getClientEnergy() { return synced(ENERGY); }
    public int getClientStatus() { return synced(STATUS); }
    public boolean isClientOpen() { return synced(OPEN) != 0; }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !player.isSpectator() && !te.isInvalid() && te.getWorld().getTileEntity(te.getPos()) == te
                && player.getDistanceSq(te.getPos().getX() + 0.5, te.getPos().getY() + 0.5, te.getPos().getZ() + 0.5) <= 64;
    }

    /** Dial/Close button (sent with the vanilla enchant packet). */
    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id != BUTTON_ID || !canInteractWith(player)) return false;
        te.toggle(player);
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack in = slot.getStack();
        ItemStack ret = in.copy();
        if (index < teSlots) {
            if (!this.mergeItemStack(in, teSlots, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else {
            int to = -1;
            for (int i = 0; i < teSlots && to < 0; i++) if (getSlot(i).isItemValid(in) && !getSlot(i).getHasStack()) to = i;
            if (to < 0) return ItemStack.EMPTY;
            getSlot(to).putStack(in.splitStack(1));
        }
        if (in.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (in.getCount() == ret.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, in);
        return ret;
    }
}
