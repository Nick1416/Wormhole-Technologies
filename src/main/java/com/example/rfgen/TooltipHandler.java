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
import com.example.rfgen.tile.NaquadahGeneratorTileEntity;
import com.example.rfgen.tile.UnstableGeneratorTileEntity;
import com.example.rfgen.util.TimedBlockItems;
import com.example.rfgen.tile.AetheriusRefinerTileEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = RFGen.MODID, value = Side.CLIENT)
public class TooltipHandler {

    /** Registry paths that have tooltip.rfgen.<path>.summary (+ optional .detail.N). */
    private static final Set<String> DOCUMENTED = new HashSet<String>(Arrays.asList(
            "wormhole_duplicator",
            "unstable_wormhole_duplicator",
            "rf_generator",
            "unstable_wormhole_energy_converter",
            "naquadah_generator",
            "naquadah_ore",
            "unstable_naquadah_block",
            "raw_naquadah",
            "refined_naquadah",
            "unstable_naquadah",
            "prysmian",
            "prysmian_block",
            "unstable_prysmian_block",
            "quantum_glue",
            "naquadah_reactor",
            "unstable_naquadah_reactor",
            "synthetic_mineral",
            "ancient_tablet",
            "high_energy_refiner",
            "relativistic_computer",
            "compressed_naquadah",
            "aetherius",
            "wormhole_pad",
            "wormhole_linker"
    ));

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) return;

        ResourceLocation id = stack.getItem().getRegistryName();
        if (!RFGen.MODID.equals(id.getResourceDomain())) return;

        event.getToolTip().add(TextFormatting.BLUE + "Wormhole Technologies");

        String path = id.getResourcePath();
        if (path.startsWith("mineral_tuner_")) {
            addMineralTunerTooltips(event, stack);
        } else if (DOCUMENTED.contains(path)) {
            addDocumentedTooltips(event, path);
        }
        addPersistedStateTooltips(event, stack, path);
    }

    @SideOnly(Side.CLIENT)
    private static void addPersistedStateTooltips(ItemTooltipEvent event, ItemStack stack, String path) {
        if ("naquadah_generator".equals(path)) {
            TimedBlockItems.addRemainingTooltip(event.getToolTip(), stack, NaquadahGeneratorTileEntity.LIFETIME_TICKS);
        } else if ("unstable_wormhole_energy_converter".equals(path)) {
            TimedBlockItems.addRemainingTooltip(event.getToolTip(), stack, UnstableGeneratorTileEntity.LIFETIME_TICKS);
        } else if ("high_energy_refiner".equals(path) && stack.hasTagCompound()) {
            int cook = stack.getTagCompound().getInteger("Cook");
            if (cook > 0) {
                int pct = Math.min(100, (cook * 100) / AetheriusRefinerTileEntity.COOK_TIME);
                event.getToolTip().add(net.minecraft.util.text.TextFormatting.GRAY + "Refine progress: " + pct + "%");
            }
        } else if ("relativistic_computer".equals(path) && stack.hasTagCompound()
                && stack.getTagCompound().hasKey("Aetherius")) {
            event.getToolTip().add(net.minecraft.util.text.TextFormatting.GRAY + "Contains Aetherius");
        }
    }

    private static void addMineralTunerTooltips(ItemTooltipEvent event, ItemStack stack) {
        String mineral = "Coal";
        if (stack.getItem() instanceof com.example.rfgen.item.ItemMineralTuner) {
            mineral = ((com.example.rfgen.item.ItemMineralTuner) stack.getItem()).getMineralName();
        }
        event.getToolTip().add(TextFormatting.GRAY + net.minecraft.client.resources.I18n.format(
                "tooltip.rfgen.mineral_tuner.summary", mineral));
        if (GuiScreen.isShiftKeyDown()) {
            event.getToolTip().add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                    "tooltip.rfgen.mineral_tuner.detail.1"));
            event.getToolTip().add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                    "tooltip.rfgen.mineral_tuner.detail.2"));
            event.getToolTip().add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                    "tooltip.rfgen.mineral_tuner.detail.3"));
        } else {
            event.getToolTip().add(TextFormatting.DARK_GRAY
                    + net.minecraft.client.resources.I18n.format("tooltip.rfgen.more"));
        }
    }

    private static void addDocumentedTooltips(ItemTooltipEvent event, String path) {
        String summaryKey = "tooltip.rfgen." + path + ".summary";
        String summary = net.minecraft.client.resources.I18n.format(summaryKey);
        if (!summary.equals(summaryKey)) {
            event.getToolTip().add(TextFormatting.GRAY + summary);
        }

        if (GuiScreen.isShiftKeyDown()) {
            for (int i = 1; i <= 6; i++) {
                String key = "tooltip.rfgen." + path + ".detail." + i;
                String line = net.minecraft.client.resources.I18n.format(key);
                if (!line.equals(key)) {
                    event.getToolTip().add(TextFormatting.DARK_GRAY + line);
                }
            }
        } else {
            // Only show "Hold Shift" if at least one detail line exists
            String firstDetail = "tooltip.rfgen." + path + ".detail.1";
            if (!net.minecraft.client.resources.I18n.format(firstDetail).equals(firstDetail)) {
                event.getToolTip().add(TextFormatting.DARK_GRAY
                        + net.minecraft.client.resources.I18n.format("tooltip.rfgen.more"));
            }
        }
    }
}
