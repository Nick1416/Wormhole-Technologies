package com.example.rfgen;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = RFGen.MODID)
public class TooltipHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) return;

        ResourceLocation id = stack.getItem().getRegistryName();
        if (!RFGen.MODID.equals(id.getResourceDomain())) return;

        // Brand line for every mod item
        event.getToolTip().add(TextFormatting.BLUE + "Wormhole Technologies");

        if ("wormhole_duplicator".equals(id.getResourcePath())) {
            addDuplicatorTooltips(event, false);
        } else if ("unstable_wormhole_duplicator".equals(id.getResourcePath())) {
            addDuplicatorTooltips(event, true);
        }
    }

    private static void addDuplicatorTooltips(ItemTooltipEvent event, boolean unstable) {
        String summaryKey = unstable
                ? "tooltip.rfgen.unstable_wormhole_duplicator.summary"
                : "tooltip.rfgen.wormhole_duplicator.summary";
        event.getToolTip().add(TextFormatting.GRAY + net.minecraft.client.resources.I18n.format(summaryKey));

        if (GuiScreen.isShiftKeyDown()) {
            String prefix = unstable
                    ? "tooltip.rfgen.unstable_wormhole_duplicator.detail."
                    : "tooltip.rfgen.wormhole_duplicator.detail.";
            for (int i = 1; i <= 4; i++) {
                String key = prefix + i;
                String line = net.minecraft.client.resources.I18n.format(key);
                if (!line.equals(key)) {
                    event.getToolTip().add(TextFormatting.DARK_GRAY + line);
                }
            }
        } else {
            event.getToolTip().add(TextFormatting.DARK_GRAY
                    + net.minecraft.client.resources.I18n.format("tooltip.rfgen.more"));
        }
    }
}
