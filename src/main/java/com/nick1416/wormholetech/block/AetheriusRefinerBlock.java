package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.tile.AetheriusRefinerTileEntity;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

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
        player.openGui(WormholeTech.INSTANCE, WormholeTech.GUI_HIGH_ENERGY_REFINER, world, pos.getX(), pos.getY(), pos.getZ());
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

    /** A few green sparks while refining, so a running machine is visible from outside. */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof AetheriusRefinerTileEntity) || !((AetheriusRefinerTileEntity) te).isActive()) return;
        for (int i = 0; i < 3; i++) {
            EnumFacing face = EnumFacing.HORIZONTALS[rand.nextInt(4)];
            double x = pos.getX() + 0.5 + face.getFrontOffsetX() * 0.52 + (face.getFrontOffsetX() == 0 ? (rand.nextDouble() - 0.5) * 0.8 : 0);
            double y = pos.getY() + 0.2 + rand.nextDouble() * 0.7;
            double z = pos.getZ() + 0.5 + face.getFrontOffsetZ() * 0.52 + (face.getFrontOffsetZ() == 0 ? (rand.nextDouble() - 0.5) * 0.8 : 0);
            // REDSTONE particle: speed args are RGB (red 0 would be treated as 1)
            world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.15, 0.95, 0.55);
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        // Contents stay in the dropped item NBT (getDrops); do not spill.
        super.breakBlock(world, pos, state);
    }
}
