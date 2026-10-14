package com.nick1416.wormholetech;

import com.nick1416.wormholetech.util.WormholeFuel;
import com.nick1416.wormholetech.ModConfig;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.nick1416.wormholetech.util.TimedBlockItems;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = WormholeTech.MODID, value = Side.CLIENT)
public class TooltipHandler {

    /** Registry paths that have tooltip.wormholetech.<path>.summary (+ optional .detail.N). */
    private static final Set<String> DOCUMENTED = new HashSet<String>(Arrays.asList(
            "wormhole_duplicator",
            "unstable_wormhole_duplicator",
            "rf_generator",
            "naquadah_generator",
            "unstable_naquadah_block",
            "refined_naquadah_block",
            "raw_naquadah",
            "refined_naquadah",
            "unstable_naquadah",
            "prysmian",
            "diamond_nugget", "quantum_field_modulator", "derelict_coordinates", "hull_plating", "hull_bulkhead", "hull_lamp", "hull_lamp_gold", "hull_window", "modulator_cradle", "titanium_ingot", "titanium_block",
            "reinforced_gate_frame",
            "prysmian_ore",
            "prysmian_block",
            "unstable_prysmian_block",
            "quantum_circuit",
            "prysmian_logic_frame",
            "naquadah_reactor",
            "unstable_naquadah_reactor",
            "ancient_tablet",
            "high_energy_refiner",
            "relativistic_computer",
            "compressed_naquadah",
            "aetherius",
            "wormhole_pad",
            "wormhole_linker",
            "dialing_computer", "gate_network_computer", "xenolith", "xenolith_tiles", "xeno_lamp", "nanite_core", "intergalactic_coordinates",
            "ancient_gate_frame",
            "gate_locator",
            "naquadah_ore_natural",
            "naquadric_stone",
            "ashen_regolith",
            "naquadah_crystal",
            "ancient_stone", "xenolith_ore", "raw_xenolith", "inactive_xenolith_drone", "xenolith_chip"
    ));

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) return;

        ResourceLocation id = stack.getItem().getRegistryName();
        if (!WormholeTech.MODID.equals(id.getResourceDomain())) return;

        // JEI already appends the mod name; only add it ourselves without JEI (avoids the duplicate line)
        if (!net.minecraftforge.fml.common.Loader.isModLoaded("jei")) event.getToolTip().add(TextFormatting.BLUE + "Wormhole Technologies");

        String path = id.getResourcePath();
        if ("miner_drone".equals(path)) {
            addDocumentedTooltips(event, path);
            event.getToolTip().add(1, TextFormatting.YELLOW + net.minecraft.client.resources.I18n.format(stack.getMetadata() == 0
                    ? "tooltip.wormholetech.miner_drone.naquadah" : "tooltip.wormholetech.miner_drone.overworld"));
            event.getToolTip().add(2, TextFormatting.YELLOW + net.minecraft.client.resources.I18n.format("tooltip.wormholetech.drone_modulator"));
        } else if ("companion_drone".equals(path)) {
            addDocumentedTooltips(event, stack.getMetadata() == 1 ? "research_drone" : "defensive_drone");
            event.getToolTip().add(2, TextFormatting.YELLOW + net.minecraft.client.resources.I18n.format("tooltip.wormholetech.drone_modulator"));
        } else if (path.startsWith("mineral_tuner_")) {
            addMineralTunerTooltips(event, stack);
        } else if (DOCUMENTED.contains(path)) {
            addDocumentedTooltips(event, path);
        }
        addPersistedStateTooltips(event, stack, path);
    }

    @SideOnly(Side.CLIENT)
    private static void addPersistedStateTooltips(ItemTooltipEvent event, ItemStack stack, String path) {
        if ("naquadah_generator".equals(path)) {
            TimedBlockItems.addRemainingTooltip(event.getToolTip(), stack, ModConfig.naquadahGeneratorLifetimeTicks);
        } else if ("refined_naquadah_block".equals(path) && WormholeFuel.isPartial(stack)) {
            int left = Math.max(0, WormholeFuel.lifetime() - WormholeFuel.decayOf(stack));
            event.getToolTip().add(TextFormatting.GRAY + net.minecraft.client.resources.I18n.format(
                    "tooltip.wormholetech.refined_naquadah_block.remaining", WormholeFuel.formatTicks(left)));
        } else if (("wormhole_duplicator".equals(path) || "unstable_wormhole_duplicator".equals(path)
                || "rf_generator".equals(path)) && stack.hasTagCompound()) {
            addLinkedMachineTooltip(event, stack);
        } else if ("high_energy_refiner".equals(path) && stack.hasTagCompound()) {
            int cook = stack.getTagCompound().getInteger("Cook");
            if (cook > 0) {
                int pct = Math.min(100, (cook * 100) / ModConfig.highEnergyRefinerCookTimeTicks);
                event.getToolTip().add(net.minecraft.util.text.TextFormatting.GRAY + "Refine progress: " + pct + "%");
            }
        } else if ("relativistic_computer".equals(path) && stack.hasTagCompound()
                && stack.getTagCompound().hasKey("Aetherius")) {
            event.getToolTip().add(net.minecraft.util.text.TextFormatting.GRAY + "Contains Aetherius");
        }
    }

    /** Saved machine: Refined Naquadah blocks inside and the time left on the current one. */
    @SideOnly(Side.CLIENT)
    private static void addLinkedMachineTooltip(ItemTooltipEvent event, ItemStack stack) {
        net.minecraft.nbt.NBTTagCompound tag = stack.getTagCompound();
        net.minecraftforge.items.ItemStackHandler items = new net.minecraftforge.items.ItemStackHandler();
        if (tag.hasKey("Items")) items.deserializeNBT(tag.getCompoundTag("Items"));
        int blocks = 0;
        for (int i = 0; i < items.getSlots(); i++) {
            if (WormholeFuel.isFuel(items.getStackInSlot(i))) blocks += items.getStackInSlot(i).getCount();
        }
        if (blocks <= 0) return;
        int decay = tag.getCompoundTag("Link").getInteger("Decay");
        int left = Math.max(0, WormholeFuel.lifetime() - decay);
        event.getToolTip().add(TextFormatting.GRAY + net.minecraft.client.resources.I18n.format(
                "tooltip.wormholetech.linked_machine.saved", blocks, WormholeFuel.formatTicks(left)));
    }

    private static void addMineralTunerTooltips(ItemTooltipEvent event, ItemStack stack) {
        String mineral = "Coal";
        if (stack.getItem() instanceof com.nick1416.wormholetech.item.ItemMineralTuner) {
            mineral = ((com.nick1416.wormholetech.item.ItemMineralTuner) stack.getItem()).getMineralName();
        }
        event.getToolTip().add(TextFormatting.GRAY + net.minecraft.client.resources.I18n.format(
                "tooltip.wormholetech.mineral_tuner.summary", mineral));
        if (GuiScreen.isShiftKeyDown()) {
            event.getToolTip().add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                    "tooltip.wormholetech.mineral_tuner.detail.1"));
            event.getToolTip().add(TextFormatting.DARK_GRAY + net.minecraft.client.resources.I18n.format(
                    "tooltip.wormholetech.mineral_tuner.detail.2"));
        } else {
            event.getToolTip().add(TextFormatting.DARK_GRAY
                    + net.minecraft.client.resources.I18n.format("tooltip.wormholetech.more"));
        }
    }

    private static void addDocumentedTooltips(ItemTooltipEvent event, String path) {
        String summaryKey = "tooltip.wormholetech." + path + ".summary";
        String summary = net.minecraft.client.resources.I18n.format(summaryKey);
        if (!summary.equals(summaryKey)) {
            event.getToolTip().add(TextFormatting.GRAY + summary);
        }

        if (GuiScreen.isShiftKeyDown()) {
            for (int i = 1; i <= 6; i++) {
                String key = "tooltip.wormholetech." + path + ".detail." + i;
                String line = net.minecraft.client.resources.I18n.format(key);
                if (!line.equals(key)) {
                    event.getToolTip().add(TextFormatting.DARK_GRAY + line);
                }
            }
        } else {
            // Only show "Hold Shift" if at least one detail line exists
            String firstDetail = "tooltip.wormholetech." + path + ".detail.1";
            if (!net.minecraft.client.resources.I18n.format(firstDetail).equals(firstDetail)) {
                event.getToolTip().add(TextFormatting.DARK_GRAY
                        + net.minecraft.client.resources.I18n.format("tooltip.wormholetech.more"));
            }
        }
    }
}
