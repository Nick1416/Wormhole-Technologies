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

/**
 * Naturally occurring Naquadah Ore (only generated on P3X-1416). Unlike the crafted Synthetic Ore it
 * always drops Raw Naquadah: 1, more with Fortune (like diamond ore), plus a little XP. Iron pickaxe+.
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

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Registration.RAW_NAQUADAH;
    }

    @Override
    public int quantityDropped(Random random) { return 1; }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0) {
            int bonus = random.nextInt(fortune + 2) - 1;
            return Math.max(1, bonus + 1);
        }
        return 1;
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof net.minecraft.world.World ? ((net.minecraft.world.World) world).rand : new Random();
        return MathHelper.getInt(rand, 1, 4);
    }
}
