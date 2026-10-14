package com.nick1416.wormholetech.drone;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Miner Drone item (meta = miner {@link DroneProgram}); placing it makes a Miner Drone block. */
public class ItemMinerDrone extends ItemBlock {
    public ItemMinerDrone(Block b) { super(b); setHasSubtypes(true); setMaxDamage(0); }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) for (int i = 0; i < DroneProgram.MINERS; i++) items.add(new ItemStack(this, 1, i));
    }

    @Override
    public String getItemStackDisplayName(ItemStack s) {
        return net.minecraft.util.text.translation.I18n.translateToLocalFormatted("item.wormholetech.miner_drone.name", DroneNames.program(DroneProgram.byMeta(s.getMetadata())));
    }

    @Override
    public boolean placeBlockAt(ItemStack s, EntityPlayer p, World w, BlockPos pos, EnumFacing side, float hx, float hy, float hz, IBlockState state) {
        if (!super.placeBlockAt(s, p, w, pos, side, hx, hy, hz, state)) return false;
        TileEntity te = w.getTileEntity(pos);
        if (te instanceof MinerDroneTileEntity) ((MinerDroneTileEntity) te).program = Math.min(DroneProgram.MINERS - 1, s.getMetadata());
        return true;
    }
}
