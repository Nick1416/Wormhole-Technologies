package com.example.rfgen.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class UnstableNaquadahBlock extends Block {
    public UnstableNaquadahBlock() {
        super(Material.ROCK);
        setHardness(3.0F);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
    }
}
