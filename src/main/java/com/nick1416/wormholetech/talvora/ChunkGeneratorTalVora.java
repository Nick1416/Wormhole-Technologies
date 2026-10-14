package com.nick1416.wormholetech.talvora;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkGeneratorOverworld;

/** Low dunes: vanilla terrain with no sea, lakes or structures, stone turned to red sandstone. */
public class ChunkGeneratorTalVora extends ChunkGeneratorOverworld {
    private static final String SETTINGS = "{\"seaLevel\":1,\"useWaterLakes\":false,\"useLavaLakes\":false,\"useDungeons\":false,"
            + "\"useStrongholds\":false,\"useVillages\":false,\"useMineShafts\":false,\"useTemples\":false,\"useMonuments\":false,"
            + "\"useMansions\":false,\"useRavines\":false,\"useLavaOceans\":false,\"dirtCount\":0,\"gravelCount\":0,\"graniteCount\":0,"
            + "\"dioriteCount\":0,\"andesiteCount\":0,\"coalCount\":0,\"ironCount\":0,\"goldCount\":0,\"redstoneCount\":0,"
            + "\"diamondCount\":0,\"lapisCount\":0}";
    private final World world;

    public ChunkGeneratorTalVora(World world, long seed) {
        super(world, seed, false, SETTINGS);
        this.world = world;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        Chunk chunk = super.generateChunk(x, z);
        IBlockState stone = Blocks.STONE.getDefaultState(), water = Blocks.WATER.getDefaultState();
        IBlockState rs = Blocks.RED_SANDSTONE.getDefaultState(), air = Blocks.AIR.getDefaultState();
        for (ExtendedBlockStorage s : chunk.getBlockStorageArray()) {
            if (s == null || s.isEmpty()) continue;
            for (int by = 0; by < 16; by++)
                for (int bz = 0; bz < 16; bz++)
                    for (int bx = 0; bx < 16; bx++) {
                        IBlockState b = s.get(bx, by, bz);
                        if (b == stone) s.set(bx, by, bz, rs);
                        else if (b == water) s.set(bx, by, bz, air);
                    }
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
        super.populate(x, z);
        AlienVillages.populate(world, x, z);
    }
}
