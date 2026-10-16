package com.nick1416.wormholetech.elyndra;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;

import java.util.Random;

/**
 * What the gate builders left behind on Elyndra: glowstone-tiled plazas, crashed ships, floating islands
 * and quartz shrines where an ascended spirit lingers. Everything stays inside the populate window
 * (chunk origin + 8..23) so nothing cascades into ungenerated chunks.
 */
public final class ElyndraRuins {
    /** The first shrine, a short walk from the arrival terrace. */
    public static final int SHRINE_X = 40, SHRINE_Z = 40;
    private static final int FLAGS = 2;

    private ElyndraRuins() {}

    public static void populate(World w, int cx, int cz, Random r) {
        int ox = (cx << 4) + 16, oz = (cz << 4) + 16; // centre of the populate window
        boolean nearArrival = Math.abs(cx) <= 1 && Math.abs(cz) <= 1;
        if (cx == (SHRINE_X - 8) >> 4 && cz == (SHRINE_Z - 8) >> 4) { shrine(w, SHRINE_X, SHRINE_Z); return; }
        if (nearArrival) return;
        int roll = r.nextInt(100);
        if (roll < 14) plaza(w, ox, oz, r);
        else if (roll < 22) ship(w, ox, oz, r);
        else if (roll < 23) shrine(w, ox, oz);
        if (r.nextInt(9) == 0) island(w, ox, oz, r);
    }

    private static int surface(World w, int x, int z) { return w.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY() - 1; }

    private static void set(World w, BlockPos p, IBlockState s) { w.setBlockState(p, s, FLAGS); }

    /** A worn plaza of glowstone and quartz tiles, a few tiles missing, with broken pillars. */
    private static void plaza(World w, int x0, int z0, Random r) {
        int y = surface(w, x0, z0);
        if (w.getBlockState(new BlockPos(x0, y, z0)).getMaterial().isLiquid()) return;
        IBlockState glow = Blocks.GLOWSTONE.getDefaultState(), quartz = Blocks.QUARTZ_BLOCK.getDefaultState();
        IBlockState chisel = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
        for (int dx = -5; dx <= 5; dx++)
            for (int dz = -5; dz <= 5; dz++) {
                if (r.nextInt(12) == 0) continue; // missing tile
                BlockPos p = new BlockPos(x0 + dx, y, z0 + dz);
                set(w, p, ((dx + dz) & 1) == 0 ? glow : quartz);
                for (int k = 1; k <= 4; k++) if (!w.isAirBlock(p.up(k)) && w.getBlockState(p.up(k)).getBlock() != Blocks.LEAVES
                        && w.getBlockState(p.up(k)).getBlock() != Blocks.LOG) set(w, p.up(k), Blocks.AIR.getDefaultState());
                for (int k = 1; k <= 3; k++) if (w.isAirBlock(p.down(k))) set(w, p.down(k), Blocks.STONE.getDefaultState());
            }
        int[][] corners = { { -5, -5 }, { 5, -5 }, { -5, 5 }, { 5, 5 } };
        for (int[] c : corners) {
            int h = 1 + r.nextInt(5);
            for (int k = 1; k <= h; k++) set(w, new BlockPos(x0 + c[0], y + k, z0 + c[1]), k == h ? chisel : Blocks.QUARTZ_BLOCK.getDefaultState()
                    .withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y));
            if (r.nextBoolean()) set(w, new BlockPos(x0 + c[0], y + h + 1, z0 + c[1]), Blocks.VINE.getDefaultState());
        }
        if (r.nextInt(3) == 0) set(w, new BlockPos(x0, y + 1, z0), Registration.NAQUADAH_CRYSTAL.getDefaultState());
    }

    /** A long-dead ship, nose-down in the ground, hull torn open. */
    private static void ship(World w, int x0, int z0, Random r) {
        int y = surface(w, x0, z0);
        boolean alongX = r.nextBoolean();
        IBlockState hull = Registration.HULL_PLATING.getDefaultState(), window = Registration.HULL_WINDOW.getDefaultState();
        IBlockState lamp = Registration.HULL_LAMP.getDefaultState(), air = Blocks.AIR.getDefaultState();
        for (int l = -7; l <= 7; l++) {
            int dy = y - 2 + (l + 7) / 3; // nose buried, tail raised
            for (int a = -2; a <= 2; a++)
                for (int b = 0; b <= 4; b++) {
                    boolean shell = Math.abs(a) == 2 || b == 0 || b == 4 || Math.abs(l) == 7;
                    BlockPos p = alongX ? new BlockPos(x0 + l, dy + b, z0 + a) : new BlockPos(x0 + a, dy + b, z0 + l);
                    if (!shell) { set(w, p, air); continue; }
                    if (r.nextInt(7) == 0) { set(w, p, air); continue; } // torn plating
                    boolean win = b == 2 && Math.abs(a) == 2 && (l & 1) == 0;
                    set(w, p, win ? window : (b == 4 && l % 4 == 0 && a == 0) ? lamp : hull);
                }
        }
        BlockPos loot = alongX ? new BlockPos(x0 + 3, y + 1 + 10 / 3, z0) : new BlockPos(x0, y + 1 + 10 / 3, z0 + 3);
        if (r.nextInt(2) == 0) set(w, loot, Blocks.DIAMOND_BLOCK.getDefaultState());
    }

    /** A floating island: a grassy top over a rocky, ore-studded cone, sometimes with a tree. */
    private static void island(World w, int x0, int z0, Random r) {
        int y0 = 110 + r.nextInt(41), rad = 4 + r.nextInt(4);
        for (int dx = -rad; dx <= rad; dx++)
            for (int dz = -rad; dz <= rad; dz++) {
                double d = Math.sqrt(dx * dx + dz * dz);
                if (d > rad) continue;
                int depth = (int) ((rad - d) * 1.6) + 1;
                for (int k = 0; k < depth; k++) {
                    BlockPos p = new BlockPos(x0 + dx, y0 - k, z0 + dz);
                    IBlockState s = k == 0 ? Blocks.GRASS.getDefaultState() : k < 3 ? Blocks.DIRT.getDefaultState()
                            : r.nextInt(14) == 0 ? Registration.NATURAL_NAQUADAH_ORE.getDefaultState()
                            : r.nextInt(30) == 0 ? Blocks.DIAMOND_ORE.getDefaultState() : Blocks.STONE.getDefaultState();
                    set(w, p, s);
                }
                if (d < rad - 1 && r.nextInt(6) == 0) set(w, new BlockPos(x0 + dx, y0 + 1, z0 + dz), Blocks.RED_FLOWER.getDefaultState());
            }
        if (r.nextInt(5) == 0) set(w, new BlockPos(x0, y0, z0), Blocks.GLOWSTONE.getDefaultState());
        else if (rad >= 6 && r.nextBoolean()) new WorldGenBigTree(false).generate(w, r, new BlockPos(x0, y0 + 1, z0));
    }

    /** A ring of quartz pillars around a glowstone dais; an ascended spirit waits there. */
    public static void shrine(World w, int x0, int z0) {
        int y = surface(w, x0, z0);
        if (w.getBlockState(new BlockPos(x0, y, z0)).getMaterial().isLiquid()) y = w.getSeaLevel();
        IBlockState chisel = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
        IBlockState pillar = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y);
        for (int dx = -4; dx <= 4; dx++)
            for (int dz = -4; dz <= 4; dz++) {
                if (dx * dx + dz * dz > 20) continue;
                BlockPos p = new BlockPos(x0 + dx, y, z0 + dz);
                set(w, p, Math.abs(dx) <= 1 && Math.abs(dz) <= 1 ? Blocks.GLOWSTONE.getDefaultState() : Blocks.QUARTZ_BLOCK.getDefaultState());
                for (int k = 1; k <= 6; k++) set(w, p.up(k), Blocks.AIR.getDefaultState());
                for (int k = 1; k <= 4; k++) if (!w.getBlockState(p.down(k)).getMaterial().isSolid()) set(w, p.down(k), Blocks.STONE.getDefaultState());
            }
        int[][] ring = { { 4, 0 }, { -4, 0 }, { 0, 4 }, { 0, -4 }, { 3, 3 }, { -3, 3 }, { 3, -3 }, { -3, -3 } };
        for (int[] c : ring) {
            for (int k = 1; k <= 4; k++) set(w, new BlockPos(x0 + c[0], y + k, z0 + c[1]), k == 4 ? chisel : pillar);
            set(w, new BlockPos(x0 + c[0], y + 5, z0 + c[1]), Blocks.GLOWSTONE.getDefaultState());
        }
        EntityAncientSpirit s = new EntityAncientSpirit(w);
        s.setLocationAndAngles(x0 + 0.5, y + 1.2, z0 + 0.5, 180F, 0F);
        w.spawnEntity(s);
    }
}
