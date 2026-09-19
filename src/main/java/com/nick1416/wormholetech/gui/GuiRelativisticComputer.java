package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.RelativisticComputerTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiRelativisticComputer extends GuiContainer {

    private static final ResourceLocation BG =
            new ResourceLocation("wormholetech", "textures/gui/relativistic_computer.png");

    private final ContainerRelativisticComputer container;

    public GuiRelativisticComputer(InventoryPlayer inv, RelativisticComputerTileEntity te) {
        super(new ContainerRelativisticComputer(inv, te));
        this.container = (ContainerRelativisticComputer) this.inventorySlots;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("container.wormholetech.relativistic_computer");
        fontRenderer.drawString(title, 8, 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 0x404040);

        String rf = I18n.format("gui.wormholetech.rf_stored", container.getClientEnergy());
        fontRenderer.drawString(rf, 8, 20, 0x404040);

        String status = I18n.format(container.isClientActive()
                ? "gui.wormholetech.status.active"
                : "gui.wormholetech.status.idle");
        fontRenderer.drawString(status, 8, 52, container.isClientActive() ? 0x206020 : 0x604020);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
