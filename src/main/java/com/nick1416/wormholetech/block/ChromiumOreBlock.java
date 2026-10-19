package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

/** Overworld ore with gold's distribution. Drops itself; smelt it into Chromium. */
public class ChromiumOreBlock extends Block {
    public ChromiumOreBlock() {
        super(Material.ROCK);
        setHardness(3.0F);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 2);
    }

    @Override protected boolean canSilkHarvest() { return true; }
}
