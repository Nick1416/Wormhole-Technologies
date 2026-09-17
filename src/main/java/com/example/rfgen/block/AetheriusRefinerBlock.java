package com.example.rfgen.block;

import com.example.rfgen.registry.Registration;
import com.example.rfgen.tile.AetheriusRefinerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AetheriusRefinerBlock extends Block {
    public AetheriusRefinerBlock() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(20.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new AetheriusRefinerTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof AetheriusRefinerTileEntity)) return false;
        AetheriusRefinerTileEntity refiner = (AetheriusRefinerTileEntity) te;
        ItemStack held = player.getHeldItem(hand);

        if (player.isSneaking()) {
            ItemStack out = refiner.ejectAll();
            if (!out.isEmpty()) {
                if (!player.inventory.addItemStackToInventory(out)) player.dropItem(out, false);
            }
            return true;
        }

        if (!held.isEmpty() && held.getItem() == Item.getItemFromBlock(Registration.COMPRESSED_NAQUADAH)) {
            if (refiner.insertInput(held)) {
                player.sendStatusMessage(new TextComponentTranslation("message.rfgen.refiner.inserted"), true);
            }
            return true;
        }

        ItemStack product = refiner.takeOutput();
        if (!product.isEmpty()) {
            if (!player.inventory.addItemStackToInventory(product)) player.dropItem(product, false);
            player.sendStatusMessage(new TextComponentTranslation("message.rfgen.refiner.took"), true);
            return true;
        }

        int pct = refiner.getProgressPercent();
        player.sendStatusMessage(new TextComponentTranslation("message.rfgen.refiner.status", pct, refiner.getEnergyStored()), true);
        return true;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                         IBlockState state, int fortune) {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof AetheriusRefinerTileEntity) {
            ((AetheriusRefinerTileEntity) te).writeToItem(stack);
        }
        drops.add(stack);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof AetheriusRefinerTileEntity) {
            ((AetheriusRefinerTileEntity) te).writeToItem(stack);
        }
        return stack;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote) return;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof AetheriusRefinerTileEntity) {
            ((AetheriusRefinerTileEntity) te).readFromItem(stack);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        // Contents stay in the dropped item NBT (getDrops); do not spill.
        super.breakBlock(world, pos, state);
    }
}
