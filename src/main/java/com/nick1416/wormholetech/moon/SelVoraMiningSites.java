package com.nick1416.wormholetech.moon;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Vorani mining sites on Sel'Vora: one per region of {@code selVoraMiningSiteRegionChunks} chunks with
 * probability {@code selVoraMiningSiteChance}%. A site is a shallow open pit with exposed Neutronium Ore,
 * a tiled pad with a Neutronium Reactor on a short pillar that powers it, and a few Vorani Mining Drones
 * working the pit. The whole site (radius 7) sits inside the populate window of its chunk, so it never
 * reaches into ungenerated chunks.
 */
public final class SelVoraMiningSites {
    private SelVoraMiningSites() {}

    /** Pit centre offset from the site centre, pit radius, reactor offset. */
    public static final int PIT_DX = -2, REACTOR_DX = 4;
    public static final double PIT_R = 3.6;

    private static long mix(long a, long b, long c) {
        long h = a * 0x9E3779B97F4A7C15L ^ b * 0xC2B2AE3D27D4EB4FL ^ c * 0x165667B19E3779F9L ^ 0x4D494E45L;
        h ^= h >>> 33; h *= 0xFF51AFD7ED558CCDL; h ^= h >>> 33; h *= 0xC4CEB9FE1A85EC53L; h ^= h >>> 33;
        return h;
    }

    private static int bucket(long h, int n) { return (int) ((h >>> 1) % n); }

    public static int regionChunks() { return Math.max(3, ModConfig.selVoraMiningSiteRegionChunks); }

    /** Chunk {cx, cz} holding the site of the region containing chunk (cx, cz), or null if none. */
    public static int[] siteChunk(int cx, int cz) {
        if (ModConfig.selVoraMiningSiteChance <= 0) return null;
        int n = regionChunks();
        int rx = Math.floorDiv(cx, n), rz = Math.floorDiv(cz, n);
        if (bucket(mix(rx, rz, 801), 100) >= ModConfig.selVoraMiningSiteChance) return null;
        int sx = rx * n + 1 + bucket(mix(rx, rz, 802), n - 2), sz = rz * n + 1 + bucket(mix(rx, rz, 803), n - 2);
        int x = sx * 16 + 16, z = sz * 16 + 16;
        if (Math.abs(x) < 80 && Math.abs(z) < 80) return null;            // keep clear of the return gate
        if (SelVoraOutposts.inside(x, z, 14)) return null;              // and of the research outposts
        return new int[] { sx, sz };
    }

    /** Site centre {x, z} of the region around block (x, z), or null. */
    public static int[] siteNear(int x, int z) {
        int[] c = siteChunk(x >> 4, z >> 4);
        return c == null ? null : new int[] { c[0] * 16 + 16, c[1] * 16 + 16 };
    }

    public static void populate(World w, int cx, int cz, Random r) {
        int[] c = siteChunk(cx, cz);
        if (c == null || c[0] != cx || c[1] != cz) return;
        int x = cx * 16 + 16, z = cz * 16 + 16;
        int g = w.getHeight(new BlockPos(x, 0, z)).getY() - 1;
        if (g < 20 || g > 200) return;
        build(w, x, g, z, r);
        spawnDrones(w, x, g, z, r);
    }

    private static void set(World w, int x, int y, int z, IBlockState s) { w.setBlockState(new BlockPos(x, y, z), s, 2); }

    private static void build(World w, int cx, int g, int cz, Random r) {
        IBlockState air = Blocks.AIR.getDefaultState(), stone = Blocks.STONE.getDefaultState();
        IBlockState regolith = Registration.MOON_REGOLITH.getDefaultState(), ore = Registration.NEUTRONIUM_ORE.getDefaultState();
        IBlockState xeno = Registration.XENOLITH.getDefaultState(), tiles = Registration.XENOLITH_TILES.getDefaultState();
        IBlockState lamp = Registration.XENO_LAMP.getDefaultState();
        int px = cx + PIT_DX;
        for (int dx = -7; dx <= 7; dx++)
            for (int dz = -7; dz <= 7; dz++) {
                int x = cx + dx, z = cz + dz;
                double d = Math.sqrt(dx * dx + dz * dz);
                if (d > 7.2) continue;
                // level the ground to g: fill below, clear above
                for (int y = g; y > g - 8; y--) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (w.getBlockState(p).isOpaqueCube()) break;
                    w.setBlockState(p, y == g ? regolith : stone, 2);
                }
                for (int y = g + 1; y <= g + 8; y++) if (!w.isAirBlock(new BlockPos(x, y, z))) set(w, x, y, z, air);
                double pd = Math.sqrt((x - px) * (x - px) + dz * dz);
                if (pd <= PIT_R) {
                    // open pit: a bowl up to 4 deep, Neutronium Ore showing in the floor and walls
                    int depth = 1 + (int) Math.round(3 * (1 - (pd / PIT_R) * (pd / PIT_R)));
                    for (int k = 0; k < depth; k++) set(w, x, g - k, z, air);
                    int fy = g - depth;
                    set(w, x, fy, z, r.nextInt(3) == 0 ? ore : stone);
                    if (r.nextInt(4) == 0) set(w, x, fy - 1, z, ore);
                } else if (pd <= PIT_R + 1.1) {
                    // rim of loose regolith and spoil, now and then a lump of ore
                    set(w, x, g, z, r.nextInt(7) == 0 ? ore : regolith);
                    if (r.nextInt(3) == 0) set(w, x, g + 1, z, regolith);
                }
            }
        // reactor pad (3x3 tiles), lamps on the corners, xenolith pillar, the reactor on top
        int rx = cx + REACTOR_DX;
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++) {
                set(w, rx + dx, g, cz + dz, tiles);
                if (dx != 0 && dz != 0) set(w, rx + dx, g + 1, cz + dz, lamp);
            }
        set(w, rx, g + 1, cz, xeno);
        set(w, rx, g + 2, cz, Registration.NEUTRONIUM_REACTOR.getDefaultState());
        // conduit of tiles from the pad to the pit rim
        for (int x = px + (int) Math.ceil(PIT_R) + 1; x < rx - 1; x++) set(w, x, g, cz, tiles);
    }

    public static BlockPos reactorPos(int cx, int g, int cz) { return new BlockPos(cx + REACTOR_DX, g + 2, cz); }
    public static BlockPos pitPos(int cx, int g, int cz) { return new BlockPos(cx + PIT_DX, g, cz); }

    private static void spawnDrones(World w, int cx, int g, int cz, Random r) {
        BlockPos pit = pitPos(cx, g, cz), reactor = reactorPos(cx, g, cz);
        for (int k = 0; k < Math.max(0, ModConfig.voraniMiningDronesPerSite); k++) {
            EntityVoraniMiningDrone d = new EntityVoraniMiningDrone(w);
            d.setLocationAndAngles(pit.getX() + 0.5 + r.nextInt(5) - 2, g + 3 + r.nextInt(2), pit.getZ() + 0.5 + r.nextInt(5) - 2, r.nextFloat() * 360F, 0F);
            d.setSite(pit, reactor);
            d.enablePersistence();
            w.spawnEntity(d);
        }
    }
}
