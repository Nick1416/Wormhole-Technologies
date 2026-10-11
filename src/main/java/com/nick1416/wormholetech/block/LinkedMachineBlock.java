package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.tile.LinkedMachineTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Machine block with a wormhole connection. Right-click opens its GUI; breaking it keeps the
 * inventory and connection state in the dropped item (restored on placement).
 */
public abstract class LinkedMachineBlock extends Block {

    private final int guiId;

    protected LinkedMachineBlock(int guiId) {
        super(Material.IRON);
        this.guiId = guiId;
        setHardness(3.5F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if (!(world.getTileEntity(pos) instanceof LinkedMachineTileEntity)) return false;
        player.openGui(WormholeTech.INSTANCE, guiId, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    private ItemStack dropFor(@Nullable TileEntity te) {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
        if (te instanceof LinkedMachineTileEntity) ((LinkedMachineTileEntity) te).writeToItem(stack);
        return stack;
    }

    /** Explosions and other non-player breaks (the tile still exists here). */
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(dropFor(world.getTileEntity(pos)));
    }

    /** Player harvest: the tile is already removed from the world, so use the one passed in. */
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state,
                             @Nullable TileEntity te, ItemStack tool) {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (!world.isRemote) spawnAsEntity(world, pos, dropFor(te));
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote) return;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof LinkedMachineTileEntity) ((LinkedMachineTileEntity) te).readFromItem(stack);
    }
}
