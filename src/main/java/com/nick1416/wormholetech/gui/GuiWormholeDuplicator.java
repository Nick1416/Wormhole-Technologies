package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.UnstableWormholeDuplicatorTileEntity;
import com.nick1416.wormholetech.tile.WormholeDuplicatorTileEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiWormholeDuplicator extends GuiLinkedMachine {

    private final boolean unstable;

    public GuiWormholeDuplicator(InventoryPlayer inv, WormholeDuplicatorTileEntity te) {
        super(new ContainerWormholeDuplicator(inv, te),
                new ResourceLocation("wormholetech", "textures/gui/wormhole_duplicator.png"));
        this.unstable = false;
    }

    public GuiWormholeDuplicator(InventoryPlayer inv, UnstableWormholeDuplicatorTileEntity te) {
        super(new ContainerWormholeDuplicator(inv, te),
                new ResourceLocation("wormholetech", "textures/gui/unstable_wormhole_duplicator.png"));
        this.unstable = true;
    }

    @Override
    protected String titleKey() {
        return unstable ? "container.wormholetech.unstable_wormhole_duplicator"
                : "container.wormholetech.wormhole_duplicator";
    }

    @Override
    protected String slotHintKey(int slotNumber) {
        switch (slotNumber) {
            case 0: return unstable ? "gui.wormholetech.slot.fuel_diamond" : "gui.wormholetech.slot.fuel_nugget";
            case 1: return "gui.wormholetech.slot.template.hint";
            case 2: return "gui.wormholetech.slot.output";
            case 3: return unstable ? "gui.wormholetech.slot.link_fuel_single" : "gui.wormholetech.slot.link_fuel";
            case 4: return unstable ? null : "gui.wormholetech.slot.unstable_out";
            default: return null;
        }
    }

    @Override
    protected void drawExtraForeground() {
        if (unstable) {
            fontRenderer.drawString(I18n.format("gui.wormholetech.link.collapse"),
                    ContainerWormholeDuplicator.UNSTABLE_X - 2, ContainerLinkedMachine.ROW_B_Y + 4, 0x8A2020);
        }
    }
}
