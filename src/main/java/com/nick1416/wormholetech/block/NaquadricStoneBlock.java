package com.nick1416.wormholetech.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/** Dark blue-violet stone that fills P3X-1416. Drops itself. Also used for decorative Ancient Stone. */
public class NaquadricStoneBlock extends Block {
    public NaquadricStoneBlock(float hardness, MapColor color) {
        super(Material.ROCK, color);
        setHardness(hardness);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(net.minecraft.creativetab.CreativeTabs.BUILDING_BLOCKS);
    }
}
