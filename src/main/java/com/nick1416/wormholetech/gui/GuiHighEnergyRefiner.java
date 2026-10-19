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
        drawTank();
        slotFrame(ContainerHighEnergyRefiner.BUCKET_X, ContainerHighEnergyRefiner.BUCKET_Y);
        mc.getTextureManager().bindTexture(BG);
        GlStateManager.color(1f, 1f, 1f, 1f);

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

    private void slotFrame(int x, int y) {
        int l = guiLeft + x - 1, t = guiTop + y - 1;
        drawRect(l, t, l + 18, t + 18, 0xFFFFFFFF);
        drawRect(l, t, l + 17, t + 17, 0xFF373737);
        drawRect(l + 1, t + 1, l + 17, t + 17, 0xFF8B8B8B);
    }

    /** Hydrothermal Fluid tank: sunken frame, fluid texture filled from the bottom, gauge ticks. */
    private void drawTank() {
        int x = guiLeft + ContainerHighEnergyRefiner.TANK_X, y = guiTop + ContainerHighEnergyRefiner.TANK_Y;
        int w = ContainerHighEnergyRefiner.TANK_W, h = ContainerHighEnergyRefiner.TANK_H;
        drawRect(x - 1, y - 1, x + w + 1, y + h + 1, 0xFFFFFFFF);
        drawRect(x - 1, y - 1, x + w, y + h, 0xFF373737);
        drawRect(x, y, x + w, y + h, 0xFF2A2A30);
        int amount = container.getClientFluid();
        int fill = Math.min(h, (int) Math.ceil(h * amount / (double) AetheriusRefinerTileEntity.TANK_CAPACITY));
        if (amount > 0 && fill > 0) {
            net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(
                    com.nick1416.wormholetech.fluid.HydrothermalFluid.fluid().getStill().toString());
            mc.getTextureManager().bindTexture(net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.color(1f, 1f, 1f, 1f);
            int top = y + h - fill;
            for (int yy = y + h; yy > top; yy -= 16) {
                int seg = Math.min(16, yy - top);
                drawTexturedModalRect(x, yy - seg, sprite, w, seg);
            }
        }
        for (int i = 1; i < 8; i++) {
            int ty = y + h - h * i / 8;
            drawRect(x, ty, x + (i % 2 == 0 ? 6 : 3), ty + 1, 0x90FFFFFF);
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
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
            case AetheriusRefinerTileEntity.STATUS_NEED_FLUID: key = "gui.wormholetech.status.need_fluid"; color = 0x902020; break;
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
        int rx = mouseX - guiLeft, ry = mouseY - guiTop;
        if (rx >= ContainerHighEnergyRefiner.TANK_X - 1 && rx <= ContainerHighEnergyRefiner.TANK_X + ContainerHighEnergyRefiner.TANK_W
                && ry >= ContainerHighEnergyRefiner.TANK_Y - 1 && ry <= ContainerHighEnergyRefiner.TANK_Y + ContainerHighEnergyRefiner.TANK_H) {
            drawHoveringText(java.util.Arrays.asList(
                    I18n.format("fluid.wormholetech.hydrothermal_fluid"),
                    net.minecraft.util.text.TextFormatting.GRAY + I18n.format("gui.wormholetech.tank_mb",
                            String.format("%,d", container.getClientFluid()), String.format("%,d", AetheriusRefinerTileEntity.TANK_CAPACITY))),
                    mouseX, mouseY);
        } else {
            net.minecraft.inventory.Slot sl = getSlotUnderMouse();
            if (sl != null && sl.slotNumber == 2 && !sl.getHasStack())
                drawHoveringText(java.util.Collections.singletonList(I18n.format("gui.wormholetech.refiner.bucket_slot")), mouseX, mouseY);
        }
    }
}
