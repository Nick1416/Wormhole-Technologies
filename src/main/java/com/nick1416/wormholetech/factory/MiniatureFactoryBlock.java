package com.nick1416.wormholetech.factory;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Miniature Factory: crafts Miniaturization Field recipes from inserted blocks, powered by RF. */
public class MiniatureFactoryBlock extends Block {
    public MiniatureFactoryBlock() {
        super(Material.IRON);
        setHardness(5.0F);
        setResistance(30.0F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 1);
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new MiniatureFactoryTileEntity(); }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing f, float hx, float hy, float hz) {
        if (!w.isRemote) p.openGui(WormholeTech.INSTANCE, WormholeTech.GUI_MINIATURE_FACTORY, w, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World w, BlockPos pos, IBlockState state) {
        TileEntity te = w.getTileEntity(pos);
        if (te instanceof MiniatureFactoryTileEntity) {
            net.minecraftforge.items.IItemHandler h = ((MiniatureFactoryTileEntity) te).items();
            for (int i = 0; i < h.getSlots(); i++) InventoryHelper.spawnItemStack(w, pos.getX(), pos.getY(), pos.getZ(), h.getStackInSlot(i));
        }
        super.breakBlock(w, pos, state);
    }
}
