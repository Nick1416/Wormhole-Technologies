package com.nick1416.wormholetech.drone;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/** Placed Miner Drone. Hovers next to a storage block and slowly fills it with its mineral's ores. */
public class MinerDroneBlock extends Block implements ITileEntityProvider {
    private static final AxisAlignedBB BOX = new AxisAlignedBB(2 / 16D, 3 / 16D, 2 / 16D, 14 / 16D, 12 / 16D, 14 / 16D);

    public MinerDroneBlock() {
        super(Material.IRON);
        setHardness(1.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setLightLevel(0.4F);
    }

    @Override public TileEntity createNewTileEntity(World w, int meta) { return new MinerDroneTileEntity(); }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState s, EntityPlayer p, EnumHand hand, EnumFacing f, float x, float y, float z) {
        if (!w.isRemote) {
            TileEntity te = w.getTileEntity(pos);
            if (te instanceof MinerDroneTileEntity) p.sendStatusMessage(((MinerDroneTileEntity) te).statusText(), true);
        }
        return true;
    }

    private ItemStack dropOf(IBlockAccess w, BlockPos pos) {
        TileEntity te = w.getTileEntity(pos);
        ItemStack s = new ItemStack(Registration.ITEM_MINER_DRONE, 1, metaAt(w, pos));
        return te instanceof MinerDroneTileEntity ? DroneCharge.with(s, ((MinerDroneTileEntity) te).charge) : s;
    }

    private int metaAt(IBlockAccess w, BlockPos pos) {
        TileEntity te = w.getTileEntity(pos);
        return te instanceof MinerDroneTileEntity ? ((MinerDroneTileEntity) te).program : 0;
    }

    @Override public void getDrops(NonNullList<ItemStack> drops, IBlockAccess w, BlockPos pos, IBlockState s, int fortune) {
        drops.add(dropOf(w, pos));
    }
    @Override public ItemStack getPickBlock(IBlockState s, RayTraceResult t, World w, BlockPos pos, EntityPlayer p) {
        return dropOf(w, pos);
    }
    /** Keep the tile entity until drops are collected. */
    @Override public boolean removedByPlayer(IBlockState s, World w, BlockPos pos, EntityPlayer p, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(s, w, pos, p, false);
    }
    @Override public void harvestBlock(World w, EntityPlayer p, BlockPos pos, IBlockState s, TileEntity te, ItemStack tool) {
        super.harvestBlock(w, p, pos, s, te, tool);
        w.setBlockToAir(pos);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState s, World w, BlockPos pos, Random r) {
        TileEntity te = w.getTileEntity(pos);
        if (!(te instanceof MinerDroneTileEntity) || ((MinerDroneTileEntity) te).status != MinerDroneTileEntity.WORKING) return;
        for (int i = 0; i < 4; i++)
            w.spawnParticle(net.minecraft.util.EnumParticleTypes.END_ROD, pos.getX() + 0.5 + (r.nextDouble() - 0.5) * 0.3,
                    pos.getY() + 0.15 - i * 0.12, pos.getZ() + 0.5 + (r.nextDouble() - 0.5) * 0.3, 0, -0.03, 0);
    }

    @Override public boolean isOpaqueCube(IBlockState s) { return false; }
    @Override public boolean isFullCube(IBlockState s) { return false; }
    @Override public AxisAlignedBB getBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) { return BOX; }
    @SideOnly(Side.CLIENT) @Override public BlockRenderLayer getBlockLayer() { return BlockRenderLayer.CUTOUT; }
}
