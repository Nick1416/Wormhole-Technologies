package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.tile.AetheriusRefinerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerHighEnergyRefiner extends SyncedIntsContainer {

    /** Background height; the player inventory is laid out from it (vanilla offsets). */
    public static final int GUI_HEIGHT = 180;

    // Item positions (the drawn 18x18 boxes in high_energy_refiner.png sit at x-1, y-1)
    public static final int INPUT_X = 56, INPUT_Y = 36;
    public static final int OUTPUT_X = 116, OUTPUT_Y = 36;
    public static final int PLAYER_INV_Y = GUI_HEIGHT - 82;   // 98
    public static final int HOTBAR_Y = PLAYER_INV_Y + 58;      // 156

    private static final int ENERGY = 0, COOK = 1, COOK_TOTAL = 2, STATUS = 3;

    private final AetheriusRefinerTileEntity te;

    public ContainerHighEnergyRefiner(InventoryPlayer playerInv, AetheriusRefinerTileEntity te) {
        super(4);
        this.te = te;
        IItemHandlerModifiable inv = te.items();

        addSlotToContainer(new SlotItemHandler(inv, 0, INPUT_X, INPUT_Y));
        addSlotToContainer(new SlotItemHandler(inv, 1, OUTPUT_X, OUTPUT_Y) {
            @Override public boolean isItemValid(ItemStack stack) { return false; }
        });

        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, PLAYER_INV_Y + r * 18));
        for (int c = 0; c < 9; c++)
            addSlotToContainer(new Slot(playerInv, c, 8 + c * 18, HOTBAR_Y));
    }

    public AetheriusRefinerTileEntity getTe() { return te; }

    @Override
    protected int[] readServerValues() {
        return new int[] { te.getEnergyStored(), te.getCookTime(), te.getCookTimeTotal(), te.getStatus() };
    }

    public int getClientEnergy() { return synced(ENERGY); }

    public int getClientCook() { return synced(COOK); }

    public int getClientCookTotal() { return synced(COOK_TOTAL); }

    public int getClientStatus() { return synced(STATUS); }

    /** Progress 0..1 at full tick precision. */
    public double getClientProgress() {
        int total = getClientCookTotal();
        if (total <= 0) return 0;
        return Math.max(0, Math.min(1.0, getClientCook() / (double) total));
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
