package com.nick1416.wormholetech.knowledge;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/** Repository of Knowledge: a technological enchanting table. Right-click opens the knowledge GUI. */
public class RepositoryBlock extends Block implements ITileEntityProvider {
    private static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 0.75, 1);

    public RepositoryBlock() {
        super(Material.IRON);
        setHardness(4.0F);
        setResistance(1200.0F);
        setSoundType(SoundType.METAL);
        setLightLevel(0.6F);
        setHarvestLevel("pickaxe", 1);
    }

    @Override public TileEntity createNewTileEntity(World w, int meta) { return new RepositoryTileEntity(); }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState s, EntityPlayer p, EnumHand hand, EnumFacing f, float x, float y, float z) {
        if (!w.isRemote && p instanceof EntityPlayerMP)
            com.nick1416.wormholetech.mode.ModeNetwork.CHANNEL.sendTo(new KnowledgeMessage(KnowledgeData.get(w).clientView(p.getUniqueID())), (EntityPlayerMP) p);
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState s, World w, BlockPos pos, Random r) {
        for (int i = 0; i < 2; i++)
            w.spawnParticle(net.minecraft.util.EnumParticleTypes.ENCHANTMENT_TABLE, pos.getX() + 0.5 + (r.nextDouble() - 0.5) * 1.6,
                    pos.getY() + 1.6, pos.getZ() + 0.5 + (r.nextDouble() - 0.5) * 1.6, 0, -0.6, 0);
    }

    @Override public boolean isOpaqueCube(IBlockState s) { return false; }
    @Override public boolean isFullCube(IBlockState s) { return false; }
    @Override public AxisAlignedBB getBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) { return BOX; }
    @SideOnly(Side.CLIENT) @Override public BlockRenderLayer getBlockLayer() { return BlockRenderLayer.CUTOUT; }
}
