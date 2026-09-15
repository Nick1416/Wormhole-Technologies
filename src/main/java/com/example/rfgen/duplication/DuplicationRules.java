package com.example.rfgen.duplication;

import com.example.rfgen.registry.Registration;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class DuplicationRules {
    private DuplicationRules() {}

    // Exact item ids that are always disallowed to duplicate
    private static final Set<ResourceLocation> EXACT = new HashSet<>(Arrays.asList(
        // rfgen items/blocks by registry id (add/remove as you like)
        rl("rfgen", "raw_naquadah"),
        rl("rfgen", "refined_naquadah"),
        rl("rfgen", "unstable_naquadah"),
        rl("rfgen", "naquadah_ore"),
        rl("rfgen", "unstable_naquadah_block"),
        rl("rfgen", "unstable_prysmian_block"),
        rl("rfgen", "wormhole_duplicator"),
        rl("rfgen", "unstable_wormhole_duplicator"),
        rl("rfgen", "rf_generator"),
        rl("rfgen", "prysmian_block"),
        rl("rfgen", "prysmian"),
        rl("rfgen","unstable_wormhole_energy_converter") // if you add one later
    ));

  

    public static boolean isBlacklisted(ItemStack stack) {
        if (stack.isEmpty()) return true;
        Item item = stack.getItem();
        ResourceLocation id = item.getRegistryName();
        if (id == null) return true;

        // 1) exact registry ids
        if (EXACT.contains(id)) return true;


        // 3) itemblocks of specific blocks
        if (item instanceof ItemBlock) {
            Block b = ((ItemBlock) item).getBlock();
            if (b == Registration.WORMHOLE_DUPLICATOR
                || b == Registration.UNSTABLE_WORMHOLE_DUPLICATOR
                || b == Registration.RF_GENERATOR
                || b == Registration.UNSTABLE_NAQUADAH_BLOCK
                || b == Registration.NAQUADAH_ORE
                || b == Registration.PRYSMIAN_BLOCK
                || b == Registration.UNSTABLE_PRYSMIAN_BLOCK
                || b == Registration.UNSTABLE_WORMHOLE_ENERGY_CONVERTER) {
                return true;
            }
        }

        return false;
    }

    private static ResourceLocation rl(String ns, String path) {
        return new ResourceLocation(ns, path);
    }
}
