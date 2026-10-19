package com.nick1416.wormholetech.fluid;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/** Hydrothermal Fluid: hot, mineral-rich water from the springs of the naquadah world. Has a universal bucket. */
public final class HydrothermalFluid {
    private HydrothermalFluid() {}

    public static final String NAME = "hydrothermal_fluid";
    private static Fluid fluid;
    private static Block block;

    /** Registers the fluid (once) and its bucket; call before the fluid block is created. */
    public static Fluid fluid() {
        if (fluid == null) {
            Fluid f = new Fluid(NAME,
                    new ResourceLocation(WormholeTech.MODID, "blocks/hydrothermal_fluid_still"),
                    new ResourceLocation(WormholeTech.MODID, "blocks/hydrothermal_fluid_flow"))
                    .setUnlocalizedName(WormholeTech.MODID + "." + NAME)
                    .setTemperature(420).setLuminosity(4).setViscosity(1200).setDensity(1100);
            if (!FluidRegistry.registerFluid(f)) f = FluidRegistry.getFluid(NAME); // another mod's fluid with this name wins
            FluidRegistry.addBucketForFluid(f);
            fluid = f;
        }
        return fluid;
    }

    public static Block block() {
        if (block == null) {
            block = new Block(fluid());
            block.setRegistryName(WormholeTech.MODID, NAME);
            block.setUnlocalizedName(WormholeTech.MODID + "." + NAME);
        }
        return block;
    }

    public static boolean is(FluidStack s) { return s != null && s.getFluid() != null && NAME.equals(s.getFluid().getName()); }

    public static FluidStack bucket() { return new FluidStack(fluid(), Fluid.BUCKET_VOLUME); }

    public static class Block extends BlockFluidClassic {
        Block(Fluid f) {
            super(f, Material.WATER);
            setLightLevel(0.25F);
            if (f.getBlock() == null) f.setBlock(this);
        }

        /** Faint steam above still pools. */
        @Override
        @SideOnly(Side.CLIENT)
        public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
            super.randomDisplayTick(state, world, pos, rand);
            if (rand.nextInt(6) == 0 && isSourceBlock(world, pos) && world.isAirBlock(pos.up()))
                world.spawnParticle(EnumParticleTypes.CLOUD, pos.getX() + rand.nextDouble(), pos.getY() + 1.0D,
                        pos.getZ() + rand.nextDouble(), 0.0D, 0.03D, 0.0D);
            if (rand.nextInt(40) == 0 && isSourceBlock(world, pos))
                world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                        net.minecraft.init.SoundEvents.BLOCK_WATER_AMBIENT, net.minecraft.util.SoundCategory.BLOCKS,
                        0.15F, 0.6F + rand.nextFloat() * 0.2F, false);
        }
    }
}
