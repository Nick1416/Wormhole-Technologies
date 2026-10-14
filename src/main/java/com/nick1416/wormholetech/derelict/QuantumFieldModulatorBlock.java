package com.nick1416.wormholetech.derelict;

import com.nick1416.wormholetech.entity.EntityGiantEmeraldSentinel;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Quantum Field Modulator. Cannot be crafted; no GUI, no item I/O.
 * In a modulator room it starts LOCKED (unbreakable) and is released when the room's Giant Emerald
 * Sentinel dies (the Sentinel unlocks modulators within {@link #RELEASE_RANGE} on death). A random
 * tick also releases it if no living Giant Sentinel is within that range any more (e.g. removed by a
 * command). Once unlocked it is mined like a normal block and drops itself.
 * TODO(modulator-use): give the Modulator a purpose (power source / gate upgrade).
 */
public class QuantumFieldModulatorBlock extends Block {
    public static final PropertyBool LOCKED = PropertyBool.create("locked");
    public static final int RELEASE_RANGE = 24;
    private static final AxisAlignedBB BOX = new AxisAlignedBB(3 / 16D, 0, 3 / 16D, 13 / 16D, 1, 13 / 16D);

    public QuantumFieldModulatorBlock() {
        super(Material.IRON);
        setHardness(3.0F);
        setResistance(6_000_000.0F);
        setSoundType(SoundType.GLASS);
        setLightLevel(0.85F);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(LOCKED, false));
    }

    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, LOCKED); }
    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(LOCKED, (meta & 1) != 0); }
    @Override public int getMetaFromState(IBlockState s) { return s.getValue(LOCKED) ? 1 : 0; }
    @Override public int damageDropped(IBlockState state) { return 0; }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        return state.getValue(LOCKED) ? -1.0F : super.getBlockHardness(state, world, pos);
    }

    @Override public EnumPushReaction getMobilityFlag(IBlockState state) { return EnumPushReaction.BLOCK; }
    @Override public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) { return false; }

    public static boolean guarded(World w, BlockPos pos) {
        return !w.getEntitiesWithinAABB(EntityGiantEmeraldSentinel.class, new AxisAlignedBB(pos).grow(RELEASE_RANGE),
                EntitySelectors.IS_ALIVE::apply).isEmpty();
    }

    @Override
    public void randomTick(World w, BlockPos pos, IBlockState state, Random rand) {
        if (!w.isRemote && state.getValue(LOCKED) && !guarded(w, pos)) unlock(w, pos);
    }

    public static void unlock(World w, BlockPos pos) {
        IBlockState s = w.getBlockState(pos);
        if (s.getBlock() instanceof QuantumFieldModulatorBlock && s.getValue(LOCKED)) {
            w.setBlockState(pos, s.withProperty(LOCKED, false), 3);
            w.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, net.minecraft.util.SoundCategory.BLOCKS, 1.0F, 1.6F);
        }
    }

    @Override
    public void onBlockClicked(World w, BlockPos pos, EntityPlayer player) {
        if (!w.isRemote && w.getBlockState(pos).getValue(LOCKED))
            player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.modulator.locked"), true);
    }

    /** A few sparks so the room can be found: client only, within 24 blocks, rate-limited by display ticks. */
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random rand) {
        EntityPlayer pl = net.minecraft.client.Minecraft.getMinecraft().player;
        if (pl == null || pl.getDistanceSqToCenter(pos) > 24 * 24) return;
        for (int i = 0; i < 3; i++)
            w.spawnParticle(net.minecraft.util.EnumParticleTypes.END_ROD, pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.8,
                    pos.getY() + 0.3 + rand.nextDouble() * 0.6, pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.8,
                    (rand.nextDouble() - 0.5) * 0.02, 0.02, (rand.nextDouble() - 0.5) * 0.02);
    }

    /** Only a free (unlocked or player-placed) modulator produces energy. */
    @Override public boolean hasTileEntity(IBlockState state) { return !state.getValue(LOCKED); }
    @Override public net.minecraft.tileentity.TileEntity createTileEntity(World world, IBlockState state) { return new QuantumFieldModulatorTileEntity(); }

    @Override public boolean isOpaqueCube(IBlockState s) { return false; }
    @Override public boolean isFullCube(IBlockState s) { return false; }
    @Override public AxisAlignedBB getBoundingBox(IBlockState s, IBlockAccess w, BlockPos p) { return BOX; }
    @SideOnly(Side.CLIENT) @Override public BlockRenderLayer getBlockLayer() { return BlockRenderLayer.CUTOUT; }
}
