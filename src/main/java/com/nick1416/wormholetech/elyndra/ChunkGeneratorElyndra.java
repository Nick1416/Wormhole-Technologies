package com.nick1416.wormholetech.elyndra;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

/** Overworld-style terrain without villages or dungeons; naquadah and diamond everywhere, plus the old ruins. */
public class ChunkGeneratorElyndra extends ChunkGeneratorOverworld {
    private static final String SETTINGS = "{\"useDungeons\":false,\"useStrongholds\":false,\"useVillages\":false,"
            + "\"useMineShafts\":false,\"useTemples\":false,\"useMonuments\":false,\"useMansions\":false,\"useLavaLakes\":false,"
            + "\"diamondCount\":0}";
    public static final int NAQUADAH_VEINS = 16, DIAMOND_VEINS = 8;
    private final World world;

    public ChunkGeneratorElyndra(World world, long seed) {
        super(world, seed, false, SETTINGS);
        this.world = world;
    }

    @Override
    public void populate(int x, int z) {
        super.populate(x, z);
        Random r = new Random(world.getSeed() ^ (x * 341873128712L + z * 132897987541L) ^ 0xE1D7A0L);
        WorldGenMinable naq = new WorldGenMinable(Registration.ELYNDRA_NAQUADAH_ORE.getDefaultState(), 8);
        for (int i = 0; i < NAQUADAH_VEINS; i++)
            naq.generate(world, r, new BlockPos((x << 4) + r.nextInt(16), 5 + r.nextInt(66), (z << 4) + r.nextInt(16)));
        WorldGenMinable dia = new WorldGenMinable(Blocks.DIAMOND_ORE.getDefaultState(), 8);
        for (int i = 0; i < DIAMOND_VEINS; i++)
            dia.generate(world, r, new BlockPos((x << 4) + r.nextInt(16), 4 + r.nextInt(40), (z << 4) + r.nextInt(16)));
        ElyndraRuins.populate(world, x, z, r);
    }
}
