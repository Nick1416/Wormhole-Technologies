package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.tile.GeneratorTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/** Wormhole Energy Converter. */
public class GeneratorBlock extends LinkedMachineBlock {
    public GeneratorBlock() { super(WormholeTech.GUI_ENERGY_CONVERTER); }

    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new GeneratorTileEntity(); }
}
