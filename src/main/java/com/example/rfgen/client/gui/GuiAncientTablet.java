package com.example.rfgen.client.gui;

import com.example.rfgen.registry.Registration;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiAncientTablet extends GuiScreen {

    private int page;
    private final List<Page> pages = new ArrayList<Page>();

    public GuiAncientTablet() {
        pages.add(new Page(
                "gui.rfgen.tablet.p0.title",
                new String[] {
                        TextFormatting.OBFUSCATED + "xxxxxxxxxxxxxxxx",
                        TextFormatting.OBFUSCATED + "yyyyyyyyyyyyyyyy",
                        "",
                        "gui.rfgen.tablet.p0.l1",
                        "gui.rfgen.tablet.p0.l2"
                },
                null));
        pages.add(new Page(
                "gui.rfgen.tablet.p2.title",
                new String[] {
                        TextFormatting.OBFUSCATED + "mmmmmm nnnnn",
                        "",
                        "gui.rfgen.tablet.p2.l1",
                        "gui.rfgen.tablet.p2.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.NAQUADAH_ORE),
                        new ItemStack(Registration.RAW_NAQUADAH),
                        new ItemStack(Registration.REFINED_NAQUADAH)
                }));
        pages.add(new Page(
                "gui.rfgen.tablet.p3.title",
                new String[] {
                        "gui.rfgen.tablet.p3.l1",
                        "gui.rfgen.tablet.p3.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.ITEM_NAQUADAH_GENERATOR),
                        new ItemStack(Registration.ITEM_UNSTABLE_WORMHOLE_ENERGY_CONVERTER),
                        new ItemStack(Registration.ITEM_RF_GENERATOR)
                }));
        pages.add(new Page(
                "gui.rfgen.tablet.p5.title",
                new String[] {
                        "gui.rfgen.tablet.p5.l1",
                        "gui.rfgen.tablet.p5.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.MINERAL_TUNER_COAL),
                        new ItemStack(Registration.MINERAL_TUNER_IRON),
                        new ItemStack(Registration.MINERAL_TUNER_GOLD)
                }));
        pages.add(new Page(
                "gui.rfgen.tablet.p4.title",
                new String[] {
                        "gui.rfgen.tablet.p4.l1",
                        "gui.rfgen.tablet.p4.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.ITEM_WORMHOLE_DUPLICATOR),
                        new ItemStack(Registration.ITEM_UNSTABLE_WORMHOLE_DUPLICATOR)
                }));
        pages.add(new Page(
                "gui.rfgen.tablet.p6.title",
                new String[] {
                        TextFormatting.OBFUSCATED + "tttt uuuuu",
                        "",
                        "gui.rfgen.tablet.p6.l1",
                        "gui.rfgen.tablet.p6.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.HIGH_ENERGY_REFINER),
                        new ItemStack(Registration.AETHERIUS),
                        new ItemStack(Registration.RELATIVISTIC_COMPUTER),
                        new ItemStack(Registration.WORMHOLE_PAD)
                }));
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int cx = width / 2;
        int by = height / 2 + 85;
        buttonList.add(new GuiButton(0, cx - 110, by, 70, 20, I18n.format("gui.rfgen.tablet.prev")));
        buttonList.add(new GuiButton(1, cx + 40, by, 70, 20, I18n.format("gui.rfgen.tablet.next")));
        buttonList.add(new GuiButton(2, cx - 30, by, 60, 20, I18n.format("gui.done")));
        updateButtons();
    }

    private void updateButtons() {
        buttonList.get(0).enabled = page > 0;
        buttonList.get(1).enabled = page < pages.size() - 1;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 && page > 0) page--;
        if (button.id == 1 && page < pages.size() - 1) page++;
        if (button.id == 2) mc.player.closeScreen();
        updateButtons();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int cx = width / 2;
        int cy = height / 2;
        int left = cx - 120;
        int top = cy - 90;
        drawGradientRect(left, top, left + 240, top + 170, 0xF0C8B890, 0xF0A09070);
        drawRect(left + 3, top + 3, left + 237, top + 167, 0x66FFFFFF);

        Page p = pages.get(page);
        fontRenderer.drawString(I18n.format(p.titleKey), left + 12, top + 10, 0x3A2A1A);
        int y = top + 28;
        for (String line : p.lines) {
            String text = line.startsWith("gui.rfgen.") ? I18n.format(line) : line;
            fontRenderer.drawSplitString(text, left + 12, y, 216, 0x2A2010);
            y += fontRenderer.getWordWrappedHeight(text, 216) + 4;
        }

        if (p.icons != null && p.icons.length > 0) {
            y += 6;
            fontRenderer.drawString(I18n.format("gui.rfgen.tablet.progression"), left + 12, y, 0x3A2A1A);
            y += 12;
            RenderHelper.enableGUIStandardItemLighting();
            int x = left + 12;
            for (int i = 0; i < p.icons.length; i++) {
                itemRender.renderItemAndEffectIntoGUI(p.icons[i], x, y);
                itemRender.renderItemOverlayIntoGUI(fontRenderer, p.icons[i], x, y, null);
                if (i < p.icons.length - 1) {
                    fontRenderer.drawString(I18n.format("gui.rfgen.tablet.arrow"), x + 18, y + 4, 0x3A2A1A);
                }
                x += 34;
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
        }

        String footer = I18n.format("gui.rfgen.tablet.page", page + 1, pages.size());
        fontRenderer.drawString(footer, left + 12, top + 155, 0x5A4A3A);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.player.closeScreen();
            return;
        }
        if (keyCode == Keyboard.KEY_LEFT && page > 0) { page--; updateButtons(); return; }
        if (keyCode == Keyboard.KEY_RIGHT && page < pages.size() - 1) { page++; updateButtons(); return; }
        super.keyTyped(typedChar, keyCode);
    }

    private static final class Page {
        final String titleKey;
        final String[] lines;
        final ItemStack[] icons;
        Page(String titleKey, String[] lines, ItemStack[] icons) {
            this.titleKey = titleKey;
            this.lines = lines;
            this.icons = icons;
        }
    }
}
