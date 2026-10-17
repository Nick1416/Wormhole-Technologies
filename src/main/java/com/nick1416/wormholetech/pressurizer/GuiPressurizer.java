package com.nick1416.wormholetech.pressurizer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class GuiPressurizer extends GuiContainer {
    private static final ResourceLocation BG = new ResourceLocation("wormholetech", "textures/gui/hydrothermal_pressurizer.png");
    public static final int ARROW_X = 117, ARROW_Y = 36;
    private static final int BAR_X = 9, BAR_Y = 19, BAR_W = 12, BAR_H = 52;
    private final ContainerPressurizer c;

    public GuiPressurizer(InventoryPlayer inv, PressurizerTileEntity te) {
        super(new ContainerPressurizer(inv, te));
        c = (ContainerPressurizer) inventorySlots;
        xSize = 176;
        ySize = ContainerPressurizer.GUI_HEIGHT;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float pt, int mx, int my) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        int fill = (int) ((long) c.cook() * 24 / PressurizerRecipe.TICKS);
        if (fill > 0) drawTexturedModalRect(guiLeft + ARROW_X, guiTop + ARROW_Y, 176, 0, Math.min(24, fill), 16);
        int h = (int) ((long) c.water() * BAR_H / PressurizerRecipe.TANK);
        if (h > 0) drawTexturedModalRect(guiLeft + BAR_X, guiTop + BAR_Y + BAR_H - h, 176, 32 + BAR_H - h, BAR_W, h);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) {
        fontRenderer.drawString(I18n.format("tile.wormholetech.hydrothermal_pressurizer.name"), 8, 6, 0x404040);
        int st = c.status();
        if (st == PressurizerTileEntity.STATUS_FORMING) {
            int pct = (int) ((long) c.cook() * 100 / PressurizerRecipe.TICKS);
            fontRenderer.drawString(I18n.format("gui.wormholetech.pressurizer.forming", pct), 30, 76, 0x206020);
        } else if (st == PressurizerTileEntity.STATUS_OUTPUT_FULL) {
            fontRenderer.drawString(I18n.format("gui.wormholetech.status.output_full"), 30, 76, 0x604020);
        }
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040);
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawDefaultBackground();
        super.drawScreen(mx, my, pt);
        renderHoveredToolTip(mx, my);
        int x = mx - guiLeft, y = my - guiTop;
        if (x >= BAR_X - 1 && x < BAR_X + BAR_W + 1 && y >= BAR_Y - 1 && y < BAR_Y + BAR_H + 1)
            drawHoveringText(Collections.singletonList(I18n.format("gui.wormholetech.pressurizer.water", c.water(), PressurizerRecipe.TANK)), mx, my);
    }
}
