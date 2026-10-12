package com.nick1416.wormholetech.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * Naturally occurring Naquadah Ore (only generated on P3X-1416). Drops itself; smelt it in a
 * furnace for Refined Naquadah. Iron pickaxe or better.
 */
public class NaturalNaquadahOreBlock extends Block {
    public NaturalNaquadahOreBlock() {
        super(Material.ROCK, MapColor.BLUE);
        setHardness(3.5F);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
        setLightLevel(3.0F / 15.0F); // faint glow of the flecks
        setHarvestLevel("pickaxe", 2);
    }
}
