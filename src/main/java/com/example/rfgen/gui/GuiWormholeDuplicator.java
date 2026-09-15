package com.example.rfgen.gui;

import com.example.rfgen.tile.UnstableWormholeDuplicatorTileEntity;
import com.example.rfgen.tile.WormholeDuplicatorTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class GuiWormholeDuplicator extends GuiContainer {

    private static final ResourceLocation BG =
            new ResourceLocation("rfgen", "textures/gui/wormhole_duplicator.png");

    private final boolean unstable;

    public GuiWormholeDuplicator(InventoryPlayer inv, WormholeDuplicatorTileEntity te) {
        super(new ContainerWormholeDuplicator(inv, te));
        this.xSize = 176;
        this.ySize = 166;
        this.unstable = false;
    }

    public GuiWormholeDuplicator(InventoryPlayer inv, UnstableWormholeDuplicatorTileEntity te) {
        super(new ContainerWormholeDuplicator(inv, te));
        this.xSize = 176;
        this.ySize = 166;
        this.unstable = true;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format(unstable
                ? "container.rfgen.unstable_wormhole_duplicator"
                : "container.rfgen.wormhole_duplicator");
        fontRenderer.drawString(title, 8, 6, 0x404040);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        drawMachineSlotHints(mouseX, mouseY);
    }

    /** Show what each machine slot is for when the cursor is over it (empty or filled). */
    private void drawMachineSlotHints(int mouseX, int mouseY) {
        Slot hovered = getSlotUnderMouse();
        if (hovered == null || hovered.slotNumber > 2) return;

        List<String> lines;
        switch (hovered.slotNumber) {
            case 0:
                lines = Arrays.asList(I18n.format(unstable
                        ? "gui.rfgen.slot.fuel_diamond"
                        : "gui.rfgen.slot.fuel_nugget"));
                break;
            case 1:
                lines = Arrays.asList(I18n.format("gui.rfgen.slot.template.hint"));
                break;
            case 2:
                lines = Arrays.asList(I18n.format("gui.rfgen.slot.output"));
                break;
            default:
                return;
        }

        // Empty slots: explain purpose. Filled slots keep the normal item tooltip.
        if (!hovered.getHasStack()) {
            drawHoveringText(lines, mouseX, mouseY);
        }
    }
}
