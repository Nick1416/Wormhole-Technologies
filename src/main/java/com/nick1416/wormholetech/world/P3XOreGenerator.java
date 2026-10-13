package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/** Natural Naquadah Ore veins in Naquadric Stone, only in P3X-1416. */
public class P3XOreGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator gen, IChunkProvider prov) {
        if (!ModDimensions.isRegistered() || world.provider.getDimension() != ModDimensions.id()) return;
        prysmian(random, chunkX, chunkZ, world);
        int veins = ModConfig.p3xNaquadahOreVeinsPerChunk;
        int maxSize = ModConfig.p3xNaquadahOreVeinSize;
        int minSize = Math.min(maxSize, ModConfig.p3xNaquadahOreVeinSizeMin);
        if (veins <= 0 || maxSize <= 0) return;
        int min = Math.max(1, ModConfig.p3xNaquadahOreMinY);
        int max = Math.max(min, Math.min(250, ModConfig.p3xNaquadahOreMaxY));
        net.minecraft.block.state.IBlockState ore = Registration.NATURAL_NAQUADAH_ORE.getDefaultState();
        com.google.common.base.Predicate<net.minecraft.block.state.IBlockState> host = s ->
                s != null && (s.getBlock() == Registration.NAQUADRIC_STONE || s.getBlock() == Blocks.STONE);
        for (int i = 0; i < veins; i++) {
            WorldGenMinable vein = new WorldGenMinable(ore, minSize + random.nextInt(maxSize - minSize + 1), host);
            // no +8 here: WorldGenMinable already adds 8, so this keeps the vein inside the population area
            BlockPos p = new BlockPos(chunkX * 16 + random.nextInt(16), min + random.nextInt(max - min + 1),
                    chunkZ * 16 + random.nextInt(16));
            vein.generate(world, random, p);
        }
    }

    /** Prysmian Ore at vanilla diamond rarity: 1 vein of 8 per chunk, Y 1-16, in Naquadric Stone or stone. */
    private static void prysmian(Random random, int chunkX, int chunkZ, World world) {
        int veins = ModConfig.p3xPrysmianOreVeinsPerChunk, size = ModConfig.p3xPrysmianOreVeinSize;
        if (veins <= 0 || size <= 0) return;
        int max = Math.max(1, Math.min(250, ModConfig.p3xPrysmianOreMaxY));
        com.google.common.base.Predicate<net.minecraft.block.state.IBlockState> host = s ->
                s != null && (s.getBlock() == Registration.NAQUADRIC_STONE || s.getBlock() == Blocks.STONE);
        WorldGenMinable vein = new WorldGenMinable(Registration.PRYSMIAN_ORE.getDefaultState(), size, host);
        for (int i = 0; i < veins; i++) {
            BlockPos p = new BlockPos(chunkX * 16 + random.nextInt(16), 1 + random.nextInt(max), chunkZ * 16 + random.nextInt(16));
            vein.generate(world, random, p);
        }
    }
}
