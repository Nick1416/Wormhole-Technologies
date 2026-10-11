package com.nick1416.wormholetech.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/** Block of Refined Naquadah: storage block and wormhole connection fuel. */
public class RefinedNaquadahBlock extends Block {
    public RefinedNaquadahBlock() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(net.minecraft.creativetab.CreativeTabs.BUILDING_BLOCKS);
    }
}
