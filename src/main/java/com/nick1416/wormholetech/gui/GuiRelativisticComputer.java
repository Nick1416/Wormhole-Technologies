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
        this.ySize = ContainerRelativisticComputer.GUI_HEIGHT;
    }

    private static final ResourceLocation CLOCK_ICON =
            new ResourceLocation("wormholetech", "textures/gui/redstone_clock.png");
    private static final int CLOCK_X = 132, CLOCK_Y = 34, CLOCK_W = 24, CLOCK_H = 20;
    private net.minecraft.client.gui.GuiButton clockButton;

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        clockButton = new net.minecraft.client.gui.GuiButton(0, guiLeft + CLOCK_X, guiTop + CLOCK_Y, CLOCK_W, CLOCK_H, "-1");
        buttonList.add(clockButton);
    }

    @Override
    protected void actionPerformed(net.minecraft.client.gui.GuiButton b) {
        if (b.id == 0) mc.playerController.sendEnchantPacket(container.windowId, 0);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (clockButton != null) clockButton.displayString = container.isClientClockForward() ? "+1" : "-1";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        net.minecraft.client.renderer.GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("container.wormholetech.relativistic_computer");
        fontRenderer.drawString(title, 8, 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040);

        String rf = I18n.format("gui.wormholetech.rf_stored", container.getClientEnergy());
        fontRenderer.drawString(rf, 8, 20, 0x404040);

        String status = I18n.format(container.isClientActive()
                ? "gui.wormholetech.status.active"
                : "gui.wormholetech.status.idle");
        fontRenderer.drawString(status, 8, 58, container.isClientActive() ? 0x206020 : 0x604020);
        // tiny redstone clock icon beside the toggle
        // -1: unlit loop, repeater pointing left; +1: lit loop, repeater pointing right
        net.minecraft.client.renderer.GlStateManager.color(1F, 1F, 1F, 1F);
        net.minecraft.client.renderer.GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(CLOCK_ICON);
        drawModalRectWithCustomSizedTexture(CLOCK_X - 18, CLOCK_Y + 2, container.isClientClockForward() ? 16 : 0, 0, 16, 16, 32, 16);
        net.minecraft.client.renderer.GlStateManager.disableBlend();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        int rx = mouseX - guiLeft, ry = mouseY - guiTop;
        if (rx >= CLOCK_X - 18 && rx < CLOCK_X + CLOCK_W && ry >= CLOCK_Y && ry < CLOCK_Y + CLOCK_H) {
            drawHoveringText(java.util.Collections.singletonList(I18n.format(container.isClientClockForward()
                    ? "gui.wormholetech.computer.clock.forward" : "gui.wormholetech.computer.clock.back")), mouseX, mouseY);
        }
    }
}
