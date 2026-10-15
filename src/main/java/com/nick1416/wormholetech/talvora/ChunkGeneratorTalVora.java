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
        generateXenolithOre(x, z);
        AlienVillages.populate(world, x, z);
    }

    /**
     * Xenolith Ore: iron-sized veins (9) in the red sandstone (and any leftover stone) from y 12 up to
     * just under the sand of that column, 23 per chunk: about 15% more ore than vanilla iron (20 x 9).
     */
    private void generateXenolithOre(int cx, int cz) {
        int n = com.nick1416.wormholetech.ModConfig.xenolithOreVeinsPerChunk;
        if (n <= 0) return;
        java.util.Random r = new java.util.Random(world.getSeed() ^ (cx * 341873128712L + cz * 132897987541L) ^ 0x58454E4FL);
        net.minecraft.world.gen.feature.WorldGenMinable vein = new net.minecraft.world.gen.feature.WorldGenMinable(
                com.nick1416.wormholetech.registry.Registration.XENOLITH_ORE.getDefaultState(), 9,
                state -> state != null && (state.getBlock() == Blocks.RED_SANDSTONE || state.getBlock() == Blocks.STONE));
        for (int i = 0; i < n; i++) {
            int x = (cx << 4) + r.nextInt(16), z = (cz << 4) + r.nextInt(16); // WorldGenMinable adds the +8 itself
            int top = Math.max(16, world.getHeight(x + 8, z + 8) - 4);   // stay under the sand layer
            vein.generate(world, r, new net.minecraft.util.math.BlockPos(x, 12 + r.nextInt(top - 11), z));
        }
    }
}
