package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.stargate.ReturnGates;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

/**
 * Frame of the return gate built on P3X-1416. Unbreakable like bedrock: no hardness, huge blast
 * resistance, immovable by pistons, ignored by withers/dragons and explosions, and it never drops
 * or pick-blocks into an item, so survival players cannot obtain it.
 */
public class AncientGateFrameBlock extends Block {
    public AncientGateFrameBlock() {
        super(Material.ROCK, MapColor.CYAN);
        setBlockUnbreakable();          // hardness -1
        setResistance(6000000.0F);
        setSoundType(SoundType.METAL);
        disableStats();
    }

    @Override public EnumPushReaction getMobilityFlag(IBlockState state) { return EnumPushReaction.BLOCK; }
    @Override public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return false; }
    @Override public boolean canDropFromExplosion(Explosion explosion) { return false; }
    @Override public void onBlockExploded(World world, BlockPos pos, Explosion explosion) { /* indestructible */ }
    @Override public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) { return 6000000.0F; }
    @Override public Item getItemDropped(IBlockState state, Random rand, int fortune) { return net.minecraft.init.Items.AIR; }
    @Override public int quantityDropped(Random random) { return 0; }
    @Override public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) { }
    @Override protected boolean canSilkHarvest() { return false; }
    @Override public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) { return false; }
    @Override public ItemStack getItem(World world, BlockPos pos, IBlockState state) { return ItemStack.EMPTY; }
    @Override public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) { return ItemStack.EMPTY; }
    @Override public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        // only creative players (or commands) may remove it
        return player.capabilities.isCreativeMode && super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    /** Placing a frame block back on P3X-1416 re-lights a repaired return gate. */
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote && ModDimensions.isRegistered() && world.provider.getDimension() == ModDimensions.id()
                && world instanceof WorldServer) {
            ReturnGates.relightIfIntact((WorldServer) world);
        }
    }
}
