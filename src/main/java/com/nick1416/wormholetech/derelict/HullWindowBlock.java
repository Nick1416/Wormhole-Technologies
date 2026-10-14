package com.nick1416.wormholetech.derelict;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/** Unbreakable viewport glass: you can see space through it but not get out. */
public class HullWindowBlock extends BlockGlass {
    public HullWindowBlock() {
        super(Material.GLASS, false);
        setBlockUnbreakable();
        setResistance(6_000_000.0F);
        setSoundType(net.minecraft.block.SoundType.GLASS);
    }

    @Override public int quantityDropped(Random random) { return 0; }
    @Override public EnumPushReaction getMobilityFlag(IBlockState state) { return EnumPushReaction.BLOCK; }
    @Override public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return false; }
    @SideOnly(Side.CLIENT) @Override public BlockRenderLayer getBlockLayer() { return BlockRenderLayer.CUTOUT; }
}
