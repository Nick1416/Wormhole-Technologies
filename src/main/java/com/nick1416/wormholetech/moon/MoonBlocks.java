package com.nick1416.wormholetech.moon;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/** Sel'Vora blocks. */
public final class MoonBlocks {
    private MoonBlocks() {}

    /** Pale grey surface dust (not falling, so caves stay stable). */
    public static class Regolith extends Block {
        public Regolith() {
            super(Material.SAND, MapColor.SILVER);
            setHardness(0.5F);
            setSoundType(SoundType.SAND);
            setHarvestLevel("shovel", 0);
        }
    }

    /** Titanium Ore: drops itself (smelt it into a Titanium Ingot). */
    public static class TitaniumOre extends Block {
        public TitaniumOre() {
            super(Material.ROCK, MapColor.STONE);
            setHardness(3.0F);
            setResistance(5.0F);
            setSoundType(SoundType.STONE);
            setHarvestLevel("pickaxe", 1);
        }
    }

    /**
     * Vorani research console in a Sel'Vora outpost. Decorative and indestructible (like the village
     * modulator): no item, no drops, cannot be mined, pushed or blown up. Shows faint data sparks.
     */
    public static class ResearchConsole extends Block {
        public ResearchConsole() {
            super(Material.IRON, MapColor.CYAN);
            setBlockUnbreakable();
            setResistance(6_000_000.0F);
            setSoundType(SoundType.METAL);
            setLightLevel(0.6F);
        }
        @Override public boolean removedByPlayer(IBlockState s, World w, BlockPos p, EntityPlayer pl, boolean willHarvest) {
            if (!w.isRemote) pl.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation("message.wormholetech.research_console"), true);
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

        @SideOnly(Side.CLIENT)
        @Override
        public void randomDisplayTick(IBlockState state, World w, BlockPos pos, Random rand) {
            w.spawnParticle(net.minecraft.util.EnumParticleTypes.END_ROD, pos.getX() + 0.2 + rand.nextDouble() * 0.6,
                    pos.getY() + 1.05, pos.getZ() + 0.2 + rand.nextDouble() * 0.6, 0, 0.015, 0);
        }
    }
}
