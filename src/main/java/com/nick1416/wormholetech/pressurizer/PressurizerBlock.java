package com.nick1416.wormholetech.pressurizer;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

public class PressurizerBlock extends Block {
    public PressurizerBlock() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(20.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new PressurizerTileEntity(); }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing side, float hx, float hy, float hz) {
        if (!(world.getTileEntity(pos) instanceof PressurizerTileEntity)) return false;
        // A water bucket (or other container) in hand pours straight into the tank.
        if (FluidUtil.getFluidHandler(player.getHeldItem(hand)) != null) {
            if (!world.isRemote) FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
            return true;
        }
        if (!world.isRemote) player.openGui(WormholeTech.INSTANCE, WormholeTech.GUI_PRESSURIZER, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof PressurizerTileEntity) ((PressurizerTileEntity) te).dropContents(world, pos);
        super.breakBlock(world, pos, state);
    }
}
