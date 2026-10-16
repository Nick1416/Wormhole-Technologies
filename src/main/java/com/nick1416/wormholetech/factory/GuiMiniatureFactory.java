package com.nick1416.wormholetech.factory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

/** Drawn without a texture: vanilla-style panel, slot wells, RF bar and progress bar. */
public class GuiMiniatureFactory extends GuiContainer {
    private static final String[] STATUS = { "idle", "running", "need_rf", "output_full", "no_field" };
    private final ContainerMiniatureFactory c;

    public GuiMiniatureFactory(InventoryPlayer inv, MiniatureFactoryTileEntity te) {
        super(new ContainerMiniatureFactory(inv, te));
        c = (ContainerMiniatureFactory) inventorySlots;
        xSize = ContainerMiniatureFactory.GUI_W; ySize = ContainerMiniatureFactory.GUI_H;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float pt, int mx, int my) {
        int x = guiLeft, y = guiTop;
        drawRect(x, y, x + xSize, y + ySize, 0xFF555555);
        drawRect(x + 1, y + 1, x + xSize - 1, y + ySize - 1, 0xFFC6C6C6);
        for (Slot s : inventorySlots.inventorySlots) {
            drawRect(x + s.xPos - 1, y + s.yPos - 1, x + s.xPos + 17, y + s.yPos + 17, 0xFF8B8B8B);
            drawRect(x + s.xPos, y + s.yPos, x + s.xPos + 17, y + s.yPos + 17, 0xFFFFFFFF);
            drawRect(x + s.xPos, y + s.yPos, x + s.xPos + 16, y + s.yPos + 16, 0xFF8B8B8B);
        }
        // progress bar (left of the outputs) and RF bar
        int px = x + 8, py = y + 86, pw = 84;
        drawRect(px, py, px + pw, py + 12, 0xFF373737);
        drawRect(px + 1, py + 1, px + 1 + (int) ((pw - 2) * c.progress()), py + 11, 0xFF46C8BE);
        int ex = x + 8, ey = y + 70, ew = 160;
        drawRect(ex, ey, ex + ew, ey + 6, 0xFF373737);
        drawRect(ex + 1, ey + 1, ex + 1 + (int) ((ew - 2) * Math.min(1.0, c.energy() / 2_000_000.0)), ey + 5, 0xFFD03030);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) {
        fontRenderer.drawString(I18n.format("tile.wormholetech.miniature_factory.name"), 8, 6, 0x404040);
        String st = I18n.format("gui.wormholetech.miniature_factory." + STATUS[Math.max(0, Math.min(STATUS.length - 1, c.status()))]);
        fontRenderer.drawString(st, 8, 18, c.status() == MiniatureFactoryTileEntity.RUNNING ? 0x206020 : 0x902020);
        String rf = I18n.format("gui.wormholetech.rf_stored", c.energy());
        fontRenderer.drawString(rf, xSize - 8 - fontRenderer.getStringWidth(rf), 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040);
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawDefaultBackground();
        super.drawScreen(mx, my, pt);
        renderHoveredToolTip(mx, my);
    }
}
