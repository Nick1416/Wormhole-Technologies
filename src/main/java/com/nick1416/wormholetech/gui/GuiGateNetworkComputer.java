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

/** Dialing panel on the left; on the right a universe view of known galaxies, and each galaxy's map of visited destinations. */
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

    /** -1 = universe view, otherwise the galaxy shown. */
    private int view = -2;
    private static final int GALAXY_ICON = 44;
    private static final int[] TINT = { 0xFFFFFF, 0xFF8C66, 0xC8A0FF };

    private int markerX(int i) { return MAP_X + Math.round(GateNetwork.MAP_POS[i][0] * MAP_S); }
    private int markerY(int i) { return MAP_Y + Math.round(GateNetwork.MAP_POS[i][1] * MAP_S); }
    private int galaxyX(int g) { return MAP_X + Math.round(GateNetwork.GALAXY_POS[g][0] * MAP_S); }
    private int galaxyY(int g) { return MAP_Y + Math.round(GateNetwork.GALAXY_POS[g][1] * MAP_S); }
    private boolean visited(int i) { return (gnc.getClientVisited() & 1 << i) != 0; }
    private int hereGalaxy() { return mc.world == null ? 0 : com.nick1416.wormholetech.world.ModDimensions.galaxy(mc.world.provider.getDimension()); }

    /** A galaxy is on the universe map when the player is in it or knows a destination there. */
    private boolean galaxyKnown(int g) {
        if (g == 0 || g == hereGalaxy()) return true;
        for (int i = 0; i < GateNetwork.count(); i++) if (visited(i) && GateNetwork.galaxy(i) == g) return true;
        return false;
    }

    private int view() { if (view == -2) view = hereGalaxy() < GateNetwork.GALAXIES ? hereGalaxy() : -1; return view; }

    private int farX() { return MAP_X + Math.round(GateNetwork.FAR_POS[0] * MAP_S); }
    private int farY() { return MAP_Y + Math.round(GateNetwork.FAR_POS[1] * MAP_S); }
    private boolean overFar(int mouseX, int mouseY) {
        int mx = mouseX - guiLeft, my = mouseY - guiTop;
        return view() < 0 && visited(GateNetwork.ELYNDRA) && Math.abs(mx - farX()) <= MARKER_S / 2 + 1 && Math.abs(my - farY()) <= MARKER_S / 2 + 1;
    }

    private int hovered(int mouseX, int mouseY) {
        if (view() < 0) return overFar(mouseX, mouseY) ? GateNetwork.ELYNDRA : -1;
        int mx = mouseX - guiLeft, my = mouseY - guiTop;
        for (int i = 0; i < GateNetwork.count(); i++)
            if (visited(i) && GateNetwork.galaxy(i) == view() && Math.abs(mx - markerX(i)) <= MARKER_S / 2 + 1 && Math.abs(my - markerY(i)) <= MARKER_S / 2 + 1) return i;
        return -1;
    }

    private int hoveredGalaxy(int mouseX, int mouseY) {
        if (view() >= 0) return -1;
        int mx = mouseX - guiLeft, my = mouseY - guiTop;
        for (int g = 0; g < GateNetwork.GALAXIES; g++)
            if (galaxyKnown(g) && Math.abs(mx - galaxyX(g)) <= GALAXY_ICON / 2 && Math.abs(my - galaxyY(g)) <= GALAXY_ICON / 2) return g;
        return -1;
    }

    /** "Universe" / back button, top right of the map. */
    private int btnX0() { return MAP_X + MAP_S - 4 - fontRenderer.getStringWidth(btnLabel()) - 6; }
    private String btnLabel() { return view() < 0 ? "" : "< " + I18n.format("gui.wormholetech.network.universe"); }
    private boolean overButton(int mouseX, int mouseY) {
        int mx = mouseX - guiLeft, my = mouseY - guiTop;
        return view() >= 0 && mx >= btnX0() && mx <= MAP_X + MAP_S - 3 && my >= MAP_Y + 2 && my <= MAP_Y + 14;
    }

    private void tint(int rgb, float k) { GlStateManager.color((rgb >> 16 & 255) / 255F * k, (rgb >> 8 & 255) / 255F * k, (rgb & 255) / 255F * k, 1F); }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        Gui.drawRect(guiLeft + PANEL_W, guiTop, guiLeft + xSize, guiTop + ySize, 0xFF1B1F2A);
        Gui.drawRect(guiLeft + MAP_X - 1, guiTop + MAP_Y - 1, guiLeft + MAP_X + MAP_S + 1, guiTop + MAP_Y + MAP_S + 1, 0xFF6A7488);
        Gui.drawRect(guiLeft + MAP_X, guiTop + MAP_Y, guiLeft + MAP_X + MAP_S, guiTop + MAP_Y + MAP_S, 0xFF05040A);
        long t = mc.world == null ? 0 : mc.world.getTotalWorldTime();
        if (view() < 0) {
            java.util.Random r = new java.util.Random(1416);
            for (int k = 0; k < 140; k++) {
                int x = guiLeft + MAP_X + r.nextInt(MAP_S), y = guiTop + MAP_Y + r.nextInt(MAP_S), c = 90 + r.nextInt(150);
                Gui.drawRect(x, y, x + 1, y + 1, 0xFF000000 | c << 16 | c << 8 | Math.min(255, c + 20));
            }
            GlStateManager.enableBlend();
            int hg = hoveredGalaxy(mouseX, mouseY);
            for (int g = 0; g < GateNetwork.GALAXIES; g++) {
                if (!galaxyKnown(g)) continue;
                tint(TINT[g], hg == g ? 1.0F : 0.8F);
                mc.getTextureManager().bindTexture(GALAXY);
                int s = GALAXY_ICON + (hg == g ? 4 : 0);
                drawModalRectWithCustomSizedTexture(guiLeft + galaxyX(g) - s / 2, guiTop + galaxyY(g) - s / 2, 0, 0, s, s, s, s);
            }
            if (visited(GateNetwork.ELYNDRA)) {
                boolean sel = gnc.getClientSelected() == GateNetwork.ELYNDRA, hv = overFar(mouseX, mouseY);
                float pulse = 0.7F + 0.3F * (float) Math.sin((t + partialTicks) * 0.15F);
                if (sel) GlStateManager.color(0.55F, 1F, 0.75F, 1F);
                else if (hv) GlStateManager.color(1F, 0.9F, 1F, 1F);
                else GlStateManager.color(0.85F * pulse, 0.65F * pulse, 1F * pulse, 1F);
                mc.getTextureManager().bindTexture(MARKER);
                drawModalRectWithCustomSizedTexture(guiLeft + farX() - MARKER_S / 2, guiTop + farY() - MARKER_S / 2,
                        sel ? MARKER_S : 0, 0, MARKER_S, MARKER_S, MARKER_S * 2, MARKER_S);
            }
            GlStateManager.color(1f, 1f, 1f, 1f);
            GlStateManager.disableBlend();
            return;
        }
        tint(TINT[view()], 1F);
        mc.getTextureManager().bindTexture(GALAXY);
        drawModalRectWithCustomSizedTexture(guiLeft + MAP_X, guiTop + MAP_Y, 0, 0, MAP_S, MAP_S, MAP_S, MAP_S);
        GlStateManager.color(1f, 1f, 1f, 1f);
        {
            boolean ob = overButton(mouseX, mouseY);
            Gui.drawRect(guiLeft + btnX0(), guiTop + MAP_Y + 2, guiLeft + MAP_X + MAP_S - 3, guiTop + MAP_Y + 14, ob ? 0xC0405070 : 0xA0202838);
        }
        int hov = hovered(mouseX, mouseY);
        GlStateManager.enableBlend();
        mc.getTextureManager().bindTexture(MARKER);
        for (int i = 0; i < GateNetwork.count(); i++) {
            if (!visited(i) || GateNetwork.galaxy(i) != view()) continue;
            boolean sel = gnc.getClientSelected() == i;
            boolean here = mc.world != null && GateNetwork.dimension(i) == mc.world.provider.getDimension();
            float pulse = 0.75F + 0.25F * (float) Math.sin((t + partialTicks) * 0.2F + i);
            if (here) GlStateManager.color(0.5F, 0.5F, 0.55F, 0.8F);
            else if (sel) GlStateManager.color(0.55F, 1F, 0.75F, 1F);
            else if (hov == i) GlStateManager.color(1F, 1F, 1F, 1F);
            else GlStateManager.color(pulse, pulse, pulse, 1F);
            drawModalRectWithCustomSizedTexture(guiLeft + markerX(i) - MARKER_S / 2, guiTop + markerY(i) - MARKER_S / 2,
                    sel ? MARKER_S : 0, 0, MARKER_S, MARKER_S, MARKER_S * 2, MARKER_S);
        }
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableBlend();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String head = view() < 0 ? I18n.format("gui.wormholetech.network.universe") : I18n.format("gui.wormholetech.network.galaxy." + view());
        fontRenderer.drawStringWithShadow(head, MAP_X + 4, MAP_Y + 4, 0xC8D2E6);
        if (view() >= 0) fontRenderer.drawStringWithShadow(btnLabel(), btnX0() + 3, MAP_Y + 4, 0xE8ECF6);
        String sel = gnc.getClientSelected() >= 0 ? I18n.format("gui.wormholetech.network.dest." + GateNetwork.KEYS[gnc.getClientSelected()])
                : I18n.format(gnc.getClientVisited() == 0 ? "gui.wormholetech.network.none_visited" : "gui.wormholetech.network.pick");
        fontRenderer.drawStringWithShadow(sel, MAP_X + 4, MAP_Y + MAP_S - 12, 0x9FF0C0);
        if (view() < 0) {
            for (int g = 0; g < GateNetwork.GALAXIES; g++) {
                if (!galaxyKnown(g)) continue;
                String n = I18n.format("gui.wormholetech.network.galaxy." + g);
                fontRenderer.drawStringWithShadow(n, galaxyX(g) - fontRenderer.getStringWidth(n) / 2, galaxyY(g) + GALAXY_ICON / 2 + 2, 0xE8ECF6);
            }
            if (visited(GateNetwork.ELYNDRA)) {
                String n = I18n.format("gui.wormholetech.network.far_point");
                fontRenderer.drawStringWithShadow(n, Math.min(MAP_X + MAP_S - 2 - fontRenderer.getStringWidth(n), farX() - fontRenderer.getStringWidth(n) / 2),
                        farY() - MARKER_S / 2 - 10, 0xD8C0FF);
            }
            return;
        }
        for (int i = 0; i < GateNetwork.count(); i++) {
            if (!visited(i) || GateNetwork.galaxy(i) != view()) continue;
            String n = I18n.format("gui.wormholetech.network.dest." + GateNetwork.KEYS[i]);
            fontRenderer.drawStringWithShadow(n, markerX(i) - fontRenderer.getStringWidth(n) / 2, markerY(i) + MARKER_S / 2 + 2, 0xE8ECF6);
        }
    }

    private void click() {
        mc.getSoundHandler().playSound(net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord(net.minecraft.init.SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws java.io.IOException {
        if (button == 0 && overButton(mouseX, mouseY)) { view = -1; click(); return; }
        int g = hoveredGalaxy(mouseX, mouseY);
        if (button == 0 && g >= 0) { view = g; click(); return; }
        int h = hovered(mouseX, mouseY);
        if (h >= 0 && button == 0) {
            int id = gnc.getClientSelected() == h ? ContainerGateNetworkComputer.CLEAR : ContainerGateNetworkComputer.SELECT_BASE + h;
            mc.playerController.sendEnchantPacket(container.windowId, id);
            click();
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    /** Scroll down/back = zoom out to the universe; scroll up over a galaxy = zoom in. */
    @Override
    public void handleMouseInput() throws java.io.IOException {
        super.handleMouseInput();
        int w = org.lwjgl.input.Mouse.getEventDWheel();
        if (w == 0) return;
        int mx = org.lwjgl.input.Mouse.getEventX() * width / mc.displayWidth;
        int my = height - org.lwjgl.input.Mouse.getEventY() * height / mc.displayHeight - 1;
        if (w < 0 && view() >= 0) view = -1;
        else if (w > 0 && view() < 0) { int g = hoveredGalaxy(mx, my); view = g >= 0 ? g : hereGalaxy(); }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int g = hoveredGalaxy(mouseX, mouseY);
        if (g >= 0) { drawHoveringText(Collections.singletonList(I18n.format("gui.wormholetech.network.zoom_in")), mouseX, mouseY); return; }
        int h = hovered(mouseX, mouseY);
        if (h >= 0 && mc.world != null && GateNetwork.dimension(h) == mc.world.provider.getDimension())
            drawHoveringText(Collections.singletonList(I18n.format("gui.wormholetech.network.here")), mouseX, mouseY);
        else if (h >= 0) drawHoveringText(Collections.singletonList(I18n.format(gnc.getClientSelected() == h
                ? "gui.wormholetech.network.deselect" : "gui.wormholetech.network.select")), mouseX, mouseY);
    }
}
