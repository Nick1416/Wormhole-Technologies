package com.nick1416.wormholetech.elyndra;

import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.stargate.GateShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The gate to Elyndra: needs two arrays of 3 chained Neutronium Superconductors, each array rooted on a
 * different titanium frame block, and every superconductor touching its own Quantum Field Modulator (6 total).
 * Igniting it draws on the world's Neutronium: 3 lightning strikes blow craters down to the void.
 */
public final class FarGate {
    private static final int MAX_CHAINS = 256;

    private FarGate() {}

    private static boolean sc(World w, BlockPos p) { return w.getBlockState(p).getBlock() == Registration.NEUTRONIUM_SUPERCONDUCTOR_BLOCK; }

    public static boolean arraysPowered(World w, GateShape g) {
        if (g == null) return false;
        List<BlockPos[]> chains = new ArrayList<>(); // [frame, s1, s2, s3]
        for (int y = -1; y <= g.height; y++)
            for (int x = -1; x <= g.width; x++) {
                if (!(y == -1 || y == g.height || x == -1 || x == g.width)) continue;
                BlockPos f = g.interior(x, y);
                for (EnumFacing a : EnumFacing.VALUES) {
                    BlockPos s1 = f.offset(a);
                    if (!sc(w, s1) || g.isFrameCell(s1)) continue;
                    for (EnumFacing b : EnumFacing.VALUES) {
                        BlockPos s2 = s1.offset(b);
                        if (!sc(w, s2)) continue;
                        for (EnumFacing c : EnumFacing.VALUES) {
                            BlockPos s3 = s2.offset(c);
                            if (!sc(w, s3) || s3.equals(s1)) continue;
                            if (chains.size() < MAX_CHAINS) chains.add(new BlockPos[] { f, s1, s2, s3 });
                        }
                    }
                }
            }
        for (int i = 0; i < chains.size(); i++)
            for (int j = i + 1; j < chains.size(); j++) {
                BlockPos[] a = chains.get(i), b = chains.get(j);
                if (a[0].equals(b[0])) continue;
                Set<BlockPos> six = new HashSet<>();
                for (int k = 1; k <= 3; k++) { six.add(a[k]); six.add(b[k]); }
                if (six.size() != 6) continue;
                if (match(w, new ArrayList<>(six), 0, new HashSet<>())) return true;
            }
        return false;
    }

    /** Give every superconductor its own adjacent Quantum Field Modulator. */
    private static boolean match(World w, List<BlockPos> scs, int i, Set<BlockPos> used) {
        if (i == scs.size()) return true;
        for (EnumFacing d : EnumFacing.VALUES) {
            BlockPos q = scs.get(i).offset(d);
            if (used.contains(q) || w.getBlockState(q).getBlock() != Registration.QUANTUM_FIELD_MODULATOR) continue;
            used.add(q);
            if (match(w, scs, i + 1, used)) return true;
            used.remove(q);
        }
        return false;
    }

    /** 3 lightning strikes around the gate, each blasting a crater open to the void. */
    public static void ignite(WorldServer w, GateShape g, BlockPos computer) {
        BlockPos c = g.interior(g.width / 2, 0);
        double base = w.rand.nextDouble() * Math.PI * 2;
        IBlockState air = Blocks.AIR.getDefaultState();
        for (int i = 0; i < 3; i++) {
            double ang = base + i * Math.PI * 2 / 3 + (w.rand.nextDouble() - 0.5) * 0.6;
            int dist = 10 + w.rand.nextInt(5);
            int x = c.getX() + (int) Math.round(Math.cos(ang) * dist), z = c.getZ() + (int) Math.round(Math.sin(ang) * dist);
            BlockPos top = w.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            w.addWeatherEffect(new EntityLightningBolt(w, x + 0.5, top.getY(), z + 0.5, true));
            w.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x + 0.5, top.getY() + 1, z + 0.5, 4, 1.5, 1.0, 1.5, 0.0);
            w.playSound(null, top, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, 0.7F);
            w.playSound(null, top, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 6.0F, 0.8F);
            for (int dx = -3; dx <= 3; dx++)
                for (int dz = -3; dz <= 3; dz++) {
                    if (dx * dx + dz * dz > 10) continue;
                    int cx = x + dx, cz = z + dz;
                    // never touch the gate, its arrays or the computer
                    if (Math.abs(cx - c.getX()) <= g.width + 4 && Math.abs(cz - c.getZ()) <= g.width + 4) continue;
                    for (int y = Math.min(255, top.getY() + 4); y >= 0; y--) {
                        BlockPos p = new BlockPos(cx, y, cz);
                        if (w.getTileEntity(p) != null || p.equals(computer)) continue;
                        if (!w.isAirBlock(p)) w.setBlockState(p, air, 2 | 16);
                    }
                }
        }
    }
}
