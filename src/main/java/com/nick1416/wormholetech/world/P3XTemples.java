package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

/**
 * Three small Ancient Stone temples for the P3X-1416 surface: an open shrine, a closed sanctum and a
 * stepped pyramid. Footprints are at most 9x9 and placed inside the chunk's population area.
 */
public final class P3XTemples {
    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(WormholeTech.MODID, "chests/p3x_temple"));

    private P3XTemples() {}

    private static final IBlockState ANC = Registration.ANCIENT_STONE.getDefaultState();
    private static final IBlockState FLOOR = Registration.NAQUADRIC_STONE.getDefaultState();
    private static final IBlockState AIR = Blocks.AIR.getDefaultState();

    private static void set(World w, BlockPos p, IBlockState s) {
        if (p.getY() > 0 && p.getY() < 255) w.setBlockState(p, s, 2);
    }

    /** Solid foundation under the footprint (down to the ground, at most 10 blocks) and clear air above. */
    private static void prepare(World w, BlockPos c, int r, int height) {
        for (int dx = -r; dx <= r; dx++)
            for (int dz = -r; dz <= r; dz++) {
                BlockPos col = c.add(dx, -1, dz);
                for (int d = 0; d < 10; d++) {
                    BlockPos p = col.down(d);
                    IBlockState s = w.getBlockState(p);
                    if (d > 0 && s.getMaterial().blocksMovement() && s.getBlock() != Registration.NAQUADAH_CRYSTAL) break;
                    set(w, p, ANC);
                }
                for (int y = 0; y <= height; y++) set(w, c.add(dx, y, dz), AIR);
            }
    }

    private static void chest(World w, Random rand, BlockPos p, EnumFacing facing) {
        if (ModConfig.p3xTempleChestChance <= 0 || rand.nextInt(ModConfig.p3xTempleChestChance) != 0) return;
        set(w, p, Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, facing));
        TileEntity te = w.getTileEntity(p);
        if (te instanceof TileEntityChest) ((TileEntityChest) te).setLootTable(LOOT, rand.nextLong());
    }

    private static void crystal(World w, BlockPos p) {
        set(w, p, Registration.NAQUADAH_CRYSTAL.getDefaultState());
    }

    public static void generate(World w, Random rand, BlockPos base) {
        switch (rand.nextInt(3)) {
            case 0: shrine(w, rand, base); break;
            case 1: sanctum(w, rand, base); break;
            default: pyramid(w, rand, base); break;
        }
    }

    /** Open 7x7 shrine: platform, four pillars, a roof ring and a crystal altar. */
    static void shrine(World w, Random rand, BlockPos c) {
        prepare(w, c, 3, 6);
        for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) set(w, c.add(dx, -1, dz), Math.abs(dx) == 3 || Math.abs(dz) == 3 ? ANC : FLOOR);
        for (int[] q : new int[][] { { -3, -3 }, { 3, -3 }, { -3, 3 }, { 3, 3 } })
            for (int y = 0; y < 4; y++) set(w, c.add(q[0], y, q[1]), ANC);
        for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++)
            if (Math.abs(dx) == 3 || Math.abs(dz) == 3 || (dx == 0 && dz == 0)) set(w, c.add(dx, 4, dz), ANC);
        set(w, c, ANC);
        crystal(w, c.up());
        chest(w, rand, c.add(0, 0, 2), EnumFacing.NORTH);
    }

    /** Closed 7x7 sanctum with a doorway, crystal lights in the corners and a chest at the back. */
    static void sanctum(World w, Random rand, BlockPos c) {
        prepare(w, c, 3, 6);
        for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) {
            set(w, c.add(dx, -1, dz), FLOOR);
            boolean wall = Math.abs(dx) == 3 || Math.abs(dz) == 3;
            for (int y = 0; y < 4; y++) if (wall) set(w, c.add(dx, y, dz), ANC);
            set(w, c.add(dx, 4, dz), ANC);
            if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) set(w, c.add(dx, 5, dz), ANC);
        }
        set(w, c.add(0, 0, -3), AIR);
        set(w, c.add(0, 1, -3), AIR);
        for (int[] q : new int[][] { { -2, -2 }, { 2, -2 }, { -2, 2 }, { 2, 2 } }) crystal(w, c.add(q[0], 0, q[1]));
        set(w, c.add(0, 0, 2), ANC);
        chest(w, rand, c.add(0, 1, 2), EnumFacing.NORTH);
    }

    /** 9x9 stepped pyramid with a small chamber, entered by a short tunnel from the north. */
    static void pyramid(World w, Random rand, BlockPos c) {
        prepare(w, c, 4, 5);
        for (int step = 0; step < 4; step++) {
            int r = 4 - step;
            for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) set(w, c.add(dx, step, dz), ANC);
        }
        crystal(w, c.up(4));
        // chamber 3x2x3 and tunnel
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) for (int y = 0; y < 2; y++) set(w, c.add(dx, y, dz), AIR);
        for (int dz = -4; dz <= -2; dz++) { set(w, c.add(0, 0, dz), AIR); set(w, c.add(0, 1, dz), AIR); }
        crystal(w, c.add(-1, 0, -1));
        chest(w, rand, c.add(0, 0, 1), EnumFacing.NORTH);
    }
}
