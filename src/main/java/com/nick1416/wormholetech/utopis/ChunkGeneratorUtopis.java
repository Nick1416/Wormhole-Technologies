package com.nick1416.wormholetech.utopis;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.List;
import java.util.Random;

/**
 * A fully artificial world: a concrete crust, a street grid every 32 blocks, and on each city block one tower of
 * white concrete and glass, many of them broken. Everything is decided per column, so there is no populate step,
 * no ores and nothing natural.
 */
public class ChunkGeneratorUtopis implements IChunkGenerator {
    public static final int GROUND = 60, CELL = 32;
    private final World world;
    private final long seed;
    private static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    private static final IBlockState CRUST = Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY);
    private static final IBlockState ROAD = Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK);
    private static final IBlockState LINE = Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIME);
    private static final IBlockState PLAZA = Blocks.QUARTZ_BLOCK.getDefaultState();
    private static final IBlockState WALL = Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
    private static final IBlockState GLASS = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE);
    private static final IBlockState FLOOR = Blocks.CONCRETE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);

    public ChunkGeneratorUtopis(World world) {
        this.world = world;
        this.seed = world.getSeed() ^ 0x0710915L;
    }

    /** Tower on a city block: {x0, z0, x1, z1, height, damage%, accent}. */
    private int[] tower(int cx, int cz) {
        Random r = new Random(seed ^ (cx * 341873128712L + cz * 132897987541L));
        if (Math.abs(cx) <= 0 && Math.abs(cz) <= 0) return null; // the arrival square stays open
        if (r.nextInt(8) == 0) return null;                        // an empty plaza
        int inset = 6 + r.nextInt(5);
        int x0 = cx * CELL + inset, z0 = cz * CELL + inset, x1 = cx * CELL + CELL - inset - r.nextInt(3), z1 = cz * CELL + CELL - inset - r.nextInt(3);
        int h = 10 + r.nextInt(r.nextInt(4) == 0 ? 120 : 50);
        int damage = r.nextInt(3) == 0 ? 0 : 5 + r.nextInt(30);
        return new int[] { x0, z0, x1, z1, Math.min(250 - GROUND, h), damage, r.nextInt(3) };
    }

    private static int hash(long s, int x, int y, int z) {
        long h = s ^ (x * 73856093L) ^ (y * 19349663L) ^ (z * 83492791L);
        h ^= h >>> 29; h *= 0xBF58476D1CE4E5B9L; h ^= h >>> 32;
        return (int) ((h & 0x7fffffff) % 100);
    }

    @Override
    public Chunk generateChunk(int chunkX, int chunkZ) {
        ChunkPrimer p = new ChunkPrimer();
        for (int lx = 0; lx < 16; lx++)
            for (int lz = 0; lz < 16; lz++) {
                int x = (chunkX << 4) + lx, z = (chunkZ << 4) + lz;
                int mx = Math.floorMod(x, CELL), mz = Math.floorMod(z, CELL);
                p.setBlockState(lx, 0, lz, BEDROCK);
                for (int y = 1; y < GROUND; y++) p.setBlockState(lx, y, lz, CRUST);
                boolean road = mx < 5 || mz < 5;
                p.setBlockState(lx, GROUND, lz, road ? ((mx == 2 || mz == 2) && (x + z & 3) == 0 ? LINE : ROAD) : PLAZA);
                if (road) continue;
                int[] t = tower(Math.floorDiv(x, CELL), Math.floorDiv(z, CELL));
                if (t == null || x < t[0] || x > t[2] || z < t[1] || z > t[3]) {
                    if ((mx == 8 || mz == 8) && (x * 31 + z * 17 & 15) == 0) p.setBlockState(lx, GROUND + 1, lz, Registration.HULL_LAMP.getDefaultState());
                    continue;
                }
                boolean edge = x == t[0] || x == t[2] || z == t[1] || z == t[3];
                boolean corner = (x == t[0] || x == t[2]) && (z == t[1] || z == t[3]);
                int top = GROUND + t[4];
                int broken = t[5] > 0 ? top - hash(seed, x, 0, z) * t[5] / 100 : top; // jagged, collapsed tops
                for (int y = GROUND + 1; y <= broken; y++) {
                    int level = y - GROUND;
                    IBlockState s = null;
                    if (edge) {
                        boolean door = level <= 3 && ((x == (t[0] + t[2]) / 2) || (z == (t[1] + t[3]) / 2));
                        if (!door) s = corner || level % 5 == 0 ? (t[6] == 0 && corner ? Registration.HULL_PLATING.getDefaultState() : WALL) : GLASS;
                    } else if (level % 5 == 0) s = FLOOR;
                    else if (level % 5 == 1 && (x + z) % 7 == 0) s = Registration.HULL_LAMP.getDefaultState();
                    if (s != null && t[5] > 0 && y > GROUND + 4 && hash(seed, x, y, z) < t[5] / 2) s = null; // shattered walls
                    if (s != null) p.setBlockState(lx, y, lz, s);
                }
                if (broken == top && !edge) p.setBlockState(lx, top, lz, FLOOR); // intact roof
            }
        Chunk c = new Chunk(world, p, chunkX, chunkZ);
        byte[] biomes = c.getBiomeArray();
        byte id = (byte) Biome.getIdForBiome(Registration.UTOPIS_BIOME);
        for (int i = 0; i < biomes.length; i++) biomes[i] = id;
        c.generateSkylightMap();
        return c;
    }

    @Override public void populate(int x, int z) {}
    @Override public boolean generateStructures(Chunk chunkIn, int x, int z) { return false; }
    @Override public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType type, BlockPos pos) {
        return Registration.UTOPIS_BIOME.getSpawnableList(type);
    }
    @Override public BlockPos getNearestStructurePos(World w, String name, BlockPos pos, boolean find) { return null; }
    @Override public void recreateStructures(Chunk chunkIn, int x, int z) {}
    @Override public boolean isInsideStructure(World w, String name, BlockPos pos) { return false; }
}
