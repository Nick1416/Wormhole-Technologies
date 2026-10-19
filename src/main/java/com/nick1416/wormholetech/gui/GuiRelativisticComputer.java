package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.tile.RelativisticComputerTileEntity;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/** Chrono Field Generator GUI. */
public class GuiRelativisticComputer extends GuiContainer {

    private static final ResourceLocation BG =
            new ResourceLocation("wormholetech", "textures/gui/relativistic_computer.png");
    private static final int BTN_X = 124, BTN_Y = 34, BTN_W = 40, BTN_H = 20;
    private static final ItemStack GHOST = new ItemStack(Registration.AETHERIUS);
    /** Aetherius decay bar under the slot. */
    private static final int BAR_X = 62, BAR_Y = 55, BAR_W = 52, BAR_H = 3;

    private final ContainerRelativisticComputer container;
    private GuiButton toggle;

    public GuiRelativisticComputer(InventoryPlayer inv, RelativisticComputerTileEntity te) {
        super(new ContainerRelativisticComputer(inv, te));
        this.container = (ContainerRelativisticComputer) this.inventorySlots;
        this.xSize = 176;
        this.ySize = ContainerRelativisticComputer.GUI_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        toggle = new GuiButton(0, guiLeft + BTN_X, guiTop + BTN_Y, BTN_W, BTN_H, "");
        buttonList.add(toggle);
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b.id == 0) mc.playerController.sendEnchantPacket(container.windowId, 0);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (toggle != null) toggle.displayString = I18n.format(container.isClientEnabled()
                ? "gui.wormholetech.chrono_field.on" : "gui.wormholetech.chrono_field.off");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(I18n.format("container.wormholetech.relativistic_computer"), 8, 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, 0x404040);
        fontRenderer.drawString(I18n.format("gui.wormholetech.rf_stored", String.format("%,d", container.getClientEnergy())), 8, 20, 0x404040);

        int left = container.getClientAetheriusLeft();
        int sx = ContainerRelativisticComputer.SLOT_X, sy = ContainerRelativisticComputer.SLOT_Y;
        if (left <= 0) {
            // faint Aetherius outline in the empty slot
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(GHOST, sx, sy);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
            drawRect(sx, sy, sx + 16, sy + 16, 0xA08B8B8B);
            GlStateManager.enableDepth();
        }
        // decay bar: full = fresh Aetherius, shrinks as it wears (green -> red)
        int max = com.nick1416.wormholetech.item.ItemAetherius.maxWear();
        float f = Math.max(0f, Math.min(1f, left / (float) max));
        drawRect(BAR_X - 1, BAR_Y - 1, BAR_X + BAR_W + 1, BAR_Y + BAR_H + 1, 0xFF373737);
        drawRect(BAR_X, BAR_Y, BAR_X + BAR_W, BAR_Y + BAR_H, 0xFF1A1A1A);
        if (left > 0) {
            int rgb = java.awt.Color.HSBtoRGB(f / 3f, 1f, 1f) & 0xFFFFFF;
            drawRect(BAR_X, BAR_Y, BAR_X + Math.max(1, Math.round(BAR_W * f)), BAR_Y + BAR_H, 0xFF000000 | rgb);
        }

        int n = container.getClientBoosted();
        String status;
        int col;
        if (!container.isClientEnabled()) { status = I18n.format("gui.wormholetech.chrono_field.status.off"); col = 0x604020; }
        else if (left <= 0) { status = I18n.format("gui.wormholetech.chrono_field.status.need_aetherius"); col = 0x802020; }
        else if (n > 0) {
            status = I18n.format("gui.wormholetech.chrono_field.status.active", n,
                    com.nick1416.wormholetech.util.TextArgs.rf((long) n * ModConfig.chronoFieldRfPerBlockPerTick));
            col = 0x206020;
        } else { status = I18n.format("gui.wormholetech.chrono_field.status.idle"); col = 0x604020; }
        fontRenderer.drawString(status, 8, 61, col);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        int rx = mouseX - guiLeft, ry = mouseY - guiTop;
        int left = container.getClientAetheriusLeft();
        boolean overSlot = rx >= ContainerRelativisticComputer.SLOT_X && rx < ContainerRelativisticComputer.SLOT_X + 16
                && ry >= ContainerRelativisticComputer.SLOT_Y && ry < ContainerRelativisticComputer.SLOT_Y + 16;
        boolean overBar = rx >= BAR_X - 1 && rx <= BAR_X + BAR_W && ry >= BAR_Y - 1 && ry <= BAR_Y + BAR_H;
        if ((overSlot && left <= 0) || overBar) {
            java.util.List<String> tip = new java.util.ArrayList<String>();
            if (left <= 0) tip.add(I18n.format("gui.wormholetech.chrono_field.slot.aetherius",
                    com.nick1416.wormholetech.util.TextArgs.duration(ModConfig.aetheriusDecayTicks)));
            else tip.add(I18n.format("gui.wormholetech.chrono_field.aetherius_left",
                    com.nick1416.wormholetech.util.TextArgs.duration(left)));
            drawHoveringText(tip, mouseX, mouseY);
        }
    }
}
