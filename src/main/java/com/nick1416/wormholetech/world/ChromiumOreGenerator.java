package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeMesa;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/** Chromium Ore in the Overworld with vanilla gold's distribution: 2 veins of 9 per chunk at Y 0-31, plus gold's mesa bonus. */
public class ChromiumOreGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator gen, IChunkProvider prov) {
        if (world.provider.getDimension() != 0) return;
        WorldGenMinable vein = new WorldGenMinable(Registration.CHROMIUM_ORE.getDefaultState(), 9, BlockMatcher.forBlock(Blocks.STONE));
        // no +8: WorldGenMinable already offsets by 8, keeping the vein inside the population area
        for (int i = 0; i < 2; i++)
            vein.generate(world, random, new BlockPos(chunkX * 16 + random.nextInt(16), random.nextInt(32), chunkZ * 16 + random.nextInt(16)));
        if (world.getBiome(new BlockPos(chunkX * 16 + 16, 0, chunkZ * 16 + 16)) instanceof BiomeMesa) {
            for (int i = 0; i < 20; i++)
                vein.generate(world, random, new BlockPos(chunkX * 16 + random.nextInt(16), 32 + random.nextInt(48), chunkZ * 16 + random.nextInt(16)));
        }
    }
}
