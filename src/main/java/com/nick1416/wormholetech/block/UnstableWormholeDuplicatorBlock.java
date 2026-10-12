package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.tile.UnstableWormholeDuplicatorTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class UnstableWormholeDuplicatorBlock extends LinkedMachineBlock {
    public UnstableWormholeDuplicatorBlock() { super(WormholeTech.GUI_WORMHOLE_DUPLICATOR); }

    @Override public TileEntity createTileEntity(World w, IBlockState s) { return new UnstableWormholeDuplicatorTileEntity(); }

    /** Duplicators only exist in mode=cc. */
    @Override public boolean isDisabled() { return !com.nick1416.wormholetech.ModConfig.isCC(); }
}
