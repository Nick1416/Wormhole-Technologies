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

    /** At night on P3X-1416, exposed surface ore glints with naquadah light. */
    @net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
    @Override
    public void randomDisplayTick(net.minecraft.block.state.IBlockState state, net.minecraft.world.World world,
                                  net.minecraft.util.math.BlockPos pos, java.util.Random rand) {
        if (rand.nextInt(2) != 0 || !com.nick1416.wormholetech.world.P3XWeather.isP3X(world)
                || !com.nick1416.wormholetech.world.P3XPatrols.isNight(world)) return;
        net.minecraft.util.math.BlockPos up = pos.up();
        if (!world.isAirBlock(up) || !world.canSeeSky(up)) return;
        world.spawnParticle(net.minecraft.util.EnumParticleTypes.REDSTONE, pos.getX() + rand.nextDouble(), pos.getY() + 1.05,
                pos.getZ() + rand.nextDouble(), 0.25D, 0.55D, 1.0D);
    }
}
