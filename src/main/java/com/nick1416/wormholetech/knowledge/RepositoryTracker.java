package com.nick1416.wormholetech.knowledge;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

/** Loaded Repositories of Knowledge per dimension (server side). */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class RepositoryTracker {
    private RepositoryTracker() {}
    private static final Map<Integer, Set<BlockPos>> LOADED = new HashMap<>();

    public static void add(World w, BlockPos p) { if (!w.isRemote) LOADED.computeIfAbsent(w.provider.getDimension(), k -> new HashSet<>()).add(p.toImmutable()); }
    public static void remove(World w, BlockPos p) { if (!w.isRemote) { Set<BlockPos> s = LOADED.get(w.provider.getDimension()); if (s != null) s.remove(p); } }

    public static boolean near(World w, double x, double y, double z, double r) {
        Set<BlockPos> s = LOADED.get(w.provider.getDimension());
        if (s == null) return false;
        for (BlockPos p : s) if (p.distanceSqToCenter(x, y, z) <= r * r) return true;
        return false;
    }

    /** Nearest tracked position within r of (x, y, z), or null. */
    public static BlockPos nearest(World w, double x, double y, double z, double r) {
        Set<BlockPos> s = LOADED.get(w.provider.getDimension());
        if (s == null) return null;
        BlockPos best = null; double bd = r * r;
        for (BlockPos p : s) { double d = p.distanceSqToCenter(x, y, z); if (d <= bd) { bd = d; best = p; } }
        return best;
    }

    @SubscribeEvent
    public static void onUnload(WorldEvent.Unload e) { if (!e.getWorld().isRemote) LOADED.remove(e.getWorld().provider.getDimension()); }
}
