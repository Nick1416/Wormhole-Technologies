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
    private static final ItemStack CHIP = new ItemStack(Registration.TIME_DILATED_CHIP);

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

        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(CHIP, ContainerRelativisticComputer.ICON_X, ContainerRelativisticComputer.ICON_Y);
        RenderHelper.disableStandardItemLighting();

        int n = container.getClientBoosted();
        String status;
        int col;
        if (!container.isClientEnabled()) { status = I18n.format("gui.wormholetech.chrono_field.status.off"); col = 0x604020; }
        else if (n > 0) {
            status = I18n.format("gui.wormholetech.chrono_field.status.active", n,
                    String.format("%,d", n * ModConfig.chronoFieldRfPerBlockPerTick));
            col = 0x206020;
        } else { status = I18n.format("gui.wormholetech.chrono_field.status.idle"); col = 0x604020; }
        fontRenderer.drawString(status, 8, 60, col);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
