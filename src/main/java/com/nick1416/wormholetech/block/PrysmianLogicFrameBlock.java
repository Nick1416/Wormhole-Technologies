package com.nick1416.wormholetech.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/** Advanced Prysmian structural block (Prysmian + Quantum Circuits). */
public class PrysmianLogicFrameBlock extends Block {
    public PrysmianLogicFrameBlock() {
        super(Material.IRON);
        setHardness(4.0F);
        setResistance(8.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
}
