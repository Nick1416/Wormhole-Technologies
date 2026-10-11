package com.nick1416.wormholetech.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import com.nick1416.wormholetech.stargate.ReturnGates;
import com.nick1416.wormholetech.world.ModDimensions;

/** Frame of the return gate built on P3X-1416. Hard, but breakable. */
public class AncientGateFrameBlock extends Block {
    public AncientGateFrameBlock() {
        super(Material.IRON, MapColor.CYAN);
        setHardness(25.0F);
        setResistance(1200.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 2);
    }

    /** Placing a frame block back on P3X-1416 re-lights a repaired return gate. */
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote && ModDimensions.isRegistered() && world.provider.getDimension() == ModDimensions.id()
                && world instanceof WorldServer) {
            ReturnGates.relightIfIntact((WorldServer) world);
        }
    }
}
