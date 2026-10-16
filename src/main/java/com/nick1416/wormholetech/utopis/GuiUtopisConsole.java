package com.nick1416.wormholetech.utopis;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/** Green-on-black terminal showing one archive log per page. */
@SideOnly(Side.CLIENT)
public class GuiUtopisConsole extends GuiScreen {
    private static final int W = 236, H = 168;
    private int page;
    private GuiButton prev, next;

    public GuiUtopisConsole(int entry) { this.page = entry; }

    @Override
    public void initGui() {
        int x = (width - W) / 2, y = (height - H) / 2;
        buttonList.clear();
        prev = addButton(new GuiButton(0, x + 8, y + H - 26, 30, 20, "<"));
        next = addButton(new GuiButton(1, x + W - 38, y + H - 26, 30, 20, ">"));
        addButton(new GuiButton(2, x + W / 2 - 30, y + H - 26, 60, 20, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b.id == 0) page = (page + UtopisConsoleBlock.ENTRIES - 1) % UtopisConsoleBlock.ENTRIES;
        else if (b.id == 1) page = (page + 1) % UtopisConsoleBlock.ENTRIES;
        else mc.displayGuiScreen(null);
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        drawDefaultBackground();
        int x = (width - W) / 2, y = (height - H) / 2;
        drawRect(x - 1, y - 1, x + W + 1, y + H + 1, 0xFF2FBF5A);
        drawRect(x, y, x + W, y + H, 0xF0040A06);
        String head = I18n.format("gui.wormholetech.utopis_console.title", page + 1, UtopisConsoleBlock.ENTRIES);
        fontRenderer.drawString(head, x + 10, y + 8, 0x7FFF9F);
        fontRenderer.drawString(I18n.format("gui.wormholetech.utopis_console.log." + (page + 1) + ".title"), x + 10, y + 22, 0xC8FFD6);
        List<String> lines = fontRenderer.listFormattedStringToWidth(I18n.format("gui.wormholetech.utopis_console.log." + (page + 1)), W - 20);
        int ly = y + 38;
        for (String l : lines) { if (ly > y + H - 36) break; fontRenderer.drawString(l, x + 10, ly, 0x4FE07A); ly += 10; }
        super.drawScreen(mx, my, pt);
    }

    @Override public boolean doesGuiPauseGame() { return false; }
}
