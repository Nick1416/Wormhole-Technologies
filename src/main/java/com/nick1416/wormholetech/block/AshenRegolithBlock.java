package com.nick1416.wormholetech.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/** Pale violet-gray surface dust of P3X-1416. Not affected by gravity (keeps cave ceilings stable). */
public class AshenRegolithBlock extends Block {
    public AshenRegolithBlock() {
        super(Material.SAND, MapColor.PURPLE_STAINED_HARDENED_CLAY);
        setHardness(0.5F);
        setSoundType(SoundType.SAND);
        setHarvestLevel("shovel", 0);
        setCreativeTab(net.minecraft.creativetab.CreativeTabs.BUILDING_BLOCKS);
    }
}
