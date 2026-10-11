package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/** Boosted Naquadah Ore veins, only in P3X-1416. */
public class P3XOreGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator gen, IChunkProvider prov) {
        if (!ModDimensions.isRegistered() || world.provider.getDimension() != ModDimensions.id()) return;
        int veins = ModConfig.p3xNaquadahOreVeinsPerChunk;
        int size = ModConfig.p3xNaquadahOreVeinSize;
        if (veins <= 0 || size <= 0) return;
        int min = Math.max(1, ModConfig.p3xNaquadahOreMinY);
        int max = Math.max(min, Math.min(250, ModConfig.p3xNaquadahOreMaxY));
        WorldGenMinable vein = new WorldGenMinable(Registration.NAQUADAH_ORE.getDefaultState(), size,
                BlockMatcher.forBlock(Blocks.STONE));
        for (int i = 0; i < veins; i++) {
            // +8 keeps the vein inside the already-loaded population area (no cascading chunk loads)
            BlockPos p = new BlockPos(chunkX * 16 + 8 + random.nextInt(16), min + random.nextInt(max - min + 1),
                    chunkZ * 16 + 8 + random.nextInt(16));
            vein.generate(world, random, p);
        }
    }
}
