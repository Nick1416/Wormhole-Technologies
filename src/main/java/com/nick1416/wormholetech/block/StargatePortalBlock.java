package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.stargate.StargateTravel;
import com.nick1416.wormholetech.stargate.TitaniumFrame;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Event horizon filling an open gate. Players walking in travel to P3X-1416 (or back home from
 * the return gate there). Like a Nether portal it collapses when a neighbour in its plane is
 * neither portal nor frame.
 */
public class StargatePortalBlock extends Block {

    public static final PropertyEnum<EnumFacing.Axis> AXIS =
            PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
    private static final AxisAlignedBB X_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
    private static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);

    public StargatePortalBlock() {
        super(Material.PORTAL);
        setBlockUnbreakable();
        setResistance(6000000.0F);
        setLightLevel(0.75F);
        setSoundType(SoundType.GLASS);
        setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(AXIS) == EnumFacing.Axis.X ? X_AABB : Z_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return NULL_AABB;
    }

    @Override public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return false; }
    @Override public void onBlockExploded(World world, BlockPos pos, net.minecraft.world.Explosion explosion) { /* indestructible */ }
    @Override public net.minecraft.block.material.EnumPushReaction getMobilityFlag(IBlockState state) { return net.minecraft.block.material.EnumPushReaction.BLOCK; }
    @Override public ItemStack getPickBlock(IBlockState s, net.minecraft.util.math.RayTraceResult t, World w, BlockPos p, net.minecraft.entity.player.EntityPlayer pl) { return ItemStack.EMPTY; }
    @Override public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, net.minecraft.entity.player.EntityPlayer player, boolean willHarvest) {
        return player.capabilities.isCreativeMode && super.removedByPlayer(state, world, pos, player, willHarvest);
    }
    @Override public boolean isOpaqueCube(IBlockState state) { return false; }
    @Override public boolean isFullCube(IBlockState state) { return false; }
    @Override public int quantityDropped(Random random) { return 0; }
    @Override public ItemStack getItem(World w, BlockPos p, IBlockState s) { return ItemStack.EMPTY; }
    @Override public BlockFaceShape getBlockFaceShape(IBlockAccess w, IBlockState s, BlockPos p, EnumFacing f) { return BlockFaceShape.UNDEFINED; }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() { return BlockRenderLayer.TRANSLUCENT; }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos from) {
        EnumFacing.Axis axis = state.getValue(AXIS);
        EnumFacing side = axis == EnumFacing.Axis.X ? EnumFacing.EAST : EnumFacing.SOUTH;
        for (EnumFacing f : new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN, side, side.getOpposite() }) {
            IBlockState n = world.getBlockState(pos.offset(f));
            if (n.getBlock() != this && !TitaniumFrame.isAnyFrame(n)) {
                world.setBlockToAir(pos);
                return;
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (world.isRemote || !(entity instanceof EntityPlayerMP)) return;
        if (entity.isRiding() || entity.isBeingRidden() || entity.timeUntilPortal > 0) return;
        StargateTravel.request((EntityPlayerMP) entity, world, pos);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(120) == 0) {
            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT,
                    SoundCategory.BLOCKS, 0.35F, rand.nextFloat() * 0.3F + 1.3F, false);
        }
        boolean x = state.getValue(AXIS) == EnumFacing.Axis.X;
        for (int i = 0; i < 3; i++) {
            double px = pos.getX() + (x ? rand.nextDouble() : 0.5 + (rand.nextDouble() - 0.5) * 0.6);
            double py = pos.getY() + rand.nextDouble();
            double pz = pos.getZ() + (x ? 0.5 + (rand.nextDouble() - 0.5) * 0.6 : rand.nextDouble());
            // REDSTONE dust: the speed arguments are the colour (teal)
            world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, 0.15D, 0.85D, 0.8D);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS) == EnumFacing.Axis.Z ? 2 : 1;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }
}
