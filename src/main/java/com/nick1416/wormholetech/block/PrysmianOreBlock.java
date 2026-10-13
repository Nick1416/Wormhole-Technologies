package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

/** Deep P3X-1416 ore (diamond rarity). Drops Prysmian, Fortune adds more like diamond ore; Silk Touch gives the block. */
public class PrysmianOreBlock extends Block {
    public PrysmianOreBlock() {
        super(Material.ROCK, MapColor.PURPLE);
        setHardness(3.0F);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 2);
    }

    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Registration.PRYSMIAN; }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune <= 0) return 1;
        int bonus = random.nextInt(fortune + 2) - 1; // vanilla ore formula
        return Math.max(0, bonus) + 1;
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof net.minecraft.world.World ? ((net.minecraft.world.World) world).rand : new Random();
        return MathHelper.getInt(rand, 3, 7);
    }

    @Override protected boolean canSilkHarvest() { return true; }
}
