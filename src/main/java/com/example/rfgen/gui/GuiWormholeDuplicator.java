package com.example.rfgen.gui;

import com.example.rfgen.tile.UnstableWormholeDuplicatorTileEntity;
import com.example.rfgen.tile.WormholeDuplicatorTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiWormholeDuplicator extends GuiContainer {

    private static final ResourceLocation BG =
            new ResourceLocation("rfgen", "textures/gui/wormhole_duplicator.png");

    // Overloads for both tile types (both use same Container/GUI)
    public GuiWormholeDuplicator(InventoryPlayer inv, WormholeDuplicatorTileEntity te) {
        super(new ContainerWormholeDuplicator(inv, te));
        this.xSize = 176; this.ySize = 166;
    }
    public GuiWormholeDuplicator(InventoryPlayer inv, UnstableWormholeDuplicatorTileEntity te) {
        super(new ContainerWormholeDuplicator(inv, te));
        this.xSize = 176; this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("Wormhole Duplicator"), 8, 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 0x404040);
    }

    // ensure slot tooltips render
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
