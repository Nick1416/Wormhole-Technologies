package com.nick1416.wormholetech.client.gui;

import com.nick1416.wormholetech.registry.Registration;
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
    /** Pages split to fit: each leaf = fragment index + its wrapped lines (null = paragraph gap). */
    private final List<Leaf> leaves = new ArrayList<Leaf>();
    private static final int TEXT_TOP = 28, FOOTER_Y = 155, ICON_BLOCK = 34, LINE_H = 9, GAP = 4, WRAP = 216;
    private final List<Page> pages = new ArrayList<Page>();

    public GuiAncientTablet() {
        pages.add(new Page(
                "gui.wormholetech.tablet.p0.title",
                new String[] {
                        TextFormatting.OBFUSCATED + "xxxxxxxxxxxxxxxx",
                        TextFormatting.OBFUSCATED + "yyyyyyyyyyyyyyyy",
                        "",
                        "gui.wormholetech.tablet.p0.l1",
                        "gui.wormholetech.tablet.p0.l2",
                        "gui.wormholetech.tablet.p0.l3"
                },
                null));
        pages.add(new Page(
                "gui.wormholetech.tablet.p2.title",
                new String[] {
                        TextFormatting.OBFUSCATED + "mmmmmm nnnnn",
                        "",
                        "gui.wormholetech.tablet.p2.l1",
                        "gui.wormholetech.tablet.p2.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.NAQUADAH_ORE),
                        new ItemStack(net.minecraft.init.Blocks.TNT),
                        new ItemStack(Registration.REFINED_NAQUADAH)
                }));
        pages.add(new Page(
                "gui.wormholetech.tablet.p3.title",
                new String[] {
                        "gui.wormholetech.tablet.p3.l1",
                        "gui.wormholetech.tablet.p3.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.ITEM_NAQUADAH_GENERATOR),
                        new ItemStack(Registration.ITEM_RF_GENERATOR)
                }));
        // Fragment on the mineral tuners: mode=cc only
        if (com.nick1416.wormholetech.ModConfig.isCC()) {
            pages.add(new Page(
                    "gui.wormholetech.tablet.p5.title",
                    new String[] {
                            "gui.wormholetech.tablet.p5.l1",
                            "gui.wormholetech.tablet.p5.l2"
                    },
                    new ItemStack[] {
                            new ItemStack(Registration.MINERAL_TUNER_COAL),
                            new ItemStack(Registration.MINERAL_TUNER_IRON),
                            new ItemStack(Registration.MINERAL_TUNER_GOLD)
                    }));
        }
        // Fragment on the duplicators: mode=cc only
        if (com.nick1416.wormholetech.ModConfig.isCC()) {
            pages.add(new Page(
                    "gui.wormholetech.tablet.p4.title",
                    new String[] {
                            "gui.wormholetech.tablet.p4.l1",
                            "gui.wormholetech.tablet.p4.l2"
                    },
                    new ItemStack[] {
                            new ItemStack(Registration.ITEM_REFINED_NAQUADAH_BLOCK),
                            new ItemStack(Registration.ITEM_WORMHOLE_DUPLICATOR),
                            new ItemStack(Registration.ITEM_UNSTABLE_WORMHOLE_DUPLICATOR)
                    }));
        }
        pages.add(new Page(
                "gui.wormholetech.tablet.p9.title",
                new String[] {
                        "gui.wormholetech.tablet.p9.l1",
                        "gui.wormholetech.tablet.p9.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.CHROMIUM_ORE),
                        new ItemStack(Registration.CHROMIUM),
                        new ItemStack(Registration.PRESSURIZER),
                        new ItemStack(net.minecraft.init.Items.EMERALD)
                }));
        pages.add(new Page(
                "gui.wormholetech.tablet.p6.title",
                new String[] {
                        TextFormatting.OBFUSCATED + "tttt uuuuu",
                        "",
                        "gui.wormholetech.tablet.p6.l1",
                        "gui.wormholetech.tablet.p6.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.HIGH_ENERGY_REFINER),
                        new ItemStack(Registration.AETHERIUS)
                }));
        pages.add(new Page(
                "gui.wormholetech.tablet.p7.title",
                new String[] {
                        "gui.wormholetech.tablet.p7.l1",
                        "gui.wormholetech.tablet.p7.l2",
                        "gui.wormholetech.tablet.p7.l3",
                        "gui.wormholetech.tablet.p7.l4",
                        "gui.wormholetech.tablet.p7.l5"
                },
                new ItemStack[] {
                        titaniumIcon(),
                        new ItemStack(Registration.DIALING_COMPUTER),
                        new ItemStack(Registration.ANCIENT_TABLET),
                        new ItemStack(Registration.GATE_LOCATOR)
                }));
        pages.add(new Page(
                "gui.wormholetech.tablet.p8.title",
                new String[] {
                        "gui.wormholetech.tablet.p8.l1",
                        "gui.wormholetech.tablet.p8.l2"
                },
                new ItemStack[] {
                        new ItemStack(Registration.NAQUADAH_CRYSTAL),
                        new ItemStack(Registration.WORMHOLE_PAD),
                        new ItemStack(Registration.WORMHOLE_LINKER)
                }));
        pages.add(new Page(
                "gui.wormholetech.tablet.p10.title",
                new String[] {
                        "gui.wormholetech.tablet.p10.l1",
                        "gui.wormholetech.tablet.p10.l2",
                        "gui.wormholetech.tablet.p10.l3"
                },
                new ItemStack[] {
                        new ItemStack(Registration.TIME_DILATED_CHIP),
                        new ItemStack(Registration.GATE_NETWORK_COMPUTER),
                        new ItemStack(Registration.RELATIVISTIC_COMPUTER)
                }));
    }

    /** Galacticraft Titanium Block if present, else any oredict blockTitanium, else the Ancient Gate Frame. */
    private static ItemStack titaniumIcon() {
        net.minecraft.item.Item gc = net.minecraft.item.Item.getByNameOrId("galacticraftplanets:asteroids_block");
        if (gc != null) return new ItemStack(gc, 1, 7);
        for (ItemStack s : net.minecraftforge.oredict.OreDictionary.getOres("blockTitanium", false)) {
            ItemStack c = s.copy();
            if (c.getMetadata() == net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE) c.setItemDamage(0);
            return c;
        }
        return new ItemStack(Registration.ANCIENT_GATE_FRAME);
    }

    /** Wraps every fragment and splits it over as many leaves as needed; icons go on its last leaf. */
    private void paginate() {
        leaves.clear();
        for (int pi = 0; pi < pages.size(); pi++) {
            Page p = pages.get(pi);
            List<String> rows = new ArrayList<String>();
            for (int i = 0; i < p.lines.length; i++) {
                String line = p.lines[i];
                String text = line.startsWith("gui.wormholetech.") ? com.nick1416.wormholetech.util.TextArgs.format(line) : line;
                if (i > 0) rows.add(null);
                rows.addAll(fontRenderer.listFormattedStringToWidth(text, WRAP));
            }
            int full = FOOTER_Y - 5 - TEXT_TOP;
            boolean icons = p.icons != null && p.icons.length > 0;
            int last = icons ? full - ICON_BLOCK : full;
            List<Leaf> mine = new ArrayList<Leaf>();
            Leaf cur = new Leaf(pi);
            int h = 0;
            for (String r : rows) {
                int rh = r == null ? GAP : LINE_H;
                if (r == null && cur.rows.isEmpty()) continue;
                if (h + rh > full) { mine.add(cur); cur = new Leaf(pi); h = 0; if (r == null) continue; }
                cur.rows.add(r); h += rh;
            }
            // the last leaf also carries the icons: move overflow rows to one more leaf if needed
            if (icons && h > last) {
                Leaf extra = new Leaf(pi);
                while (h > last && !cur.rows.isEmpty()) {
                    String r = cur.rows.remove(cur.rows.size() - 1);
                    h -= r == null ? GAP : LINE_H;
                    if (r != null) extra.rows.add(0, r);
                }
                while (!cur.rows.isEmpty() && cur.rows.get(cur.rows.size() - 1) == null) cur.rows.remove(cur.rows.size() - 1);
                while (!extra.rows.isEmpty() && extra.rows.get(0) == null) extra.rows.remove(0);
                mine.add(cur); cur = extra;
            }
            mine.add(cur);
            for (int i = 0; i < mine.size(); i++) { mine.get(i).part = i; mine.get(i).parts = mine.size(); }
            mine.get(mine.size() - 1).icons = icons;
            leaves.addAll(mine);
        }
        if (page >= leaves.size()) page = leaves.size() - 1;
    }

    @Override
    public void initGui() {
        paginate();
        buttonList.clear();
        int cx = width / 2;
        int by = height / 2 + 85;
        buttonList.add(new GuiButton(0, cx - 110, by, 70, 20, I18n.format("gui.wormholetech.tablet.prev")));
        buttonList.add(new GuiButton(1, cx + 40, by, 70, 20, I18n.format("gui.wormholetech.tablet.next")));
        buttonList.add(new GuiButton(2, cx - 30, by, 60, 20, I18n.format("gui.done")));
        updateButtons();
    }

    private void updateButtons() {
        buttonList.get(0).enabled = page > 0;
        buttonList.get(1).enabled = page < leaves.size() - 1;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 && page > 0) page--;
        if (button.id == 1 && page < leaves.size() - 1) page++;
        if (button.id == 2) mc.player.closeScreen();
        updateButtons();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        hoveredIcon = ItemStack.EMPTY;
        int cx = width / 2;
        int cy = height / 2;
        int left = cx - 120;
        int top = cy - 90;
        drawGradientRect(left, top, left + 240, top + 170, 0xF0C8B890, 0xF0A09070);
        drawRect(left + 3, top + 3, left + 237, top + 167, 0x66FFFFFF);

        Leaf leaf = leaves.get(page);
        Page p = pages.get(leaf.page);
        String head = title(p, leaf.page) + (leaf.parts > 1 ? " (" + (leaf.part + 1) + "/" + leaf.parts + ")" : "");
        fontRenderer.drawString(head, left + 12, top + 10, 0x3A2A1A);
        int y = top + TEXT_TOP;
        for (String r : leaf.rows) {
            if (r == null) { y += GAP; continue; }
            fontRenderer.drawString(r, left + 12, y, 0x2A2010);
            y += LINE_H;
        }

        if (leaf.icons) {
            y = top + FOOTER_Y - 5 - ICON_BLOCK; // fixed slot just above the footer
            y += 6;
            fontRenderer.drawString(I18n.format("gui.wormholetech.tablet.progression"), left + 12, y, 0x3A2A1A);
            y += 12;
            RenderHelper.enableGUIStandardItemLighting();
            int x = left + 12;
            for (int i = 0; i < p.icons.length; i++) {
                if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) hoveredIcon = p.icons[i];
                itemRender.renderItemAndEffectIntoGUI(p.icons[i], x, y);
                itemRender.renderItemOverlayIntoGUI(fontRenderer, p.icons[i], x, y, null);
                if (i < p.icons.length - 1) {
                    fontRenderer.drawString(I18n.format("gui.wormholetech.tablet.arrow"), x + 18, y + 4, 0x3A2A1A);
                }
                x += 34;
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
        }

        String footer = I18n.format("gui.wormholetech.tablet.page", page + 1, leaves.size());
        fontRenderer.drawString(footer, left + 12, top + 155, 0x5A4A3A);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!hoveredIcon.isEmpty()) {
            List<String> tip = getItemToolTip(hoveredIcon);
            if (JEI) tip.add(TextFormatting.GRAY + I18n.format("gui.wormholetech.tablet.jei_hint"));
            drawHoveringText(tip, mouseX, mouseY);
        }
    }

    private static final boolean JEI = net.minecraftforge.fml.common.Loader.isModLoaded("jei");
    private ItemStack hoveredIcon = ItemStack.EMPTY;

    /** Separate method so JEI classes are only touched when JEI is present. */
    private static void openJei(ItemStack stack, boolean uses) {
        com.nick1416.wormholetech.jei.WormholeJeiPlugin.showRecipes(stack, uses);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (JEI && !hoveredIcon.isEmpty() && (button == 0 || button == 1)) { openJei(hoveredIcon, button == 1); return; }
        super.mouseClicked(mouseX, mouseY, button);
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
        if (JEI && !hoveredIcon.isEmpty() && (keyCode == Keyboard.KEY_R || keyCode == Keyboard.KEY_U)) {
            openJei(hoveredIcon, keyCode == Keyboard.KEY_U);
            return;
        }
        if (keyCode == Keyboard.KEY_LEFT && page > 0) { page--; updateButtons(); return; }
        if (keyCode == Keyboard.KEY_RIGHT && page < leaves.size() - 1) { page++; updateButtons(); return; }
        super.keyTyped(typedChar, keyCode);
    }

    private static final String[] ROMAN = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};

    /** "Fragment N — Name", numbered by position so pages hidden by the mode leave no gap. */
    private static String title(Page p, int index) {
        String t = I18n.format(p.titleKey);
        int dash = t.indexOf(" \u2014 ");
        if (!t.startsWith("Fragment ") || dash < 0 || index >= ROMAN.length) return t;
        return "Fragment " + ROMAN[index] + t.substring(dash);
    }

    private static final class Leaf {
        final int page; int part, parts; boolean icons;
        final List<String> rows = new ArrayList<String>();
        Leaf(int page) { this.page = page; }
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
