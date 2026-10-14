package com.nick1416.wormholetech.stargate;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.block.StargatePortalBlock;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

/**
 * The always-open return gate on P3X-1416: a 2x3 portal in an Ancient Gate Frame on a small
 * stone-brick platform near (0, 0). Built on the first arrival, then reused (and repaired) via
 * {@link ReturnGateData}.
 */
public final class ReturnGates {
    private static final int HALF = 3; // platform spans -3..4 on x and z around the gate

    private ReturnGates() {}

    public static float arrivalYaw() { return 0.0F; } // facing south, away from the gate

    /** Makes sure the gate exists and returns the (safe) arrival position in front of it. */
    public static BlockPos ensure(WorldServer w) {
        ReturnGateData d = ReturnGateData.get(w);
        if (d.gate == null && com.nick1416.wormholetech.derelict.DerelictSpawner.isDerelict(w)) {
            // the ship's hub chunk already holds the gate (built by the chunk generator)
            w.getChunkFromBlockCoords(com.nick1416.wormholetech.derelict.DerelictLayout.GATE.bottomLeft);
            d.gate = com.nick1416.wormholetech.derelict.DerelictLayout.GATE;
            d.arrival = d.gate.front(1, 2);
            d.markDirty();
        }
        if (d.gate == null) {
            int yb = surfaceY(w, 0, 0);
            d.gate = new GateShape(new BlockPos(0, yb + 1, 0), EnumFacing.Axis.X, 2, 3);
            d.arrival = d.gate.front(1, 2);
            buildPlatform(w, yb);
            d.markDirty();
            WormholeTech.LOGGER.info("Built the P3X-1416 return gate at {}", d.gate.bottomLeft);
        }
        buildGate(w, d.gate);
        // keep the arrival spot usable even if someone dug/built there
        BlockPos a = d.arrival;
        if (!StargateTravel.isSafe(w, a)) {
            if (!w.getBlockState(a.down()).getMaterial().isSolid()) w.setBlockState(a.down(), Blocks.STONEBRICK.getDefaultState());
            StargateTravel.clear(w, a);
            StargateTravel.clear(w, a.up());
        }
        return a;
    }

    /** Re-light the return gate when its frame is complete again (e.g. a player repaired it). */
    public static void relightIfIntact(WorldServer w) {
        ReturnGateData d = ReturnGateData.get(w);
        GateShape g = d.gate;
        if (g == null) return;
        for (int y = -1; y <= g.height; y++) {
            for (int x = -1; x <= g.width; x++) {
                boolean corner = (x == -1 || x == g.width) && (y == -1 || y == g.height);
                boolean edge = y == -1 || y == g.height || x == -1 || x == g.width;
                BlockPos p = g.interior(x, y);
                if (corner) continue;
                if (edge && !TitaniumFrame.isAnyFrame(w.getBlockState(p))) return;
                if (!edge && !w.isAirBlock(p) && w.getBlockState(p).getBlock() != Registration.STARGATE_PORTAL) return;
            }
        }
        IBlockState portal = Registration.STARGATE_PORTAL.getDefaultState().withProperty(StargatePortalBlock.AXIS, g.axis);
        for (int y = 0; y < g.height; y++)
            for (int x = 0; x < g.width; x++) {
                BlockPos p = g.interior(x, y);
                if (w.isAirBlock(p)) w.setBlockState(p, portal, 2);
            }
    }

    private static int surfaceY(WorldServer w, int x, int z) {
        w.getChunkFromBlockCoords(new BlockPos(x, 0, z)); // generate/load
        int y = 255;
        while (y > 1) {
            Material m = w.getBlockState(new BlockPos(x, y, z)).getMaterial();
            if (m.blocksMovement() && m != Material.LEAVES && m != Material.WOOD || m.isLiquid()) break;
            y--;
        }
        return Math.min(200, Math.max(8, Math.max(y, w.getSeaLevel())));
    }

    private static void buildPlatform(WorldServer w, int yb) {
        IBlockState floor = Blocks.STONEBRICK.getDefaultState();
        IBlockState support = Blocks.STONE.getDefaultState();
        IBlockState air = Blocks.AIR.getDefaultState();
        for (int x = -HALF; x <= HALF + 1; x++) {
            for (int z = -HALF; z <= HALF + 1; z++) {
                w.setBlockState(new BlockPos(x, yb, z), floor, 2);
                for (int y = yb + 1; y <= yb + 6; y++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (!w.isAirBlock(p)) w.setBlockState(p, air, 2);
                }
                for (int y = yb - 1, n = 0; y > 0 && n < 24; y--, n++) {
                    BlockPos p = new BlockPos(x, y, z);
                    IBlockState s = w.getBlockState(p);
                    if (s.getMaterial().isSolid() && s.getBlock() != Blocks.SAND && s.getBlock() != Blocks.GRAVEL) break;
                    w.setBlockState(p, support, 2);
                }
            }
        }
    }

    private static void buildGate(WorldServer w, GateShape g) {
        IBlockState frame = Registration.ANCIENT_GATE_FRAME.getDefaultState();
        for (int y = -1; y <= g.height; y++) {
            for (int x = -1; x <= g.width; x++) {
                boolean edge = y == -1 || y == g.height || x == -1 || x == g.width;
                BlockPos p = g.interior(x, y);
                if (edge && w.getBlockState(p).getBlock() != Registration.ANCIENT_GATE_FRAME) w.setBlockState(p, frame, 2);
            }
        }
        IBlockState portal = Registration.STARGATE_PORTAL.getDefaultState().withProperty(StargatePortalBlock.AXIS, g.axis);
        for (int y = 0; y < g.height; y++) {
            for (int x = 0; x < g.width; x++) {
                BlockPos p = g.interior(x, y);
                if (w.getBlockState(p) != portal) w.setBlockState(p, portal, 2);
            }
        }
    }
}
