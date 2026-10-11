package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.tile.WormholeLink;
import com.nick1416.wormholetech.util.WormholeFuel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

/** Shared GUI for machines with a wormhole connection: status, decay timer and the connect button. */
public abstract class GuiLinkedMachine extends GuiContainer {

    // decay arrow in row B; filled sprite sits right of the panel in the texture
    protected static final int ARROW_X = 106, ARROW_W = 24, ARROW_H = 16;
    private static final int FILL_U = 208, FILL_V = 0;
    protected static final int STATUS_Y = 64, BUTTON_Y = 76;

    private final ResourceLocation bg;
    protected final ContainerLinkedMachine container;
    private GuiButton establish;

    protected GuiLinkedMachine(ContainerLinkedMachine container, ResourceLocation bg) {
        super(container);
        this.container = container;
        this.bg = bg;
        this.xSize = ContainerLinkedMachine.GUI_WIDTH;
        this.ySize = ContainerLinkedMachine.GUI_HEIGHT;
    }

    protected abstract String titleKey();

    /** Hover text for an empty machine slot (container index), or null. */
    protected abstract String slotHintKey(int slotNumber);

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        establish = new GuiButton(ContainerLinkedMachine.BUTTON_ESTABLISH, guiLeft + 8, guiTop + BUTTON_Y,
                xSize - 16, 20, I18n.format("gui.wormholetech.link.establish"));
        buttonList.add(establish);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (establish != null) establish.enabled = container.canEstablishClient();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == ContainerLinkedMachine.BUTTON_ESTABLISH && button.enabled) {
            // vanilla "enchant item" packet carries the button id; the server container validates it
            mc.playerController.sendEnchantPacket(inventorySlots.windowId, button.id);
        }
    }

    protected boolean hasFuel() {
        int i = container.getFuelContainerIndex();
        return i >= 0 && WormholeFuel.isFuel(container.inventorySlots.get(i).getStack());
    }

    /** 0..1 decay of the current block. */
    protected float decayFraction() {
        if (!hasFuel()) return 0f;
        int life = container.getLifetime();
        return Math.max(0f, Math.min(1f, (life - container.getRemaining()) / (float) life));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(bg);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        int fill = Math.round(decayFraction() * ARROW_W);
        if (fill > 0) {
            drawTexturedModalRect(guiLeft + ARROW_X, guiTop + ContainerLinkedMachine.ROW_B_Y,
                    FILL_U, FILL_V, fill, ARROW_H);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format(titleKey()), 8, 6, 0x404040);
        fontRenderer.drawString(I18n.format("gui.wormholetech.link.label"), 8, ContainerLinkedMachine.ROW_B_Y + 4, 0x404040);

        int status = container.getStatus();
        String text;
        int color;
        if (status == WormholeLink.STATUS_CONNECTED) {
            text = I18n.format("gui.wormholetech.link.connected");
            color = 0x1F7A2E;
        } else if (status == WormholeLink.STATUS_PAUSED) {
            text = I18n.format("gui.wormholetech.link.paused");
            color = 0xA05A00;
        } else {
            text = I18n.format("gui.wormholetech.link.not_connected");
            color = 0x9A1C1C;
        }
        fontRenderer.drawString(text, 8, STATUS_Y, color);
        if (hasFuel()) {
            String t = I18n.format("gui.wormholetech.link.remaining", WormholeFuel.formatTicks(container.getRemaining()));
            fontRenderer.drawString(t, xSize - 8 - fontRenderer.getStringWidth(t), STATUS_Y, 0x404040);
        }
        fontRenderer.drawString(I18n.format("container.inventory"), ContainerLinkedMachine.INV_X, ContainerLinkedMachine.INV_Y - 11, 0x404040);
        drawExtraForeground();
    }

    protected void drawExtraForeground() {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);

        Slot hovered = getSlotUnderMouse();
        if (hovered != null && !hovered.getHasStack()) {
            String key = slotHintKey(hovered.slotNumber);
            if (key != null) drawHoveringText(Collections.singletonList(I18n.format(key)), mouseX, mouseY);
        }
        int ax = guiLeft + ARROW_X, ay = guiTop + ContainerLinkedMachine.ROW_B_Y;
        if (hasFuel() && mouseX >= ax && mouseX < ax + ARROW_W && mouseY >= ay && mouseY < ay + ARROW_H) {
            drawHoveringText(Collections.singletonList(I18n.format("gui.wormholetech.link.decay",
                    Math.round(decayFraction() * 100))), mouseX, mouseY);
        }
        if (establish != null && !establish.enabled && establish.isMouseOver()) {
            String key = container.getStatus() != WormholeLink.STATUS_NONE
                    ? "gui.wormholetech.link.already" : "gui.wormholetech.link.need_fuel";
            drawHoveringText(Collections.singletonList(I18n.format(key)), mouseX, mouseY);
        }
    }
}
