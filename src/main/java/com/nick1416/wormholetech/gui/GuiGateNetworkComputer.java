package com.nick1416.wormholetech.gui;

import com.nick1416.wormholetech.stargate.GateNetwork;
import com.nick1416.wormholetech.tile.GateNetworkComputerTileEntity;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

/** Dialing panel on the left, galaxy map of visited destinations on the right. */
public class GuiGateNetworkComputer extends GuiDialingComputer {
    private static final ResourceLocation GALAXY = new ResourceLocation("wormholetech", "textures/gui/galaxy_map.png");
    private static final ResourceLocation MARKER = new ResourceLocation("wormholetech", "textures/gui/galaxy_marker.png");
    /** Map area inside the right panel. */
    private static final int MAP_X = PANEL_W + 4, MAP_Y = 4, MAP_S = 176;
    private static final int MARKER_S = 12;

    private final ContainerGateNetworkComputer gnc;

    public GuiGateNetworkComputer(InventoryPlayer inv, GateNetworkComputerTileEntity te, EntityPlayer player) {
        super(new ContainerGateNetworkComputer(inv, te, player));
        this.gnc = (ContainerGateNetworkComputer) container;
        this.xSize = PANEL_W + MAP_S + 8;
    }

    @Override protected String titleKey() { return "container.wormholetech.gate_network_computer"; }

    private int markerX(int i) { return MAP_X + Math.round(GateNetwork.MAP_POS[i][0] * MAP_S); }
    private int markerY(int i) { return MAP_Y + Math.round(GateNetwork.MAP_POS[i][1] * MAP_S); }
    private boolean visited(int i) { return (gnc.getClientVisited() & 1 << i) != 0; }

    private int hovered(int mouseX, int mouseY) {
        int mx = mouseX - guiLeft, my = mouseY - guiTop;
        for (int i = 0; i < GateNetwork.count(); i++)
            if (visited(i) && Math.abs(mx - markerX(i)) <= MARKER_S / 2 + 1 && Math.abs(my - markerY(i)) <= MARKER_S / 2 + 1) return i;
        return -1;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        // frame
        Gui.drawRect(guiLeft + PANEL_W, guiTop, guiLeft + xSize, guiTop + ySize, 0xFF1B1F2A);
        Gui.drawRect(guiLeft + MAP_X - 1, guiTop + MAP_Y - 1, guiLeft + MAP_X + MAP_S + 1, guiTop + MAP_Y + MAP_S + 1, 0xFF6A7488);
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(GALAXY);
        drawModalRectWithCustomSizedTexture(guiLeft + MAP_X, guiTop + MAP_Y, 0, 0, MAP_S, MAP_S, MAP_S, MAP_S);
        long t = mc.world == null ? 0 : mc.world.getTotalWorldTime();
        int hov = hovered(mouseX, mouseY);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(MARKER);
        for (int i = 0; i < GateNetwork.count(); i++) {
            if (!visited(i)) continue;
            boolean sel = gnc.getClientSelected() == i;
            float pulse = 0.75F + 0.25F * (float) Math.sin((t + partialTicks) * 0.2F + i);
            if (sel) GlStateManager.color(0.55F, 1F, 0.75F, 1F);
            else if (hov == i) GlStateManager.color(1F, 1F, 1F, 1F);
            else GlStateManager.color(pulse, pulse, pulse, 1F);
            // marker texture: left half = ring, right half = selected ring
            drawModalRectWithCustomSizedTexture(guiLeft + markerX(i) - MARKER_S / 2, guiTop + markerY(i) - MARKER_S / 2,
                    sel ? MARKER_S : 0, 0, MARKER_S, MARKER_S, MARKER_S * 2, MARKER_S);
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableBlend();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String head = I18n.format("gui.wormholetech.network.map");
        fontRenderer.drawStringWithShadow(head, MAP_X + 4, MAP_Y + 4, 0xC8D2E6);
        String sel = gnc.getClientSelected() >= 0 ? I18n.format("gui.wormholetech.network.dest." + GateNetwork.KEYS[gnc.getClientSelected()])
                : I18n.format(gnc.getClientVisited() == 0 ? "gui.wormholetech.network.none_visited" : "gui.wormholetech.network.pick");
        fontRenderer.drawStringWithShadow(sel, MAP_X + 4, MAP_Y + MAP_S - 12, 0x9FF0C0);
        for (int i = 0; i < GateNetwork.count(); i++) {
            if (!visited(i)) continue;
            String n = I18n.format("gui.wormholetech.network.dest." + GateNetwork.KEYS[i]);
            fontRenderer.drawStringWithShadow(n, markerX(i) - fontRenderer.getStringWidth(n) / 2, markerY(i) + MARKER_S / 2 + 2, 0xE8ECF6);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws java.io.IOException {
        int h = hovered(mouseX, mouseY);
        if (h >= 0 && button == 0) {
            int id = gnc.getClientSelected() == h ? ContainerGateNetworkComputer.CLEAR : ContainerGateNetworkComputer.SELECT_BASE + h;
            mc.playerController.sendEnchantPacket(container.windowId, id);
            mc.getSoundHandler().playSound(net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord(net.minecraft.init.SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int h = hovered(mouseX, mouseY);
        if (h >= 0) drawHoveringText(Collections.singletonList(I18n.format(gnc.getClientSelected() == h
                ? "gui.wormholetech.network.deselect" : "gui.wormholetech.network.select")), mouseX, mouseY);
    }
}
