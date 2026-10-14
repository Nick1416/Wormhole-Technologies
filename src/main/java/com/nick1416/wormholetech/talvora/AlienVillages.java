package com.nick1416.wormholetech.talvora;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Rare, huge alien villages on Tal'Vora. The world is split into square regions of
 * {@code talVoraVillageRegionChunks} chunks; each region holds one village with probability
 * {@code talVoraVillageChance}% at a hashed spot well inside the region. Every column of a village
 * is a pure function of its offset from the centre, so each chunk's population builds its own
 * part without loading neighbours. Layout (radius ~46): a tiled plaza, a central spire with a dome,
 * eight domed huts on a ring, and twelve glowing obelisks around the edge.
 */
public final class AlienVillages {
    private AlienVillages() {}

    public static final int RADIUS = 46, FLOOR_Y = 70;
    private static final int HUTS = 8, HUT_RING = 28, OBELISKS = 12, OBELISK_RING = 39;
    private static final double HUT_R = 6.5;

    private static long mix(long a, long b, long c) {
        long h = a * 0x9E3779B97F4A7C15L ^ b * 0xC2B2AE3D27D4EB4FL ^ c * 0x165667B19E3779F9L ^ 0x5DEECE66DL;
        h ^= h >>> 33; h *= 0xFF51AFD7ED558CCDL; h ^= h >>> 33; h *= 0xC4CEB9FE1A85EC53L; h ^= h >>> 33;
        return h;
    }

    private static int bucket(long h, int n) { return (int) ((h >>> 1) % n); }

    /** Village centre {x, z} of the region containing block (x, z), or null. */
    public static int[] villageFor(int x, int z) {
        int size = Math.max(8, ModConfig.talVoraVillageRegionChunks) * 16;
        int rx = Math.floorDiv(x, size), rz = Math.floorDiv(z, size);
        if (bucket(mix(rx, rz, 501), 100) >= ModConfig.talVoraVillageChance) return null;
        int margin = RADIUS + 8, span = size - 2 * margin;
        int vx = rx * size + margin + bucket(mix(rx, rz, 502), span), vz = rz * size + margin + bucket(mix(rx, rz, 503), span);
        if (Math.abs(vx) < 160 && Math.abs(vz) < 160) return null; // keep clear of the return gate
        return new int[] { vx, vz };
    }

    /** Nearest village centre to (x, z), searching r regions around, or null. */
    public static int[] nearest(int x, int z, int r) {
        int size = Math.max(8, ModConfig.talVoraVillageRegionChunks) * 16;
        int[] best = null;
        long bd = Long.MAX_VALUE;
        for (int i = -r; i <= r; i++)
            for (int j = -r; j <= r; j++) {
                int[] v = villageFor(x + i * size, z + j * size);
                if (v == null) continue;
                long d = (long) (v[0] - x) * (v[0] - x) + (long) (v[1] - z) * (v[1] - z);
                if (d < bd) { bd = d; best = v; }
            }
        return best;
    }

    private static double hutAngle(int vx, int vz, int k) { return k * Math.PI * 2 / HUTS + bucket(mix(vx, vz, 504), 360) * Math.PI / 180; }

    public static void populate(World w, int cx, int cz) {
        int x0 = (cx << 4) + 8, z0 = (cz << 4) + 8;
        for (int lx = 0; lx < 16; lx++)
            for (int lz = 0; lz < 16; lz++) {
                int x = x0 + lx, z = z0 + lz;
                int[] v = villageFor(x, z);
                if (v != null) column(w, x, z, v[0], v[1]);
            }
        int[] v = villageFor(x0 + 8, z0 + 8);
        if (v != null && v[0] >= x0 && v[0] < x0 + 16 && v[1] >= z0 && v[1] < z0 + 16) spawnVillagers(w, v[0], v[1]);
    }

    private static void set(World w, int x, int y, int z, IBlockState s) { w.setBlockState(new BlockPos(x, y, z), s, 2); }

    private static void column(World w, int x, int z, int vx, int vz) {
        double dx = x - vx, dz = z - vz, r = Math.sqrt(dx * dx + dz * dz);
        if (r > RADIUS) return;
        IBlockState stone = Registration.XENOLITH.getDefaultState(), tiles = Registration.XENOLITH_TILES.getDefaultState();
        IBlockState lamp = Registration.XENO_LAMP.getDefaultState(), air = Blocks.AIR.getDefaultState();
        IBlockState base = Blocks.RED_SANDSTONE.getStateFromMeta(2);
        int y0 = FLOOR_Y;
        // foundation down to solid ground, clear the sky above
        for (int y = y0 - 1, n = 0; y > 0 && n < 30; y--, n++) {
            BlockPos p = new BlockPos(x, y, z);
            if (w.getBlockState(p).isOpaqueCube() && w.getBlockState(p).getBlock() != Blocks.SAND) break;
            w.setBlockState(p, base, 2);
        }
        for (int y = y0 + 1; y <= y0 + 48; y++) if (!w.isAirBlock(new BlockPos(x, y, z))) set(w, x, y, z, air);
        // floor: tiled plaza, xenolith spokes and rings
        double ax = Math.abs(dx), az = Math.abs(dz);
        boolean spoke = ax <= 1 || az <= 1 || Math.abs(ax - az) <= 1.0;
        boolean ring = (r >= 17 && r < 19) || (r >= 34 && r < 36);
        IBlockState floor = r < 20 ? tiles : (spoke || ring ? stone : base);
        if ((int) r == 10 || ((int) r == 35 && ((x + z) & 3) == 0)) floor = lamp;
        set(w, x, y0, z, floor);

        // central spire
        if (r <= 9.2) {
            boolean door = (ax <= 1.2 || az <= 1.2);
            if (r >= 8) {
                for (int y = y0 + 1; y <= y0 + 30; y++) {
                    if (door && y <= y0 + 4) continue;
                    set(w, x, y, z, (y - y0) % 6 == 0 ? lamp : stone);
                }
            }
            double top = Math.sqrt(Math.max(0, 9.2 * 9.2 - r * r)) * 0.9;
            int ty = y0 + 30 + (int) Math.round(top);
            set(w, x, ty, z, r < 1.5 ? lamp : stone);
            if (ty - 1 > y0 + 30) set(w, x, ty - 1, z, stone);
            if (r < 1.5) for (int y = y0 + 1; y <= y0 + 6; y++) set(w, x, y, z, lamp);
            return;
        }
        // huts
        for (int k = 0; k < HUTS; k++) {
            double a = hutAngle(vx, vz, k);
            double hx = Math.cos(a) * HUT_RING, hz = Math.sin(a) * HUT_RING;
            double px = dx - hx, pz = dz - hz, hr = Math.sqrt(px * px + pz * pz);
            if (hr > HUT_R) continue;
            double outer = Math.sqrt(HUT_R * HUT_R - hr * hr) * 1.25;
            double inner = hr < HUT_R - 1 ? Math.sqrt((HUT_R - 1) * (HUT_R - 1) - hr * hr) * 1.25 : -1;
            double ux = -hx / HUT_RING, uz = -hz / HUT_RING;            // towards the centre
            double along = px * ux + pz * uz, perp = Math.abs(px * uz - pz * ux);
            boolean doorCol = along > 3.5 && perp < 1.3;
            int top = (int) Math.round(outer);
            for (int y = 1; y <= top; y++) {
                if (y <= inner) continue;
                if (doorCol && y <= 3) continue;
                set(w, x, y0 + y, z, hr < 1.0 && y == top ? lamp : stone);
            }
            if (hr < 1.0) set(w, x, y0, z, lamp);
            return;
        }
        // obelisks
        for (int k = 0; k < OBELISKS; k++) {
            double a = k * Math.PI * 2 / OBELISKS;
            if (Math.abs(dx - Math.cos(a) * OBELISK_RING) <= 1.0 && Math.abs(dz - Math.sin(a) * OBELISK_RING) <= 1.0) {
                for (int y = y0 + 1; y <= y0 + 14; y++) set(w, x, y, z, stone);
                set(w, x, y0 + 15, z, lamp);
                return;
            }
        }
    }

    private static void spawnVillagers(World w, int vx, int vz) {
        for (int k = 0; k < 6; k++) {
            double a = k * Math.PI / 3 + 0.4;
            double x = vx + 0.5 + Math.cos(a) * 14, z = vz + 0.5 + Math.sin(a) * 14;
            EntityAlienVillager v = new EntityAlienVillager(w);
            v.setLocationAndAngles(x, FLOOR_Y + 1, z, (float) (k * 60), 0F);
            v.enablePersistence();
            w.spawnEntity(v);
        }
    }
}
