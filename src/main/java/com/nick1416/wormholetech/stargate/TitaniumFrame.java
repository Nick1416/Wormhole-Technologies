package com.nick1416.wormholetech.stargate;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Which blocks count as gate frame. Galacticraft stays a soft dependency: its Titanium Block is
 * looked up by registry name (galacticraftplanets:asteroids_block, meta 7) only when the mod is
 * loaded, and every block registered as oredict "blockTitanium" is accepted too. With neither,
 * {@link #available()} is false and the gate feature reports itself disabled.
 */
public final class TitaniumFrame {

    private static final ResourceLocation GC_BLOCK = new ResourceLocation("galacticraftplanets", "asteroids_block");
    private static final int GC_TITANIUM_META = 7;
    private static final int ANY = -1;

    private static Map<Block, Set<Integer>> accepted;

    private TitaniumFrame() {}

    /** Rebuilds the cache (oredict can change until the server starts, e.g. CraftTweaker). */
    public static synchronized void refresh() {
        Map<Block, Set<Integer>> m = new HashMap<Block, Set<Integer>>();
        if (Loader.isModLoaded("galacticraftplanets")) {
            Block gc = Block.REGISTRY.getObject(GC_BLOCK);
            if (gc != null && gc != net.minecraft.init.Blocks.AIR) add(m, gc, GC_TITANIUM_META);
        }
        for (ItemStack s : OreDictionary.getOres("blockTitanium", false)) {
            Item it = s.getItem();
            if (it instanceof ItemBlock) {
                int meta = s.getMetadata() == OreDictionary.WILDCARD_VALUE ? ANY : s.getMetadata();
                add(m, ((ItemBlock) it).getBlock(), meta);
            }
        }
        accepted = m;
        WormholeTech.LOGGER.info("Gate frame blocks: {}", m.isEmpty() ? "none found (gate disabled)" : m.keySet());
    }

    private static void add(Map<Block, Set<Integer>> m, Block b, int meta) {
        Set<Integer> s = m.get(b);
        if (s == null) m.put(b, s = new HashSet<Integer>());
        s.add(meta);
    }

    private static Map<Block, Set<Integer>> map() {
        if (accepted == null) refresh();
        return accepted;
    }

    public static boolean available() {
        return ModConfig.stargateEnabled && !map().isEmpty();
    }

    /** Frame block of a dialable (origin) gate. */
    public static boolean isTitanium(IBlockState state) {
        Set<Integer> metas = map().get(state.getBlock());
        if (metas == null) return false;
        if (metas.contains(ANY)) return true;
        Block b = state.getBlock();
        return metas.contains(b.damageDropped(state)) || metas.contains(b.getMetaFromState(state));
    }

    /** Titanium or the return gate's Ancient Gate Frame. */
    public static boolean isAnyFrame(IBlockState state) {
        return state.getBlock() == Registration.ANCIENT_GATE_FRAME || isTitanium(state);
    }
}
