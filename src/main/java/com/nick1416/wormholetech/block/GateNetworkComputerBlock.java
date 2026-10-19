package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.tile.DialingComputerTileEntity;
import com.nick1416.wormholetech.tile.GateNetworkComputerTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * A Dialing Computer with a Time Dilated Chip in its upgrade slot. It is made by inserting the chip;
 * broken, it drops a Dialing Computer (the chip drops with the other slot contents).
 */
public class GateNetworkComputerBlock extends DialingComputerBlock {
    public GateNetworkComputerBlock() { setCreativeTab(null); }

    @Override protected DialingComputerTileEntity newTile() { return new GateNetworkComputerTileEntity(); }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Registration.ITEM_DIALING_COMPUTER; }

    @Override
    public ItemStack getPickBlock(IBlockState state, net.minecraft.util.math.RayTraceResult target, World world, BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
        return new ItemStack(Registration.ITEM_DIALING_COMPUTER);
    }

    /** A Gate Network Computer item from before the upgrade slot existed comes with its chip. */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileEntity te = world.getTileEntity(pos);
        if (!world.isRemote && te instanceof DialingComputerTileEntity) {
            DialingComputerTileEntity dc = (DialingComputerTileEntity) te;
            if (dc.items().getStackInSlot(DialingComputerTileEntity.SLOT_UPGRADE).isEmpty())
                dc.items().setStackInSlot(DialingComputerTileEntity.SLOT_UPGRADE, new ItemStack(Registration.TIME_DILATED_CHIP));
        }
    }
}
