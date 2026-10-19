package com.nick1416.wormholetech.fluid;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Hydrothermal springs on the naquadah world: in about 1 of p3xHydrothermalSpringChance chunks a small,
 * roughly round pool (radius 1-3, 1-2 deep) of Hydrothermal Fluid sits in the ash, lined with
 * Naquadric Stone and a few exposed Naquadah Crystals at the rim.
 */
public class HydrothermalSprings implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator gen, IChunkProvider prov) {
        if (ModConfig.p3xHydrothermalSpringChance <= 0) return;
        if (!ModDimensions.isRegistered() || world.provider.getDimension() != ModDimensions.id()) return;
        if (random.nextInt(ModConfig.p3xHydrothermalSpringChance) != 0) return;
        // centre inside the populated area (+8) so the pool never reaches ungenerated chunks
        int cx = chunkX * 16 + 8 + 4 + random.nextInt(8), cz = chunkZ * 16 + 8 + 4 + random.nextInt(8);
        int r = 1 + random.nextInt(3);
        BlockPos top = world.getHeight(new BlockPos(cx, 0, cz)).down();
        if (top.getY() < 40 || !surface(world.getBlockState(top))) return;
        // the rim must be roughly flat ground
        int y = top.getY();
        for (int dx = -r - 1; dx <= r + 1; dx++)
            for (int dz = -r - 1; dz <= r + 1; dz++) {
                int h = world.getHeight(new BlockPos(cx + dx, 0, cz + dz)).getY() - 1;
                if (Math.abs(h - y) > 1) return;
            }
        IBlockState fluid = HydrothermalFluid.block().getDefaultState();
        IBlockState rim = Registration.NAQUADRIC_STONE.getDefaultState();
        int depth = 1 + random.nextInt(2);
        for (int dx = -r - 1; dx <= r + 1; dx++)
            for (int dz = -r - 1; dz <= r + 1; dz++) {
                double d = Math.sqrt(dx * dx + dz * dz) + random.nextDouble() * 0.4;
                BlockPos col = new BlockPos(cx + dx, y, cz + dz);
                if (d <= r) {
                    int deep = d <= r - 1 ? depth : 1;
                    for (int k = 1; k <= 3; k++) world.setBlockState(col.up(k), Blocks.AIR.getDefaultState(), 2);
                    for (int k = 0; k < deep; k++) world.setBlockState(col.down(k), fluid, 2);
                    world.setBlockState(col.down(deep), rim, 2);
                } else if (d <= r + 1.2) {
                    for (int k = 1; k <= 3; k++) world.setBlockState(col.up(k), Blocks.AIR.getDefaultState(), 2);
                    for (int k = 0; k <= depth; k++) {
                        IBlockState s = world.getBlockState(col.down(k));
                        if (!s.getMaterial().isSolid() || s.getMaterial() == Material.SAND) world.setBlockState(col.down(k), rim, 2);
                    }
                    world.setBlockState(col, rim, 2);
                    if (random.nextInt(9) == 0 && world.isAirBlock(col.up()))
                        world.setBlockState(col.up(), Registration.NAQUADAH_CRYSTAL.getDefaultState(), 2);
                }
            }
        // every fluid cell must be enclosed so the spring never spills
        for (int dx = -r; dx <= r; dx++)
            for (int dz = -r; dz <= r; dz++)
                for (int k = 0; k < depth; k++) {
                    BlockPos p = new BlockPos(cx + dx, y - k, cz + dz);
                    if (world.getBlockState(p).getBlock() != fluid.getBlock()) continue;
                    for (net.minecraft.util.EnumFacing f : net.minecraft.util.EnumFacing.HORIZONTALS) {
                        BlockPos n = p.offset(f);
                        IBlockState s = world.getBlockState(n);
                        if (s.getBlock() != fluid.getBlock() && !s.getMaterial().isSolid()) world.setBlockState(n, rim, 2);
                    }
                    BlockPos below = p.down();
                    IBlockState sb = world.getBlockState(below);
                    if (sb.getBlock() != fluid.getBlock() && !sb.getMaterial().isSolid()) world.setBlockState(below, rim, 2);
                }
    }

    private static boolean surface(IBlockState s) {
        return s.getBlock() == Registration.ASHEN_REGOLITH || s.getBlock() == Registration.NAQUADRIC_STONE
                || s.getBlock() == Blocks.GRAVEL || s.getBlock() == Blocks.STONE;
    }
}
