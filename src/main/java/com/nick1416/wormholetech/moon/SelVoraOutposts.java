package com.nick1416.wormholetech.moon;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.talvora.EntityAlienVillager;
import com.nick1416.wormholetech.talvora.EntityXenolithDrone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Vorani research outposts on Sel'Vora: one per region of {@code selVoraOutpostRegionChunks} chunks
 * with probability {@code selVoraOutpostChance}%. Like the Tal'Vora villages, every column is a pure
 * function of its offset from the centre. Layout (radius 16): a tiled pad, a xenolith dome with four
 * doors, a Neutronium Ore block in the middle, four research consoles around it with a
 * Vorani studying each (facing the centre), and a lamp antenna on top.
 */
public final class SelVoraOutposts {
    private SelVoraOutposts() {}

    public static final int RADIUS = 16, FLOOR_Y = 72, CONSOLE_R = 4;
    private static final double DOME_R = 10.5;

    private static long mix(long a, long b, long c) {
        long h = a * 0x9E3779B97F4A7C15L ^ b * 0xC2B2AE3D27D4EB4FL ^ c * 0x165667B19E3779F9L ^ 0x5EEECE66DL;
        h ^= h >>> 33; h *= 0xFF51AFD7ED558CCDL; h ^= h >>> 33; h *= 0xC4CEB9FE1A85EC53L; h ^= h >>> 33;
        return h;
    }

    private static int bucket(long h, int n) { return (int) ((h >>> 1) % n); }

    /** Outpost centre {x, z} of the region containing block (x, z), or null. */
    public static int[] outpostFor(int x, int z) {
        int size = Math.max(6, ModConfig.selVoraOutpostRegionChunks) * 16;
        int rx = Math.floorDiv(x, size), rz = Math.floorDiv(z, size);
        if (bucket(mix(rx, rz, 701), 100) >= ModConfig.selVoraOutpostChance) return null;
        int margin = RADIUS + 8, span = size - 2 * margin;
        int vx = rx * size + margin + bucket(mix(rx, rz, 702), span), vz = rz * size + margin + bucket(mix(rx, rz, 703), span);
        if (Math.abs(vx) < 64 && Math.abs(vz) < 64) return null; // keep clear of the return gate
        return new int[] { vx, vz };
    }

    /** Is (x, z) within an outpost plus {@code pad} blocks? */
    public static boolean inside(int x, int z, int pad) {
        int[] v = outpostFor(x, z);
        if (v == null) return false;
        long dx = v[0] - x, dz = v[1] - z, r = RADIUS + pad;
        return dx * dx + dz * dz <= r * r;
    }

    public static void populate(World w, int cx, int cz) {
        int x0 = (cx << 4) + 8, z0 = (cz << 4) + 8;
        for (int lx = 0; lx < 16; lx++)
            for (int lz = 0; lz < 16; lz++) {
                int x = x0 + lx, z = z0 + lz;
                int[] v = outpostFor(x, z);
                if (v != null) column(w, x, z, v[0], v[1]);
            }
        int[] v = outpostFor(x0 + 8, z0 + 8);
        if (v != null && v[0] >= x0 && v[0] < x0 + 16 && v[1] >= z0 && v[1] < z0 + 16) spawnResidents(w, v[0], v[1]);
    }

    private static void set(World w, int x, int y, int z, IBlockState s) { w.setBlockState(new BlockPos(x, y, z), s, 2); }

    /** Console positions (offsets from the centre): N, E, S, W. */
    private static final int[][] CONSOLES = { { 0, -CONSOLE_R }, { CONSOLE_R, 0 }, { 0, CONSOLE_R }, { -CONSOLE_R, 0 } };

    private static void column(World w, int x, int z, int vx, int vz) {
        int dx = x - vx, dz = z - vz;
        double r = Math.sqrt(dx * dx + dz * dz);
        if (r > RADIUS) return;
        IBlockState stone = Registration.XENOLITH.getDefaultState(), tiles = Registration.XENOLITH_TILES.getDefaultState();
        IBlockState lamp = Registration.XENO_LAMP.getDefaultState(), air = Blocks.AIR.getDefaultState();
        IBlockState base = Blocks.STONE.getDefaultState();
        int y0 = FLOOR_Y;
        for (int y = y0 - 1, n = 0; y > 0 && n < 30; y--, n++) {
            BlockPos p = new BlockPos(x, y, z);
            if (w.getBlockState(p).isOpaqueCube() && w.getBlockState(p).getBlock() != Registration.MOON_REGOLITH) break;
            w.setBlockState(p, base, 2);
        }
        for (int y = y0 + 1; y <= y0 + 30; y++) if (!w.isAirBlock(new BlockPos(x, y, z))) set(w, x, y, z, air);
        int ax = Math.abs(dx), az = Math.abs(dz);
        IBlockState floor = r < DOME_R ? tiles : ((ax <= 1 || az <= 1) ? tiles : stone);
        if ((int) r == 14 && ((x + z) & 3) == 0) floor = lamp;
        set(w, x, y0, z, floor);
        if (r >= DOME_R) return;
        // dome shell (one block thick), lamp ring at mid height, four doors
        double outer = Math.sqrt(DOME_R * DOME_R - r * r) * 0.85, inner = r < DOME_R - 1 ? Math.sqrt((DOME_R - 1) * (DOME_R - 1) - r * r) * 0.85 : -1;
        boolean doorCol = (ax <= 1 || az <= 1) && r >= DOME_R - 1.5;
        int top = (int) Math.round(outer);
        for (int y = 1; y <= top; y++) {
            if (y <= inner) continue;
            if (doorCol && y <= 3) continue;
            set(w, x, y0 + y, z, y == 4 && !doorCol ? lamp : stone);
        }
        // ceiling light and antenna
        if (ax + az == 0) {
            set(w, x, y0 + top - 1, z, lamp);
            for (int y = y0 + top + 1; y <= y0 + top + 8; y++) set(w, x, y, z, y == y0 + top + 8 ? lamp : stone);
        }
        // centre: a Neutronium Ore block, studied by the researchers
        if (ax + az == 0) set(w, x, y0 + 1, z, Registration.NEUTRONIUM_ORE.getDefaultState());
        // research consoles on the floor
        for (int[] c : CONSOLES) if (dx == c[0] && dz == c[1]) set(w, x, y0 + 1, z, Registration.RESEARCH_CONSOLE.getDefaultState());
    }

    private static void spawnResidents(World w, int vx, int vz) {
        for (int[] c : CONSOLES) {
            // stand one block further out than the console, facing it
            double x = vx + 0.5 + c[0] * (CONSOLE_R + 1.5) / CONSOLE_R, z = vz + 0.5 + c[1] * (CONSOLE_R + 1.5) / CONSOLE_R;
            EntityAlienVillager v = new EntityAlienVillager(w);
            float yaw = (float) Math.toDegrees(Math.atan2(-(vx + 0.5 + c[0] - x), vz + 0.5 + c[1] - z));
            v.setLocationAndAngles(x, FLOOR_Y + 1, z, yaw, 0F);
            v.rotationYawHead = yaw; v.renderYawOffset = yaw;
            v.setResearching(true);
            v.enablePersistence();
            w.spawnEntity(v);
        }
        for (int k = 0; k < 2; k++) {
            EntityXenolithDrone d = new EntityXenolithDrone(w);
            d.setLocationAndAngles(vx + 0.5 + (k == 0 ? 13 : -13), FLOOR_Y + 10, vz + 0.5, 0F, 0F);
            d.enablePersistence();
            w.spawnEntity(d);
        }
    }
}
