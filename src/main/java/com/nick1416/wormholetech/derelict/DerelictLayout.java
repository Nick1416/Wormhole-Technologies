package com.nick1416.wormholetech.derelict;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.block.StargatePortalBlock;
import com.nick1416.wormholetech.entity.EntityGiantEmeraldSentinel;
import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.stargate.GateShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

/**
 * The derelict vessel: an endless one-deck labyrinth between two hull layers, floating in space.
 * Everything is decided per chunk from hashes of global coordinates, so a chunk never needs its
 * neighbours (no cascading generation).
 *
 * Grid: 4-block cells; walls on every x%4==0 / z%4==0 line, 3-wide corridors. Maze chunks carve a
 * spanning tree over their 16 cells and every chunk edge has fixed doorways (see segmentOpen), so
 * the whole deck is connected. Some chunks are open rooms (walls
 * inside removed, doorways forced in the middle of each side): plain rooms with a chest, modulator
 * rooms (Quantum Field Modulator in its cradle + Giant Emerald Sentinel), and the hub at chunk 0,0
 * with the return gate.
 */
public final class DerelictLayout {
    private DerelictLayout() {}

    public static final int HULL_LOW = 63, FLOOR = 64, CEIL = 69, HULL_HIGH = 70;
    public static final int TYPE_MAZE = 0, TYPE_ROOM = 1, TYPE_MODULATOR = 2, TYPE_HUB = 3;
    /** Return gate in the hub: 2x3 opening, frame x 6..9, y 64..68 at z 8. */
    public static final GateShape GATE = new GateShape(new BlockPos(7, FLOOR + 1, 8), EnumFacing.Axis.X, 2, 3);
    public static final ResourceLocation ROOM_LOOT = LootTableList.register(new ResourceLocation(WormholeTech.MODID, "chests/derelict_room"));

    private static long mix(long a, long b, long c) {
        long h = a * 0x9E3779B97F4A7C15L ^ b * 0xC2B2AE3D27D4EB4FL ^ c * 0x165667B19E3779F9L ^ 0x5DEECE66DL;
        h ^= h >>> 33; h *= 0xFF51AFD7ED558CCDL; h ^= h >>> 33; h *= 0xC4CEB9FE1A85EC53L; h ^= h >>> 33;
        return h;
    }

    private static int bucket(long h, int n) { return (int) ((h >>> 1) % n); }

    public static int chunkType(int cx, int cz) {
        if (cx == 0 && cz == 0) return TYPE_HUB;
        boolean nearHub = Math.abs(cx) <= 2 && Math.abs(cz) <= 2;
        int modChance = ModConfig.derelictModulatorRoomChance;
        if (!nearHub && modChance > 0 && bucket(mix(cx, cz, 77), modChance) == 0) return TYPE_MODULATOR;
        int roomChance = ModConfig.derelictRoomChance;
        if (roomChance > 0 && bucket(mix(cx, cz, 31), roomChance) == 0) return TYPE_ROOM;
        return TYPE_MAZE;
    }

    private static boolean isRoom(int cx, int cz) { return chunkType(cx, cz) != TYPE_MAZE; }

    /**
     * Connectivity guarantee. Every chunk is a 4x4 grid of cells. Inside a maze chunk the open wall
     * segments always contain a spanning tree of all 16 cells (randomised Kruskal seeded by the chunk
     * coordinates), plus a few extra openings for loops. Room chunks are fully open inside. On every
     * chunk edge, the segments at cell positions 1 and 2 along the edge are ALWAYS open (fixed
     * doorways), for every chunk type. So each chunk is internally connected and joined to all 4
     * neighbours: the whole deck is one connected region and nothing can be sealed off.
     */
    private static final ThreadLocal<long[]> TREE_CACHE = new ThreadLocal<>();

    /** Bits 0..11: segment between cell (i, j) and (i+1, j), index j*3+i. Bits 12..23: between (i, j) and (i, j+1), index 12+i*3+j. */
    static long mazeMask(int cx, int cz) {
        long[] c = TREE_CACHE.get();
        if (c != null && c[0] == cx && c[1] == cz) return c[2];
        int[] parent = new int[16];
        for (int i = 0; i < 16; i++) parent[i] = i;
        int[] order = new int[24];
        for (int i = 0; i < 24; i++) order[i] = i;
        Random r = new Random(mix(cx, cz, 99));
        for (int i = 23; i > 0; i--) { int k = r.nextInt(i + 1); int t = order[i]; order[i] = order[k]; order[k] = t; }
        long mask = 0;
        for (int e : order) {
            int a, b;
            if (e < 12) { int j = e / 3, i = e % 3; a = j * 4 + i; b = a + 1; }
            else { int i = (e - 12) / 3, j = (e - 12) % 3; a = j * 4 + i; b = a + 4; }
            int ra = find(parent, a), rb = find(parent, b);
            if (ra != rb) { parent[ra] = rb; mask |= 1L << e; }
            else if (r.nextInt(100) < 25) mask |= 1L << e; // extra loops
        }
        TREE_CACHE.set(new long[] { cx, cz, mask });
        return mask;
    }

    private static int find(int[] p, int x) { while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; } return x; }

    /** Is the wall segment on {@code line} (a multiple of 4) at {@code cell} open? axis 0 = x line, 1 = z line. */
    private static boolean segmentOpen(int line, int cell, int axis) {
        int cellA = (line >> 2) - 1, cellB = line >> 2;
        int chA = cellA >> 2, chB = cellB >> 2, chO = cell >> 2;
        int along = cell & 3;
        if (chA != chB) return along == 1 || along == 2;   // fixed doorways on every chunk edge
        int cx = axis == 0 ? chA : chO, cz = axis == 0 ? chO : chA;
        if (isRoom(cx, cz)) return true;
        int i = cellA & 3;                                   // cell index across the line (0..2)
        int bit = axis == 0 ? along * 3 + i : 12 + along * 3 + i;
        return (mazeMask(cx, cz) >> bit & 1) != 0;
    }

    /** Post at the corner (x, z), both multiples of 4: solid unless all four cells are inside one room chunk. */
    private static boolean postOpen(int x, int z) {
        int cx = x >> 4, cz = z >> 4;
        if ((x & 15) == 0 || (z & 15) == 0) return false;
        return isRoom(cx, cz);
    }

    /** Bulkhead column at (x, z)? Package-visible for the connectivity check. */
    static boolean wallAt(int x, int z) {
        boolean lx = (x & 3) == 0, lz = (z & 3) == 0;
        if (lx && lz) return !postOpen(x, z);
        if (lx) return !segmentOpen(x, z >> 2, 0);
        if (lz) return !segmentOpen(z, x >> 2, 1);
        return false;
    }

    public static void fill(ChunkPrimer p, int cx, int cz) {
        IBlockState plating = Registration.HULL_PLATING.getDefaultState();
        IBlockState wall = Registration.HULL_BULKHEAD.getDefaultState();
        IBlockState window = Registration.HULL_WINDOW.getDefaultState();
        IBlockState lamp = Registration.HULL_LAMP.getDefaultState();
        int type = chunkType(cx, cz);
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = (cx << 4) + lx, z = (cz << 4) + lz;
                int cellX = x >> 2, cellZ = z >> 2;
                boolean centre = (x & 3) == 2 && (z & 3) == 2;
                boolean cellInner = (x & 3) != 0 && (z & 3) != 0;
                long ch = mix(cellX, cellZ, 5);
                boolean skylight = cellInner && bucket(ch, 11) == 0;     // ceiling viewport over this cell
                boolean floorView = cellInner && bucket(ch, 23) == 1;    // floor viewport
                p.setBlockState(lx, HULL_LOW, lz, plating);
                p.setBlockState(lx, FLOOR, lz, floorView ? window : plating);
                p.setBlockState(lx, CEIL, lz, skylight ? window : (centre && ((cellX + cellZ) & 1) == 0 ? lamp : plating));
                p.setBlockState(lx, HULL_HIGH, lz, skylight ? window : plating);
                if (floorView) p.setBlockState(lx, HULL_LOW, lz, window);
                if (wallAt(x, z)) for (int y = FLOOR + 1; y < CEIL; y++) p.setBlockState(lx, y, lz, wall);
            }
        }
        if (type == TYPE_ROOM || type == TYPE_MODULATOR || type == TYPE_HUB) {
            for (int[] c : new int[][] { { 3, 3 }, { 12, 3 }, { 3, 12 }, { 12, 12 } }) p.setBlockState(c[0], CEIL, c[1], lamp);
        }
        placeGuideLamps(p, cx, cz, type);
        if (type == TYPE_MODULATOR) {
            IBlockState cradle = Registration.MODULATOR_CRADLE.getDefaultState();
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++) {
                    p.setBlockState(8 + dx, FLOOR + 1, 8 + dz, cradle);
                    if (dx != 0 && dz != 0) {
                        p.setBlockState(8 + dx, FLOOR + 2, 8 + dz, cradle);
                        p.setBlockState(8 + dx, FLOOR + 3, 8 + dz, cradle);
                    }
                }
            p.setBlockState(8, FLOOR + 2, 8, Registration.QUANTUM_FIELD_MODULATOR.getDefaultState()
                    .withProperty(QuantumFieldModulatorBlock.LOCKED, true));
        }
        if (type == TYPE_HUB) {
            IBlockState frame = Registration.ANCIENT_GATE_FRAME.getDefaultState();
            IBlockState portal = Registration.STARGATE_PORTAL.getDefaultState().withProperty(StargatePortalBlock.AXIS, GATE.axis);
            for (int y = -1; y <= GATE.height; y++)
                for (int x = -1; x <= GATE.width; x++) {
                    BlockPos q = GATE.interior(x, y);
                    boolean edge = y == -1 || y == GATE.height || x == -1 || x == GATE.width;
                    p.setBlockState(q.getX(), q.getY(), q.getZ(), edge ? frame : portal);
                }
        }
    }

    /**
     * Gold Hull Lamps in the ceiling over the cells leading to each modulator room doorway: three cells
     * deep into each neighbouring chunk plus the doorway cells inside the room. Ceiling only, so no
     * path is ever blocked. Decided from chunk types alone (no neighbour generation needed).
     */
    private static void placeGuideLamps(ChunkPrimer p, int cx, int cz, int type) {
        IBlockState gold = Registration.HULL_LAMP_GOLD.getDefaultState();
        int[][] dirs = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        for (int[] d : dirs) {
            boolean towardRoom = chunkType(cx + d[0], cz + d[1]) == TYPE_MODULATOR;
            int depthMax = towardRoom ? 3 : (type == TYPE_MODULATOR ? 1 : 0);
            for (int depth = 0; depth < depthMax; depth++)
                for (int along = 1; along <= 2; along++) {
                    // cell index across: 0 is the cell touching the edge in direction d
                    int across = d[0] + d[1] < 0 ? depth : 3 - depth;
                    int cellX = d[0] != 0 ? across : along, cellZ = d[0] != 0 ? along : across;
                    p.setBlockState(cellX * 4 + 2, CEIL, cellZ * 4 + 2, gold);
                }
        }
    }

    /** Called from the chunk generator's populate: blocks/entities inside this chunk only. */
    public static void populate(World w, int cx, int cz, Random rand) {
        int type = chunkType(cx, cz);
        int bx = cx << 4, bz = cz << 4;
        if (type == TYPE_ROOM && rand.nextInt(2) == 0) {
            BlockPos c = new BlockPos(bx + 2 + rand.nextInt(12), FLOOR + 1, bz + 2 + rand.nextInt(12));
            if (w.isAirBlock(c)) {
                w.setBlockState(c, Blocks.CHEST.getDefaultState(), 2);
                TileEntity te = w.getTileEntity(c);
                if (te instanceof TileEntityChest) ((TileEntityChest) te).setLootTable(ROOM_LOOT, rand.nextLong());
            }
        }
        if (type == TYPE_MODULATOR) {
            BlockPos home = new BlockPos(bx + 8, FLOOR + 1, bz + 8);
            if (w.getEntitiesWithinAABB(EntityGiantEmeraldSentinel.class, new AxisAlignedBB(home).grow(16)).isEmpty()) {
                EntityGiantEmeraldSentinel g = new EntityGiantEmeraldSentinel(w);
                g.setLocationAndAngles(bx + 3.5, FLOOR + 1, bz + 3.5, rand.nextFloat() * 360F, 0F);
                g.setHome(home);
                g.onInitialSpawn(w.getDifficultyForLocation(home), null);
                w.spawnEntity(g);
            }
        }
    }

    /** Nearest modulator room chunk (search radius in chunks), or null. For tests/locators. */
    public static int[] findModulatorRoom(int r) {
        for (int d = 0; d <= r; d++)
            for (int x = -d; x <= d; x++)
                for (int z = -d; z <= d; z++)
                    if (Math.max(Math.abs(x), Math.abs(z)) == d && chunkType(x, z) == TYPE_MODULATOR) return new int[] { x, z };
        return null;
    }
}
