package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

/**
 * Vanilla overworld terrain/caves, then every stone block becomes Naquadric Stone. Runs once per
 * new chunk (before decoration), so already generated chunks are untouched.
 */
public class ChunkGeneratorP3X extends ChunkGeneratorOverworld {
    private final World world;

    public ChunkGeneratorP3X(World world, long seed) {
        super(world, seed, false, "");
        this.world = world;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        Chunk chunk = super.generateChunk(x, z);
        IBlockState stone = Blocks.STONE.getDefaultState();
        IBlockState naq = Registration.NAQUADRIC_STONE.getDefaultState();
        for (ExtendedBlockStorage s : chunk.getBlockStorageArray()) {
            if (s == null || s.isEmpty()) continue;
            for (int by = 0; by < 16; by++)
                for (int bz = 0; bz < 16; bz++)
                    for (int bx = 0; bx < 16; bx++)
                        if (s.get(bx, by, bz) == stone) s.set(bx, by, bz, naq);
        }
        return chunk;
    }
}
