package com.nick1416.wormholetech.derelict;

import java.util.ArrayDeque;

/**
 * Generates the wall layout of a 30x30-chunk area around the hub (chunks -15..14) and flood-fills the
 * walkable floor from the hub. Fails (exit 1) unless 100% of walkable cells are reachable.
 * Furniture (modulator cradle, hub gate) is treated as solid too.
 * Run: ./gradlew derelictConnectivity
 */
public final class DerelictConnectivityCheck {
    public static void main(String[] args) {
        int r = args.length > 0 ? Integer.parseInt(args[0]) : 15;
        int lo = -r * 16, n = 2 * r * 16;
        boolean[] walk = new boolean[n * n];
        int total = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                int x = lo + i, z = lo + j;
                boolean ok = !DerelictLayout.wallAt(x, z) && !furniture(x, z);
                walk[i * n + j] = ok;
                if (ok) total++;
            }
        boolean[] seen = new boolean[n * n];
        ArrayDeque<int[]> q = new ArrayDeque<>();
        int si = 3 - lo, sj = 3 - lo; // hub corner (3, 3)
        seen[si * n + sj] = true;
        q.add(new int[] { si, sj });
        int reached = 0;
        int[][] d = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        while (!q.isEmpty()) {
            int[] c = q.poll();
            reached++;
            for (int[] k : d) {
                int a = c[0] + k[0], b = c[1] + k[1];
                if (a < 0 || b < 0 || a >= n || b >= n) continue;
                int id = a * n + b;
                if (walk[id] && !seen[id]) { seen[id] = true; q.add(new int[] { a, b }); }
            }
        }
        int mods = 0;
        for (int x = -r; x < r; x++) for (int z = -r; z < r; z++) if (DerelictLayout.chunkType(x, z) == DerelictLayout.TYPE_MODULATOR) mods++;
        System.out.printf("chunks=%dx%d walkable=%d reachable=%d share=%.4f%% modulatorRooms=%d%n", 2 * r, 2 * r, total, reached, 100.0 * reached / total, mods);
        java.util.Random rnd = new java.util.Random(1);
        int[] ds = new int[1000];
        for (int i = 0; i < ds.length; i++) {
            int px = rnd.nextInt(8000) - 4000, pz = rnd.nextInt(8000) - 4000;
            int[] c = DerelictLayout.findModulatorRoom(px, pz, 96);
            ds[i] = c == null ? -1 : (int) (Math.hypot(c[0] - px, c[1] - pz) * 16);
        }
        java.util.Arrays.sort(ds);
        int[] hub = DerelictLayout.findModulatorRoom(0, 0, 96);
        System.out.printf("nearest hall from random point (blocks): median=%d p90=%d max=%d; from hub=%d%n", ds[500], ds[900], ds[999],
                (int) (Math.hypot(hub[0], hub[1]) * 16));
        if (reached != total) System.exit(1);
    }

    private static boolean furniture(int x, int z) {
        int cx = x >> 4, cz = z >> 4, lx = x & 15, lz = z & 15;
        int t = DerelictLayout.chunkType(cx, cz);
        if (DerelictLayout.isHallCentre(cx, cz)) return Math.abs(lx - 8) <= 1 && Math.abs(lz - 8) <= 1;
        if (t == DerelictLayout.TYPE_HUB) return lz == 8 && lx >= 6 && lx <= 9;
        return false;
    }
}
