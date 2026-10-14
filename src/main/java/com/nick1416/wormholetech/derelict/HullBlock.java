package com.nick1416.wormholetech.derelict;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

/** Ship hull, bulkheads, lamps and cradle machinery: unbreakable like bedrock, blast-proof, immovable. */
public class HullBlock extends Block {
    public HullBlock(MapColor color, float light) {
        super(Material.IRON, color);
        setBlockUnbreakable();
        setResistance(6_000_000.0F);
        setSoundType(SoundType.METAL);
        setLightLevel(light);
    }

    @Override public int quantityDropped(Random random) { return 0; }
    @Override public EnumPushReaction getMobilityFlag(IBlockState state) { return EnumPushReaction.BLOCK; }
    @Override public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return false; }
}
