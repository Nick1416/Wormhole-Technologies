package com.example.rfgen.block;

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
}
