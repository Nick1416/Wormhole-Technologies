package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.AetheriusRefinerTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiHighEnergyRefiner extends GuiContainer {

    private static final ResourceLocation BG =
            new ResourceLocation("wormholetech", "textures/gui/high_energy_refiner.png");

    private final ContainerHighEnergyRefiner container;

    public GuiHighEnergyRefiner(InventoryPlayer inv, AetheriusRefinerTileEntity te) {
        super(new ContainerHighEnergyRefiner(inv, te));
        this.container = (ContainerHighEnergyRefiner) this.inventorySlots;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // Fill progress arrow proportionally (darker overlay on the drawn arrow region)
        int pct = container.getClientProgress();
        if (pct > 0) {
            int fill = (int) (34 * (pct / 100.0f));
            // simple filled bar under the arrow area
            drawRect(guiLeft + 79, guiTop + 55, guiLeft + 79 + fill, guiTop + 59, 0xFF55AA55);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("container.wormholetech.high_energy_refiner");
        fontRenderer.drawString(title, 8, 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 0x404040);

        String rf = I18n.format("gui.wormholetech.rf_stored", container.getClientEnergy());
        fontRenderer.drawString(rf, 8, 20, 0x404040);

        String progress = I18n.format("gui.wormholetech.progress_percent", container.getClientProgress());
        fontRenderer.drawString(progress, 8, 58, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
