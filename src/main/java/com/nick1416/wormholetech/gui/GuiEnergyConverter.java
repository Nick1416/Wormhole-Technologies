package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.tile.GeneratorTileEntity;
import com.nick1416.wormholetech.tile.WormholeLink;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiEnergyConverter extends GuiLinkedMachine {

    public GuiEnergyConverter(InventoryPlayer inv, GeneratorTileEntity te) {
        super(new ContainerEnergyConverter(inv, te),
                new ResourceLocation("wormholetech", "textures/gui/wormhole_energy_converter.png"));
    }

    @Override
    protected String titleKey() { return "container.wormholetech.rf_generator"; }

    @Override
    protected String slotHintKey(int slotNumber) {
        if (slotNumber == 0) return "gui.wormholetech.slot.link_fuel";
        if (slotNumber == 1) return "gui.wormholetech.slot.unstable_out";
        return null;
    }

    @Override
    protected void drawExtraForeground() {
        int y = ContainerLinkedMachine.ROW_A_Y;
        fontRenderer.drawString(I18n.format("gui.wormholetech.rf_stored",
                String.format("%,d / %,d", container.getEnergy(), ModConfig.stableConverterBuffer)), 8, y, 0x404040);
        String rate = container.getStatus() == WormholeLink.STATUS_CONNECTED
                ? I18n.format("gui.wormholetech.link.rate", String.format("%,d", ModConfig.stableConverterRfPerTick))
                : I18n.format("gui.wormholetech.status.idle");
        fontRenderer.drawString(rate, 8, y + 11, 0x404040);
    }
}
