package com.example.rfgen.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class CompressedNaquadahBlock extends Block {
    public CompressedNaquadahBlock() {
        super(Material.ROCK);
        setHardness(50.0F);
        setResistance(2000.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativeTabs.BUILDINGBLOCKS);
    }
}
