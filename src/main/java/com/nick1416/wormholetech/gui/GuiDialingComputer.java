package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.tile.DialingComputerTileEntity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiDialingComputer extends GuiContainer {

    private static final ResourceLocation BG =
            new ResourceLocation("wormholetech", "textures/gui/dialing_computer.png");

    /** Width of the dialing panel (subclasses may add panels to the right). */
    protected static final int PANEL_W = 176;

    protected final ContainerDialingComputer container;
    private GuiButton button;

    public GuiDialingComputer(InventoryPlayer inv, DialingComputerTileEntity te) { this(new ContainerDialingComputer(inv, te)); }

    protected GuiDialingComputer(ContainerDialingComputer c) {
        super(c);
        this.container = c;
        this.xSize = 176;
        this.ySize = ContainerDialingComputer.GUI_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();
        button = addButton(new GuiButton(0, guiLeft + 53, guiTop + 66, 70, 20, ""));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        int s = container.getClientStatus();
        boolean open = container.isClientOpen();
        button.displayString = I18n.format(open ? "gui.wormholetech.dialing.close" : "gui.wormholetech.dialing.dial");
        button.enabled = open || s == DialingComputerTileEntity.STATUS_READY || s == DialingComputerTileEntity.STATUS_UNREACHABLE;
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b.id == 0) mc.playerController.sendEnchantPacket(container.windowId, ContainerDialingComputer.BUTTON_ID);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, PANEL_W, ySize);
        slotFrame(ContainerDialingComputer.UPGRADE_X, ContainerDialingComputer.SLOT_Y, 0xFF7A5FB0);
        if (container.teSlots > 2) slotFrame(ContainerDialingComputer.AETHERIUS_X, ContainerDialingComputer.SLOT_Y, 0xFF5FA8B0);
    }

    /** Vanilla-style 18x18 slot box around an item position, with a thin coloured accent. */
    protected void slotFrame(int x, int y, int accent) {
        int l = guiLeft + x - 1, t = guiTop + y - 1;
        drawRect(l - 1, t - 1, l + 19, t + 19, accent);
        drawRect(l, t, l + 18, t + 18, 0xFFFFFFFF);
        drawRect(l, t, l + 17, t + 17, 0xFF373737);
        drawRect(l + 1, t + 1, l + 17, t + 17, 0xFF8B8B8B);
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format(titleKey()), 8, 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040);

        String rf = I18n.format("gui.wormholetech.rf_stored", container.getClientEnergy());
        fontRenderer.drawString(rf, 8, 18, 0x404040);
        String rate = I18n.format("gui.wormholetech.dialing.rate", String.format("%,d", ModConfig.dialingComputerRfPerTick()));
        fontRenderer.drawString(rate, PANEL_W - 8 - fontRenderer.getStringWidth(rate), 18, 0x404040);

        int s = container.getClientStatus();
        if (s < 0 || s >= DialingComputerTileEntity.STATUS_KEYS.length) s = 0;
        String status = I18n.format("gui.wormholetech.dialing.status." + DialingComputerTileEntity.STATUS_KEYS[s]);
        int col = s == DialingComputerTileEntity.STATUS_OPEN ? 0x1C7A72
                : s == DialingComputerTileEntity.STATUS_READY ? 0x206020 : 0x803020;
        // wrap within the panel, centred, bottom line just above the Dial button (y 66)
        java.util.List<String> lines = fontRenderer.listFormattedStringToWidth(status, PANEL_W - 12);
        if (lines.size() > 2) { // never expected; shrink to one line rather than overlap
            float k = (float) (PANEL_W - 12) / fontRenderer.getStringWidth(status);
            net.minecraft.client.renderer.GlStateManager.pushMatrix();
            net.minecraft.client.renderer.GlStateManager.translate(6, 56, 0);
            net.minecraft.client.renderer.GlStateManager.scale(k, k, 1);
            fontRenderer.drawString(status, 0, 0, col);
            net.minecraft.client.renderer.GlStateManager.popMatrix();
        } else {
            int y = lines.size() > 1 ? 49 : 54; // slot ends at y 48, button starts at 66
            for (String l : lines) { fontRenderer.drawString(l, (PANEL_W - fontRenderer.getStringWidth(l)) / 2, y, col); y += 8; }
        }
    }

    protected String titleKey() { return "container.wormholetech.dialing_computer"; }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
        // empty-slot hints
        net.minecraft.inventory.Slot h = getSlotUnderMouse();
        if (h != null && !h.getHasStack() && h.slotNumber > 0 && h.slotNumber < container.teSlots) {
            String key = h.slotNumber == 1 ? "gui.wormholetech.dialing.slot.upgrade" : "gui.wormholetech.dialing.slot.aetherius";
            drawHoveringText(java.util.Collections.singletonList(I18n.format(key)), mouseX, mouseY);
        }
    }
}
