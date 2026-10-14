package com.nick1416.wormholetech.drone;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler.MineralMix;
import net.minecraft.item.ItemStack;

import java.util.Random;

/** Isolated so Immersive Engineering classes load only when it is installed. */
final class IEMinerals {
    private IEMinerals() {}
    static ItemStack randomOre(String name, Random r) {
        MineralMix mix = com.nick1416.wormholetech.item.ItemMineralTuner.resolveByName(name);
        if (mix == null) return ItemStack.EMPTY;
        if (mix.recalculatedChances == null || mix.oreOutput == null || mix.oreOutput.isEmpty()) mix.recalculateChances();
        if (mix.oreOutput == null || mix.oreOutput.isEmpty()) return ItemStack.EMPTY;
        ItemStack s = mix.getRandomOre(r);
        return s == null ? ItemStack.EMPTY : s.copy();
    }
}
