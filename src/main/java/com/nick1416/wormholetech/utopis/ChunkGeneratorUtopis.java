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
    /** Towers with a guarded modulator lab in the lobby (about 9% of chunks; the vessel has about 3%). */
    public static final int LAB_PERCENT = 35;
    public static final net.minecraft.util.ResourceLocation LAB_LOOT = new net.minecraft.util.ResourceLocation(
            com.nick1416.wormholetech.WormholeTech.MODID, "chests/utopis_lab");
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
        boolean lab = r.nextInt(100) < LAB_PERCENT;
        return new int[] { x0, z0, x1, z1, Math.min(250 - GROUND, h), damage, r.nextInt(3), lab ? 1 : 0 };
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
                if (x == 7 && z == 7) p.setBlockState(lx, GROUND + 1, lz, Registration.UTOPIS_CONSOLE.getDefaultState()); // by the arrival gate
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
                int mxc = (t[0] + t[2]) / 2, mzc = (t[1] + t[3]) / 2;
                int off = t[7] == 1 ? 3 : 1;
                if (x == mxc + off && z == mzc + off && (t[7] == 1 || hash(seed, x, 1, z) < 70)) // a console in the lobby
                    p.setBlockState(lx, GROUND + 1, lz, Registration.UTOPIS_CONSOLE.getDefaultState());
                if (t[7] == 1 && Math.abs(x - mxc) <= 1 && Math.abs(z - mzc) <= 1) { // modulator cradle, like the vessel's halls
                    IBlockState cradle = Registration.MODULATOR_CRADLE.getDefaultState();
                    p.setBlockState(lx, GROUND + 1, lz, cradle);
                    if (x != mxc && z != mzc) { p.setBlockState(lx, GROUND + 2, lz, cradle); p.setBlockState(lx, GROUND + 3, lz, cradle); }
                    else { p.setBlockState(lx, GROUND + 2, lz, Blocks.AIR.getDefaultState()); p.setBlockState(lx, GROUND + 3, lz, Blocks.AIR.getDefaultState()); }
                    if (x == mxc && z == mzc) p.setBlockState(lx, GROUND + 2, lz, Registration.QUANTUM_FIELD_MODULATOR.getDefaultState()
                            .withProperty(com.nick1416.wormholetech.derelict.QuantumFieldModulatorBlock.LOCKED, true));
                }
            }
        Chunk c = new Chunk(world, p, chunkX, chunkZ);
        byte[] biomes = c.getBiomeArray();
        byte id = (byte) Biome.getIdForBiome(Registration.UTOPIS_BIOME);
        for (int i = 0; i < biomes.length; i++) biomes[i] = id;
        c.generateSkylightMap();
        return c;
    }

    /** Guards and loot for labs whose centre lies in this chunk. */
    @Override
    public void populate(int chunkX, int chunkZ) {
        int bx = chunkX << 4, bz = chunkZ << 4;
        for (int cx = Math.floorDiv(bx, CELL) - 1; cx <= Math.floorDiv(bx + 15, CELL); cx++)
            for (int cz = Math.floorDiv(bz, CELL) - 1; cz <= Math.floorDiv(bz + 15, CELL); cz++) {
                int[] t = tower(cx, cz);
                if (t == null || t[7] != 1) continue;
                int mx = (t[0] + t[2]) / 2, mz = (t[1] + t[3]) / 2;
                if (mx >> 4 != chunkX || mz >> 4 != chunkZ) continue;
                Random r = new Random(seed ^ (mx * 31L + mz));
                BlockPos home = new BlockPos(mx, GROUND + 1, mz);
                com.nick1416.wormholetech.entity.EntityGiantEmeraldSentinel g = new com.nick1416.wormholetech.entity.EntityGiantEmeraldSentinel(world);
                g.setLocationAndAngles(mx + 3.5, GROUND + 1, mz + 0.5, r.nextFloat() * 360F, 0F);
                g.setHome(home);
                g.onInitialSpawn(world.getDifficultyForLocation(home), null);
                world.spawnEntity(g);
                for (int i = 0; i < 2; i++) {
                    EntityEmeraldDroid d = new EntityEmeraldDroid(world);
                    d.setLocationAndAngles(mx + 0.5 + i, GROUND + 1, mz + 3.5, r.nextFloat() * 360F, 0F);
                    d.enablePersistence();
                    d.onInitialSpawn(world.getDifficultyForLocation(home), null);
                    world.spawnEntity(d);
                }
                BlockPos chest = new BlockPos(mx + 2, GROUND + 1, mz + 3); // stay on the +x/+z side: those chunks are loaded during populate
                world.setBlockState(chest, Blocks.CHEST.getDefaultState(), 2);
                net.minecraft.tileentity.TileEntity te = world.getTileEntity(chest);
                if (te instanceof net.minecraft.tileentity.TileEntityChest) ((net.minecraft.tileentity.TileEntityChest) te).setLootTable(LAB_LOOT, r.nextLong());
            }
    }
    @Override public boolean generateStructures(Chunk chunkIn, int x, int z) { return false; }
    @Override public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType type, BlockPos pos) {
        return Registration.UTOPIS_BIOME.getSpawnableList(type);
    }
    @Override public BlockPos getNearestStructurePos(World w, String name, BlockPos pos, boolean find) { return null; }
    @Override public void recreateStructures(Chunk chunkIn, int x, int z) {}
    @Override public boolean isInsideStructure(World w, String name, BlockPos pos) { return false; }
}
