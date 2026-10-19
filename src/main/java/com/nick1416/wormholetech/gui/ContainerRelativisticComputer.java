package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.RelativisticComputerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** Chrono Field Generator: no machine slots, just RF, status and the on/off switch. */
public class ContainerRelativisticComputer extends SyncedIntsContainer {

    /** Background height; the player inventory is laid out from it (vanilla offsets). */
    public static final int GUI_HEIGHT = 166;
    /** The drawn 18x18 box in relativistic_computer.png (item position); shows the field's chip. */
    public static final int ICON_X = 80, ICON_Y = 36;
    public static final int PLAYER_INV_Y = GUI_HEIGHT - 82;   // 84
    public static final int HOTBAR_Y = PLAYER_INV_Y + 58;      // 142

    private static final int ENERGY = 0, BOOSTED = 1, ENABLED = 2;

    private final RelativisticComputerTileEntity te;

    public ContainerRelativisticComputer(InventoryPlayer playerInv, RelativisticComputerTileEntity te) {
        super(3);
        this.te = te;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, PLAYER_INV_Y + r * 18));
        for (int c = 0; c < 9; c++)
            addSlotToContainer(new Slot(playerInv, c, 8 + c * 18, HOTBAR_Y));
    }

    public RelativisticComputerTileEntity getTe() { return te; }

    @Override
    protected int[] readServerValues() {
        return new int[] { te.getEnergyStored(), te.getBoosted(), te.isEnabled() ? 1 : 0 };
    }

    public int getClientEnergy() { return synced(ENERGY); }
    public int getClientBoosted() { return synced(BOOSTED); }
    public boolean isClientEnabled() { return synced(ENABLED) != 0; }

    /** Button 0 switches the field on/off (sent with the vanilla enchant-item packet). */
    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == 0) {
            te.toggle();
            return true;
        }
        return false;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isSpectator();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) { return ItemStack.EMPTY; }
}
