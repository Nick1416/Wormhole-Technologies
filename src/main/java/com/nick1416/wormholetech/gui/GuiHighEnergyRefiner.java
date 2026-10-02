package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.AetheriusRefinerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public class GuiHighEnergyRefiner extends GuiContainer {

    private static final ResourceLocation BG =
            new ResourceLocation("wormholetech", "textures/gui/high_energy_refiner.png");

    // Progress arrow: empty arrow is part of the background at (ARROW_X, ARROW_Y);
    // the filled arrow and the moving highlight are sprites to the right of the panel.
    private static final int ARROW_X = 79, ARROW_Y = 36, ARROW_W = 24, ARROW_H = 16;
    private static final int FILL_U = 176, FILL_V = 0;
    private static final int GLOW_U = 176, GLOW_V = 16;
    private static final int GLOW_W = 5;

    // Text rows (y, relative to the panel)
    private static final int TITLE_Y = 6, RF_Y = 20, PROGRESS_Y = 60, STATUS_Y = 72;

    private final ContainerHighEnergyRefiner container;

    public GuiHighEnergyRefiner(InventoryPlayer inv, AetheriusRefinerTileEntity te) {
        super(new ContainerHighEnergyRefiner(inv, te));
        this.container = (ContainerHighEnergyRefiner) this.inventorySlots;
        this.xSize = 176;
        this.ySize = ContainerHighEnergyRefiner.GUI_HEIGHT;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // Fill by real progress
        int fill = (int) (container.getClientProgress() * ARROW_W);
        if (fill > 0) {
            drawTexturedModalRect(guiLeft + ARROW_X, guiTop + ARROW_Y, FILL_U, FILL_V, fill, ARROW_H);
        }

        // While refining, sweep a highlight along the arrow so it visibly moves even at 0%
        if (container.getClientStatus() == AetheriusRefinerTileEntity.STATUS_REFINING) {
            int cycle = ARROW_W + GLOW_W + 8;
            int off = (int) ((Minecraft.getSystemTime() / 45L) % cycle) - GLOW_W;
            int x0 = Math.max(0, off);
            int x1 = Math.min(ARROW_W, off + GLOW_W);
            if (x1 > x0) {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(
                        GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                drawTexturedModalRect(guiLeft + ARROW_X + x0, guiTop + ARROW_Y, GLOW_U + x0, GLOW_V, x1 - x0, ARROW_H);
                GlStateManager.disableBlend();
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("container.wormholetech.high_energy_refiner"), 8, TITLE_Y, 0x404040);
        fontRenderer.drawString(I18n.format("gui.wormholetech.rf_stored", container.getClientEnergy()), 8, RF_Y, 0x404040);

        String pct = String.format(Locale.ROOT, "%.2f", container.getClientProgress() * 100.0);
        fontRenderer.drawString(I18n.format("gui.wormholetech.progress_percent", pct), 8, PROGRESS_Y, 0x404040);

        int status = container.getClientStatus();
        String key;
        int color;
        switch (status) {
            case AetheriusRefinerTileEntity.STATUS_REFINING: key = "gui.wormholetech.status.refining"; color = 0x206020; break;
            case AetheriusRefinerTileEntity.STATUS_NEED_RF: key = "gui.wormholetech.status.need_rf"; color = 0x902020; break;
            case AetheriusRefinerTileEntity.STATUS_OUTPUT_FULL: key = "gui.wormholetech.status.output_full"; color = 0x604020; break;
            default: key = "gui.wormholetech.status.idle"; color = 0x604020; break;
        }
        fontRenderer.drawString(I18n.format(key), 8, STATUS_Y, color);

        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
