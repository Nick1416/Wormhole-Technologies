package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.tile.UnstableWormholeDuplicatorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UnstableWormholeDuplicatorBlock extends Block {
    public UnstableWormholeDuplicatorBlock() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override public boolean hasTileEntity(IBlockState s) { return true; }
    @Override public TileEntity createTileEntity(World w, IBlockState s) { return new UnstableWormholeDuplicatorTileEntity(); }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState s,
                                    EntityPlayer p, EnumHand hand, EnumFacing side,
                                    float hx, float hy, float hz) {
        if (w.isRemote) return true;
        // reuse the same GUI as the regular duplicator
        p.openGui(WormholeTech.INSTANCE, WormholeTech.GUI_WORMHOLE_DUPLICATOR, w, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
