package com.nick1416.wormholetech.client.gui;

import com.nick1416.wormholetech.knowledge.KnowledgeEvents;
import com.nick1416.wormholetech.knowledge.Planets;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * Repository of Knowledge: Planets / Ancient Technology / Ascension tabs. Everything is drawn by hand so
 * the three tabs are equal width inside the frame and every string is measured to fit (scaled, wrapped or trimmed).
 */
public class GuiRepository extends GuiScreen {
    private static final int W = 248, H = 180, PAD = 8, TAB_Y = 18, TAB_H = 18, GAP = 2;
    private static final String[] TABS = { "planets", "tech", "ascension" };
    private NBTTagCompound data;
    private int tab;

    public GuiRepository(NBTTagCompound d) { data = d; }
    public void setData(NBTTagCompound d) { data = d; }

    private int left() { return (width - W) / 2; }
    private int top() { return (height - H) / 2; }
    private int tabW() { return (W - 2 * PAD - 2 * GAP) / 3; }
    private int tabX(int i) { return left() + PAD + i * (tabW() + GAP); }

    @Override
    protected void mouseClicked(int mx, int my, int button) throws java.io.IOException {
        super.mouseClicked(mx, my, button);
        if (button != 0) return;
        int y = top() + TAB_Y;
        for (int i = 0; i < 3; i++)
            if (mx >= tabX(i) && mx < tabX(i) + tabW() && my >= y && my < y + TAB_H && tab != i) {
                tab = i;
                mc.getSoundHandler().playSound(net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord(net.minecraft.init.SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
    }

    /** Draws s centred at cx, shrunk if it is wider than max. */
    private void fitCentered(String s, int cx, int y, int max, int color) {
        int w = fontRenderer.getStringWidth(s);
        if (w <= max) { fontRenderer.drawString(s, cx - w / 2, y, color); return; }
        float k = (float) max / w;
        GlStateManager.pushMatrix();
        GlStateManager.translate(cx - max / 2f, y + (fontRenderer.FONT_HEIGHT * (1 - k)) / 2f, 0);
        GlStateManager.scale(k, k, 1);
        fontRenderer.drawString(s, 0, 0, color);
        GlStateManager.popMatrix();
    }

    private String trim(String s, int max) {
        if (fontRenderer.getStringWidth(s) <= max) return s;
        return fontRenderer.trimStringToWidth(s, max - fontRenderer.getStringWidth("...")) + "...";
    }

    /** Wrapped text, at most maxLines lines (the last one trimmed); returns the height used. */
    private int wrap(String s, int x, int y, int max, int maxLines, int color) {
        List<String> lines = fontRenderer.listFormattedStringToWidth(s, max);
        int n = Math.min(maxLines, lines.size());
        for (int i = 0; i < n; i++) {
            String l = lines.get(i);
            if (i == n - 1 && lines.size() > n) l = trim(l + "...", max);
            fontRenderer.drawString(l, x, y + i * 10, color);
        }
        return n * 10;
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawDefaultBackground();
        int x = left(), y = top(), inner = W - 2 * PAD;
        drawRect(x - 1, y - 1, x + W + 1, y + H + 1, 0xFF46C8BE);
        drawRect(x, y, x + W, y + H, 0xF0101820);
        fitCentered(I18n.format("tile.wormholetech.repository_of_knowledge.name"), x + W / 2, y + 6, inner, 0x7FF0E6);

        int ty = y + TAB_Y, tw = tabW();
        for (int i = 0; i < 3; i++) {
            int tx = tabX(i);
            boolean sel = i == tab, hover = !sel && mx >= tx && mx < tx + tw && my >= ty && my < ty + TAB_H;
            drawRect(tx, ty, tx + tw, ty + TAB_H, sel ? 0xFF46C8BE : 0xFF2A3440);
            drawRect(tx + 1, ty + 1, tx + tw - 1, ty + TAB_H - (sel ? 0 : 1), sel ? 0xFF1C3A40 : hover ? 0xFF3A4A58 : 0xFF202A34);
            fitCentered(I18n.format("gui.wormholetech.repository.tab." + TABS[i]), tx + tw / 2, ty + 5, tw - 6, sel ? 0x7FF0E6 : hover ? 0xFFFFFF : 0xAAAAAA);
        }
        drawRect(x + PAD, ty + TAB_H, x + W - PAD, ty + TAB_H + 1, 0xFF46C8BE);

        int ly = ty + TAB_H + 8, bottom = y + H - PAD;
        if (tab == 0) {
            String hint = I18n.format("gui.wormholetech.repository.planets_hint");
            int hintH = Math.min(2, fontRenderer.listFormattedStringToWidth(hint, inner).size()) * 10;
            int listEnd = bottom - hintH - 4;
            NBTTagCompound pl = data.getCompoundTag("Planets");
            boolean any = false;
            for (int i = 0; i < Planets.KEYS.length && ly + 16 <= listEnd; i++) {
                String k = Planets.KEYS[i];
                if (!pl.hasKey(k)) continue;
                any = true;
                int pct = Math.max(0, Math.min(100, pl.getInteger(k)));
                String p = pct + "%";
                int pw = fontRenderer.getStringWidth(p);
                fontRenderer.drawString(trim(I18n.format("dimension.wormholetech." + k), inner - pw - 6), x + PAD, ly, 0xFFFFFF);
                fontRenderer.drawString(p, x + W - PAD - pw, ly, pct >= 100 ? 0x7FFF7F : 0xCCCCCC);
                drawRect(x + PAD, ly + 10, x + W - PAD, ly + 14, 0xFF2A3440);
                drawRect(x + PAD, ly + 10, x + PAD + inner * pct / 100, ly + 14, 0xFF46C8BE);
                ly += 22;
            }
            if (!any) wrap(I18n.format("gui.wormholetech.repository.no_planets"), x + PAD, ly, inner, 2, 0xAAAAAA);
            wrap(hint, x + PAD, bottom - hintH, inner, 2, 0x888888);
        } else if (tab == 1) {
            NBTTagCompound t = data.getCompoundTag("Tech");
            for (String k : KnowledgeEvents.TECH) {
                boolean have = t.hasKey(k);
                fontRenderer.drawString(trim(have ? I18n.format("gui.wormholetech.repository.tech." + k) : "???", inner), x + PAD, ly, have ? 0xFFFFFF : 0x666666);
                ly += 10;
                ly += wrap(I18n.format(have ? "gui.wormholetech.repository.tech." + k + ".desc" : "gui.wormholetech.repository.undiscovered"),
                        x + PAD + 6, ly, inner - 6, 2, 0x999999) + 6;
            }
        } else {
            fitCentered(I18n.format("gui.wormholetech.repository.locked"), x + W / 2, (ly + bottom) / 2 - 4, inner, 0xFF7F7F);
        }
        super.drawScreen(mx, my, pt);
    }

    @Override public boolean doesGuiPauseGame() { return false; }
}
