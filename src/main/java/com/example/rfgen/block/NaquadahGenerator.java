package com.example.rfgen.block;

import com.example.rfgen.tile.NaquadahGeneratorTileEntity;
import com.example.rfgen.util.TimedBlockItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class NaquadahGenerator extends Block {

    public NaquadahGenerator() {
        super(Material.IRON);
        setHardness(3.5F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new NaquadahGeneratorTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                         IBlockState state, int fortune) {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof NaquadahGeneratorTileEntity) {
            NaquadahGeneratorTileEntity gen = (NaquadahGeneratorTileEntity) te;
            TimedBlockItems.writeAgeEnergy(stack, gen.getAgeTicks(), gen.getEnergyStored());
        }
        drops.add(stack);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof NaquadahGeneratorTileEntity) {
            NaquadahGeneratorTileEntity gen = (NaquadahGeneratorTileEntity) te;
            TimedBlockItems.writeAgeEnergy(stack, gen.getAgeTicks(), gen.getEnergyStored());
        }
        return stack;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote) return;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof NaquadahGeneratorTileEntity) {
            NaquadahGeneratorTileEntity gen = (NaquadahGeneratorTileEntity) te;
            gen.setAgeTicks(TimedBlockItems.readAge(stack));
            gen.setEnergyStored(TimedBlockItems.readEnergy(stack));
        }
    }
}
