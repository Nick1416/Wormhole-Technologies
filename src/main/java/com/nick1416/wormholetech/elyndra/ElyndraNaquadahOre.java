package com.nick1416.wormholetech.elyndra;

import com.nick1416.wormholetech.block.NaturalNaquadahOreBlock;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/** Elyndra's naquadah: crystals growing out of plain stone. Drops normal Naquadah Ore. */
public class ElyndraNaquadahOre extends NaturalNaquadahOreBlock {
    @Override public Item getItemDropped(IBlockState s, Random r, int fortune) { return Registration.ITEM_NATURAL_NAQUADAH_ORE; }
    @Override public boolean canSilkHarvest(World w, BlockPos p, IBlockState s, EntityPlayer pl) { return false; }
}
