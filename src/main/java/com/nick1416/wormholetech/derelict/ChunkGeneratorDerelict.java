package com.nick1416.wormholetech.derelict;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.List;
import java.util.Random;

public class ChunkGeneratorDerelict implements IChunkGenerator {
    private final World world;
    private final Random rand;

    public ChunkGeneratorDerelict(World world, long seed) {
        this.world = world;
        this.rand = new Random(seed);
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        ChunkPrimer primer = new ChunkPrimer();
        DerelictLayout.fill(primer, x, z);
        Chunk chunk = new Chunk(world, primer, x, z);
        byte id = (byte) Biome.getIdForBiome(Registration.DERELICT_BIOME);
        byte[] biomes = chunk.getBiomeArray();
        for (int i = 0; i < biomes.length; i++) biomes[i] = id;
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
        rand.setSeed(world.getSeed() ^ (x * 341873128712L + z * 132897987541L));
        DerelictLayout.populate(world, x, z, rand);
    }

    @Override public boolean generateStructures(Chunk chunkIn, int x, int z) { return false; }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType type, BlockPos pos) {
        return Registration.DERELICT_BIOME.getSpawnableList(type);
    }

    @Override public BlockPos getNearestStructurePos(World w, String name, BlockPos pos, boolean findUnexplored) { return null; }
    @Override public void recreateStructures(Chunk chunkIn, int x, int z) {}
    @Override public boolean isInsideStructure(World w, String name, BlockPos pos) { return false; }
}
