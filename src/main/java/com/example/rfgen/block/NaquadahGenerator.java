package com.example.rfgen.block;

import com.example.rfgen.tile.NaquadahGeneratorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class NaquadahGenerator extends Block {

    public NaquadahGenerator() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    // It has a tile entity
    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new NaquadahGeneratorTileEntity();
    }

    // No GUI / no item input: ignore right-clicks
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        return false; // PASS (do nothing)
    }

    // Standard model
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
