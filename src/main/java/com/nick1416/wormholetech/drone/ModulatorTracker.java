package com.nick1416.wormholetech.drone;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Loaded, power-capable Quantum Field Modulators per dimension (server side). Only free modulators
 * have a tile entity, so locked cradle modulators and the decorative village one never register.
 * Active Xenolith drones need one within {@link ModConfig#droneModulatorRange} blocks.
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class ModulatorTracker {
    private ModulatorTracker() {}
    private static final Map<Integer, Set<BlockPos>> LOADED = new HashMap<>();

    public static void add(World w, BlockPos p) {
        if (w != null && !w.isRemote) LOADED.computeIfAbsent(w.provider.getDimension(), k -> new HashSet<>()).add(p.toImmutable());
    }

    public static void remove(World w, BlockPos p) {
        if (w == null || w.isRemote) return;
        Set<BlockPos> s = LOADED.get(w.provider.getDimension());
        if (s != null) s.remove(p);
    }

    /** Is a free Quantum Field Modulator within range of (x, y, z) in this world? */
    public static boolean near(World w, double x, double y, double z) {
        Set<BlockPos> s = LOADED.get(w.provider.getDimension());
        if (s == null || s.isEmpty()) return false;
        double r2 = (double) ModConfig.droneModulatorRange * ModConfig.droneModulatorRange;
        for (BlockPos p : s)
            if (p.distanceSqToCenter(x, y, z) <= r2) return true;
        return false;
    }

    @SubscribeEvent
    public static void onUnload(WorldEvent.Unload e) {
        if (!e.getWorld().isRemote) LOADED.remove(e.getWorld().provider.getDimension());
    }
}
