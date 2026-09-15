package com.example.rfgen.block;

import com.example.rfgen.tile.GeneratorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GeneratorBlock extends Block {
    public GeneratorBlock() {
        super(Material.IRON);
        setHardness(3.5f);
        setResistance(10f);
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new GeneratorTileEntity(); }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof GeneratorTileEntity)) return false;
        GeneratorTileEntity gen = (GeneratorTileEntity) te;

        ItemStack held = player.getHeldItem(hand);
        boolean sneaking = player.isSneaking();

        if (sneaking) {
            ItemStack out = gen.setDisplayItem(ItemStack.EMPTY);
            if (!out.isEmpty()) player.addItemStackToInventory(out);
            return true;
        }

        if (!held.isEmpty() && gen.getDisplayItem().isEmpty()) {
            gen.setDisplayItem(held.copy().splitStack(1));
            held.shrink(1);
            return true;
        }
        return true;
    }
}
