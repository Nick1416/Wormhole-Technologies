package com.nick1416.wormholetech.moon;

import com.nick1416.wormholetech.talvora.VoraniModulatorBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Neutronium Reactor atop a Tal'Vora village spire (replaces the gold village modulator in newly generated
 * villages): the blue Vorani counterpart of the Quantum Field
 * Modulator. Indestructible like the village modulator (no item, no drops, no recipe), but it powers
 * Xenolith drones within droneModulatorRange exactly like a placed modulator. It gives no RF.
 */
public class NeutroniumReactorBlock extends VoraniModulatorBlock {
    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new Tile(); }

    @Override public boolean removedByPlayer(IBlockState s, World w, BlockPos p, net.minecraft.entity.player.EntityPlayer pl, boolean willHarvest) {
        if (!w.isRemote) pl.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation("message.wormholetech.neutronium_reactor"), true);
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random rand) {
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.7, y = pos.getY() + 0.3 + rand.nextDouble() * 0.7, z = pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.7;
            // redstone dust with a colour: (r, g, b) passed as speed; r must be > 0 to be used
            w.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0001D, 0.6D, 1.0D);
        }
        if (rand.nextInt(3) == 0)
            w.spawnParticle(EnumParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 0.95, pos.getZ() + 0.5, 0, 0.03, 0);
    }

    /** Registers the reactor with the drone power tracker while loaded. */
    public static class Tile extends TileEntity {
        @Override public void onLoad() { com.nick1416.wormholetech.drone.ModulatorTracker.add(world, pos); }
        @Override public void invalidate() { super.invalidate(); com.nick1416.wormholetech.drone.ModulatorTracker.remove(world, pos); }
        @Override public void onChunkUnload() { com.nick1416.wormholetech.drone.ModulatorTracker.remove(world, pos); }
    }
}
