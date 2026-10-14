package com.nick1416.wormholetech.client.gui;

import com.nick1416.wormholetech.knowledge.KnowledgeEvents;
import com.nick1416.wormholetech.knowledge.Planets;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

/** Repository of Knowledge: Planets / Ancient Technology / Ascension tabs. Drawn entirely with rectangles. */
public class GuiRepository extends GuiScreen {
    private static final int W = 248, H = 180;
    private NBTTagCompound data;
    private int tab;

    public GuiRepository(NBTTagCompound d) { data = d; }
    public void setData(NBTTagCompound d) { data = d; }

    @Override
    public void initGui() {
        buttonList.clear();
        int x = (width - W) / 2, y = (height - H) / 2;
        String[] t = { "planets", "tech", "ascension" };
        for (int i = 0; i < 3; i++)
            buttonList.add(new GuiButton(i, x + 4 + i * 80, y + 18, 80, 20, I18n.format("gui.wormholetech.repository.tab." + t[i])));
        refresh();
    }

    private void refresh() { for (GuiButton b : buttonList) b.enabled = b.id != tab; }

    @Override protected void actionPerformed(GuiButton b) { tab = b.id; refresh(); }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawDefaultBackground();
        int x = (width - W) / 2, y = (height - H) / 2;
        drawRect(x - 1, y - 1, x + W + 1, y + H + 1, 0xFF46C8BE);
        drawRect(x, y, x + W, y + H, 0xF0101820);
        drawCenteredString(fontRenderer, I18n.format("tile.wormholetech.repository_of_knowledge.name"), width / 2, y + 6, 0x7FF0E6);
        int ly = y + 46;
        if (tab == 0) {
            NBTTagCompound pl = data.getCompoundTag("Planets");
            boolean any = false;
            for (int i = 0; i < Planets.KEYS.length; i++) {
                String k = Planets.KEYS[i];
                if (!pl.hasKey(k)) continue;
                any = true;
                int pct = Math.min(100, pl.getInteger(k));
                fontRenderer.drawString(I18n.format("dimension.wormholetech." + k), x + 10, ly, 0xFFFFFF);
                String p = pct + "%";
                fontRenderer.drawString(p, x + W - 10 - fontRenderer.getStringWidth(p), ly, pct >= 100 ? 0x7FFF7F : 0xCCCCCC);
                drawRect(x + 10, ly + 11, x + W - 10, ly + 15, 0xFF2A3440);
                drawRect(x + 10, ly + 11, x + 10 + (W - 20) * pct / 100, ly + 15, 0xFF46C8BE);
                ly += 24;
            }
            if (!any) fontRenderer.drawSplitString(I18n.format("gui.wormholetech.repository.no_planets"), x + 10, ly, W - 20, 0xAAAAAA);
            fontRenderer.drawSplitString(I18n.format("gui.wormholetech.repository.planets_hint"), x + 10, y + H - 22, W - 20, 0x888888);
        } else if (tab == 1) {
            NBTTagCompound t = data.getCompoundTag("Tech");
            for (String k : KnowledgeEvents.TECH) {
                boolean have = t.hasKey(k);
                fontRenderer.drawString(have ? I18n.format("gui.wormholetech.repository.tech." + k) : "???", x + 10, ly, have ? 0xFFFFFF : 0x666666);
                fontRenderer.drawSplitString(I18n.format(have ? "gui.wormholetech.repository.tech." + k + ".desc" : "gui.wormholetech.repository.undiscovered"),
                        x + 18, ly + 10, W - 28, 0x999999);
                ly += 34;
            }
        } else {
            drawCenteredString(fontRenderer, I18n.format("gui.wormholetech.repository.locked"), width / 2, y + 96, 0xFF7F7F);
        }
        super.drawScreen(mx, my, pt);
    }

    @Override public boolean doesGuiPauseGame() { return false; }
}
