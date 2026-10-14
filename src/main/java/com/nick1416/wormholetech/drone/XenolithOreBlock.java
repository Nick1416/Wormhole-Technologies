package com.nick1416.wormholetech.drone;

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

/** Xenolith Ore under the Tal'Vora sand. Drops 1-2 Raw Xenolith, Fortune adds more; Silk Touch gives the block. */
public class XenolithOreBlock extends Block {
    public XenolithOreBlock() {
        super(Material.ROCK, MapColor.CYAN);
        setHardness(3.0F);
        setResistance(5.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 1);
    }

    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Registration.RAW_XENOLITH; }
    @Override public int quantityDropped(Random r) { return 1 + r.nextInt(2); }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        int base = quantityDropped(random);
        if (fortune <= 0) return base;
        int bonus = random.nextInt(fortune + 2) - 1;
        return base * (Math.max(0, bonus) + 1);
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof net.minecraft.world.World ? ((net.minecraft.world.World) world).rand : new Random();
        return MathHelper.getInt(rand, 1, 4);
    }

    @Override protected boolean canSilkHarvest() { return true; }
}
