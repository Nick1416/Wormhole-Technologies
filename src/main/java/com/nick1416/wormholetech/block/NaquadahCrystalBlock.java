package com.nick1416.wormholetech.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Decorative glowing crystal cluster from P3X-1416. Not a resource: it drops nothing, only itself
 * with Silk Touch. Needs a solid block below.
 */
public class NaquadahCrystalBlock extends Block {
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.15D, 0.0D, 0.15D, 0.85D, 0.8D, 0.85D);

    public NaquadahCrystalBlock() {
        super(Material.GLASS, MapColor.CYAN);
        setHardness(0.4F);
        setLightLevel(12.0F / 15.0F);
        setSoundType(SoundType.GLASS);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override public int quantityDropped(Random random) { return 0; }
    @Override protected boolean canSilkHarvest() { return true; }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public AxisAlignedBB getBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) { return AABB; }
    @Nullable @Override public AxisAlignedBB getCollisionBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) { return NULL_AABB; }
    @Override public BlockFaceShape getBlockFaceShape(IBlockAccess w, IBlockState s, BlockPos p, EnumFacing f) { return BlockFaceShape.UNDEFINED; }

    @SideOnly(Side.CLIENT)
    @Override public BlockRenderLayer getBlockLayer() { return BlockRenderLayer.CUTOUT; }

    public static boolean canStay(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP);
    }

    @Override public boolean canPlaceBlockAt(World world, BlockPos pos) { return super.canPlaceBlockAt(world, pos) && canStay(world, pos); }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos from) {
        if (!canStay(world, pos)) world.destroyBlock(pos, false);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        // brighter sparkle at night on P3X-1416
        boolean night = com.nick1416.wormholetech.world.P3XWeather.isP3X(world) && com.nick1416.wormholetech.world.P3XPatrols.isNight(world);
        for (int i = night ? 3 : 0; i >= 0; i--) if (night ? rand.nextInt(2) == 0 : rand.nextInt(4) == 0) {
            world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.2 + rand.nextDouble() * 0.6,
                    pos.getY() + 0.3 + rand.nextDouble() * 0.6, pos.getZ() + 0.2 + rand.nextDouble() * 0.6, 0.2D, 0.9D, 0.75D);
        }
    }
}
