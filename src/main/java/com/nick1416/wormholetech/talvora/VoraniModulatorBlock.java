package com.nick1416.wormholetech.talvora;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Sacred Quantum Field Modulator in a Vorani village spire. Decorative only: no item form, no drops,
 * no tile entity (so no power), not counted by gates. It cannot be mined (even in creative), pushed,
 * exploded or picked; only commands (/setblock, /fill) can change it.
 */
public class VoraniModulatorBlock extends Block {
    private static final AxisAlignedBB BOX = new AxisAlignedBB(3 / 16D, 0, 3 / 16D, 13 / 16D, 1, 13 / 16D);

    public VoraniModulatorBlock() {
        super(Material.IRON);
        setBlockUnbreakable();
        setResistance(6_000_000.0F);
        setSoundType(SoundType.GLASS);
        setLightLevel(0.85F);
    }

    @Override public boolean removedByPlayer(IBlockState s, World w, BlockPos p, EntityPlayer pl, boolean willHarvest) {
        if (!w.isRemote) pl.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation("message.wormholetech.vorani_modulator"), true);
        return false;
    }
    @Override public boolean canHarvestBlock(IBlockAccess w, BlockPos p, EntityPlayer pl) { return false; }
    @Override public Item getItemDropped(IBlockState s, Random r, int f) { return net.minecraft.init.Items.AIR; }
    @Override public int quantityDropped(Random r) { return 0; }
    @Override public ItemStack getPickBlock(IBlockState s, RayTraceResult t, World w, BlockPos p, EntityPlayer pl) { return ItemStack.EMPTY; }
    @Override public EnumPushReaction getMobilityFlag(IBlockState state) { return EnumPushReaction.BLOCK; }
    @Override public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return false; }
    @Override public boolean canDropFromExplosion(Explosion e) { return false; }
    @Override public void onBlockExploded(World w, BlockPos p, Explosion e) {}
    @Override public float getExplosionResistance(World w, BlockPos p, Entity e, Explosion x) { return 6_000_000.0F; }
    @Override public boolean canCreatureSpawn(IBlockState s, IBlockAccess w, BlockPos p, EntityLiving.SpawnPlacementType t) { return false; }
    @Override public boolean isReplaceable(IBlockAccess w, BlockPos p) { return false; }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random rand) {
        for (int i = 0; i < 2; i++)
            w.spawnParticle(net.minecraft.util.EnumParticleTypes.END_ROD, pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.8,
                    pos.getY() + 0.3 + rand.nextDouble() * 0.6, pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.8,
                    (rand.nextDouble() - 0.5) * 0.02, 0.02, (rand.nextDouble() - 0.5) * 0.02);
    }

    @Override public boolean isOpaqueCube(IBlockState s) { return false; }
    @Override public boolean isFullCube(IBlockState s) { return false; }
    @Override public AxisAlignedBB getBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) { return BOX; }
    @SideOnly(Side.CLIENT) @Override public BlockRenderLayer getBlockLayer() { return BlockRenderLayer.CUTOUT; }
}
