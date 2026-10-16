package com.nick1416.wormholetech.moon;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

import java.util.Random;

/**
 * Ice-free barrens on Sel'Vora. Large noise regions (a few hundred blocks across) lose the frost the cold biome
 * leaves on the surface; there the ground is bare regolith with gravel dust plains, exposed rock and craters.
 * Works on the populate window only (chunk origin + 8..23), so it never cascades. New chunks only.
 */
public final class SelVoraBarrens {
    /** Region noise above this is ice-free (simplex is roughly symmetric around 0, so ~55% barrens). */
    public static final double THRESHOLD = -0.1;
    private static NoiseGeneratorSimplex region, dust;
    private static long seedFor = Long.MIN_VALUE;

    private SelVoraBarrens() {}

    private static void init(World w) {
        if (seedFor == w.getSeed() && region != null) return;
        seedFor = w.getSeed();
        region = new NoiseGeneratorSimplex(new Random(seedFor ^ 0xBA22E45L));
        dust = new NoiseGeneratorSimplex(new Random(seedFor ^ 0xD057L));
    }

    public static boolean iceFree(World w, int x, int z) {
        init(w);
        return region.getValue(x / 320.0, z / 320.0) > THRESHOLD;
    }

    public static void populate(World w, int cx, int cz, Random r) {
        init(w);
        int x0 = (cx << 4) + 8, z0 = (cz << 4) + 8;
        boolean any = false;
        for (int dx = 0; dx < 16; dx++)
            for (int dz = 0; dz < 16; dz++) {
                int x = x0 + dx, z = z0 + dz;
                if (!iceFree(w, x, z)) continue;
                any = true;
                BlockPos top = w.getPrecipitationHeight(new BlockPos(x, 0, z));
                for (BlockPos p = top; p.getY() > top.getY() - 3; p = p.down()) { // strip frost
                    IBlockState s = w.getBlockState(p);
                    if (s.getBlock() == Blocks.SNOW_LAYER || s.getBlock() == Blocks.SNOW) w.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
                    else if (s.getBlock() == Blocks.ICE || s.getBlock() == Blocks.PACKED_ICE) w.setBlockState(p, Registration.MOON_REGOLITH.getDefaultState(), 2);
                }
                BlockPos g = w.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).down();
                if (w.getBlockState(g).getBlock() != Registration.MOON_REGOLITH) continue;
                double d = dust.getValue(x / 48.0, z / 48.0);
                if (d > 0.35) w.setBlockState(g, Blocks.GRAVEL.getDefaultState(), 2);            // dust plains
                else if (d < -0.55) w.setBlockState(g, Blocks.STONE.getDefaultState(), 2);        // exposed rock
                else if (d < -0.45 && r.nextInt(3) == 0) w.setBlockState(g, Blocks.COBBLESTONE.getDefaultState(), 2);
            }
        if (any && r.nextInt(6) == 0 && iceFree(w, x0 + 8, z0 + 8) && !SelVoraOutposts.inside(x0 + 8, z0 + 8, 24)) crater(w, x0 + 8, z0 + 8, r);
    }

    /** A shallow bowl with a gravel floor and a raised rim. */
    private static void crater(World w, int cx, int cz, Random r) {
        int rad = 3 + r.nextInt(4); // stays inside the populate window
        int y = w.getTopSolidOrLiquidBlock(new BlockPos(cx, 0, cz)).getY() - 1;
        for (int dx = -rad - 1; dx <= rad + 1; dx++)
            for (int dz = -rad - 1; dz <= rad + 1; dz++) {
                double d = Math.sqrt(dx * dx + dz * dz);
                BlockPos col = new BlockPos(cx + dx, y, cz + dz);
                if (d <= rad) {
                    int depth = (int) Math.round((1 - (d / rad) * (d / rad)) * rad * 0.6) + 1;
                    for (int k = 0; k < depth + 4; k++) w.setBlockState(col.up(4 - k), Blocks.AIR.getDefaultState(), 2);
                    w.setBlockState(col.down(depth), d < rad * 0.5 ? Blocks.GRAVEL.getDefaultState() : Registration.MOON_REGOLITH.getDefaultState(), 2);
                } else if (d <= rad + 1.2) {
                    w.setBlockState(col.up(), Registration.MOON_REGOLITH.getDefaultState(), 2); // rim
                }
            }
    }
}
