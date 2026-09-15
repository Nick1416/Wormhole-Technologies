package com.example.rfgen.block;

import com.example.rfgen.RFGen;
import com.example.rfgen.tile.WormholeDuplicatorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WormholeDuplicatorBlock extends Block {
    public WormholeDuplicatorBlock() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World w, IBlockState s) { return new WormholeDuplicatorTileEntity(); }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        player.openGui(RFGen.INSTANCE, RFGen.GUI_WORMHOLE_DUPLICATOR, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
