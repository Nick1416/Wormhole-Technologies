package com.nick1416.wormholetech.moon;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

/** Rolling grey highlands with no water, lakes or structures; titanium ore in the stone. */
public class ChunkGeneratorSelVora extends ChunkGeneratorOverworld {
    private static final String SETTINGS = "{\"seaLevel\":1,\"useWaterLakes\":false,\"useLavaLakes\":false,\"useDungeons\":false,"
            + "\"useStrongholds\":false,\"useVillages\":false,\"useMineShafts\":false,\"useTemples\":false,\"useMonuments\":false,"
            + "\"useMansions\":false,\"useRavines\":false,\"useLavaOceans\":false,\"dirtCount\":0,\"gravelCount\":0,\"graniteCount\":0,"
            + "\"dioriteCount\":0,\"andesiteCount\":0,\"coalCount\":0,\"ironCount\":0,\"goldCount\":0,\"redstoneCount\":0,"
            + "\"diamondCount\":0,\"lapisCount\":0}";
    /** Neutronium Ore veins per chunk (size 9, y 4..67). */
    public static final int NEUTRONIUM_VEINS = 20;
    /** Titanium Ore veins per chunk (size 8, y 6..60): a little more common than vanilla iron. */
    public static final int TITANIUM_VEINS = 14;
    private final World world;

    public ChunkGeneratorSelVora(World world, long seed) {
        super(world, seed, false, SETTINGS);
        this.world = world;
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        Chunk chunk = super.generateChunk(x, z);
        IBlockState water = Blocks.WATER.getDefaultState(), air = Blocks.AIR.getDefaultState();
        for (ExtendedBlockStorage s : chunk.getBlockStorageArray()) {
            if (s == null || s.isEmpty()) continue;
            for (int by = 0; by < 16; by++)
                for (int bz = 0; bz < 16; bz++)
                    for (int bx = 0; bx < 16; bx++)
                        if (s.get(bx, by, bz) == water) s.set(bx, by, bz, air);
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
        super.populate(x, z);
        Random r = new Random(world.getSeed() ^ (x * 341873128712L + z * 132897987541L) ^ 0x5449544EL);
        WorldGenMinable vein = new WorldGenMinable(Registration.TITANIUM_ORE.getDefaultState(), 8,
                s -> s != null && (s.getBlock() == Blocks.STONE || s.getBlock() == Registration.MOON_REGOLITH));
        for (int i = 0; i < TITANIUM_VEINS; i++)   // WorldGenMinable adds the +8 itself
            vein.generate(world, r, new BlockPos((x << 4) + r.nextInt(16), 6 + r.nextInt(55), (z << 4) + r.nextInt(16)));
        WorldGenMinable nVein = new WorldGenMinable(Registration.NEUTRONIUM_ORE.getDefaultState(), 9,
                s -> s != null && (s.getBlock() == Blocks.STONE || s.getBlock() == Registration.MOON_REGOLITH));
        for (int i = 0; i < NEUTRONIUM_VEINS; i++)  // as common as Overworld iron: 20 veins of 9
            nVein.generate(world, r, new BlockPos((x << 4) + r.nextInt(16), 4 + r.nextInt(64), (z << 4) + r.nextInt(16)));
        SelVoraBarrens.populate(world, x, z, r);
        SelVoraOutposts.populate(world, x, z);
    }

    @Override
    public java.util.List<net.minecraft.world.biome.Biome.SpawnListEntry> getPossibleCreatures(net.minecraft.entity.EnumCreatureType type, BlockPos pos) {
        if (type == net.minecraft.entity.EnumCreatureType.MONSTER && SelVoraOutposts.inside(pos.getX(), pos.getZ(), 10))
            return java.util.Collections.emptyList();
        return super.getPossibleCreatures(type, pos);
    }
}
