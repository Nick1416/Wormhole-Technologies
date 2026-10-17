package com.nick1416.wormholetech.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class NaquadahOreBlock extends Block {
    public NaquadahOreBlock() {
        super(Material.ROCK);
        setHardness(3.0F);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
    }

    /** Any tool (or a bare hand) harvests it; it always drops itself. Explosions refine it (see DropHandler). */
    @Override
    public boolean canHarvestBlock(net.minecraft.world.IBlockAccess world, net.minecraft.util.math.BlockPos pos, net.minecraft.entity.player.EntityPlayer player) { return true; }
}
