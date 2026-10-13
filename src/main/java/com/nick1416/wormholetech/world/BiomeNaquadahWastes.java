package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.block.NaquadahCrystalBlock;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * The only biome of P3X-1416: steep Naquadric Stone hills under Ashen Regolith, with gravel and
 * bare-rock patches, glowing Naquadah Crystals, rock spires, the odd floating rock and Ancient Stone
 * obelisks/ruins. All features stay inside the chunk's population area (no cascading generation)
 * and keep clear of the return gate near (0, 0).
 */
public class BiomeNaquadahWastes extends Biome {

    /** Spires, floating rocks and ruins never start within this many blocks (x and z) of the return gate at (0, 0). */
    private static final int GATE_CLEARANCE = 32;

    private static boolean nearGate(BlockPos p) {
        return Math.abs(p.getX()) < GATE_CLEARANCE && Math.abs(p.getZ()) < GATE_CLEARANCE;
    }

    public BiomeNaquadahWastes() {
        super(new BiomeProperties("Naquadah Wastes")
                .setBaseHeight(0.7F).setHeightVariation(0.85F)
                .setTemperature(1.6F).setRainfall(0.0F).setRainDisabled()
                .setWaterColor(0x3FCFC4));
        topBlock = Registration.ASHEN_REGOLITH.getDefaultState();
        fillerBlock = Registration.ASHEN_REGOLITH.getDefaultState();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        decorator.treesPerChunk = -999;
        decorator.flowersPerChunk = 0;
        decorator.grassPerChunk = 0;
        decorator.deadBushPerChunk = 0;
        decorator.reedsPerChunk = 0;
        decorator.cactiPerChunk = 0;
        decorator.mushroomsPerChunk = 0;
        decorator.generateFalls = false;
    }

    /** No vanilla ore/dirt/gravel/stone-variant veins: P3X-1416 has Naquadah Ore instead. */
    @Override
    public net.minecraft.world.biome.BiomeDecorator createBiomeDecorator() {
        return getModdedBiomeDecorator(new net.minecraft.world.biome.BiomeDecorator() {
            @Override
            protected void generateOres(World worldIn, Random random) { }
        });
    }

    @Override
    public void genTerrainBlocks(World world, Random rand, ChunkPrimer primer, int x, int z, double noise) {
        IBlockState regolith = Registration.ASHEN_REGOLITH.getDefaultState();
        if (noise > 2.3D) {
            topBlock = Blocks.STONE.getDefaultState(); // becomes Naquadric Stone: bare rock ridges
            fillerBlock = Blocks.STONE.getDefaultState();
        } else if (noise < -2.5D) {
            topBlock = Blocks.GRAVEL.getDefaultState();
            fillerBlock = regolith;
        } else {
            topBlock = regolith;
            fillerBlock = regolith;
        }
        generateBiomeTerrain(world, rand, primer, x, z, noise);
    }

    @Override
    public void decorate(World world, Random rand, BlockPos chunkOrigin) {
        super.decorate(world, rand, chunkOrigin);
        int cx = chunkOrigin.getX(), cz = chunkOrigin.getZ();

        if (ModConfig.p3xSurfaceOreClusterChance > 0 && rand.nextInt(ModConfig.p3xSurfaceOreClusterChance) == 0) {
            surfaceOre(world, rand, surface(world, cx + 8 + rand.nextInt(16), cz + 8 + rand.nextInt(16)));
        }
        for (int i = 0; i < ModConfig.p3xCrystalsPerChunk; i++) {
            int x = cx + 8 + rand.nextInt(16), z = cz + 8 + rand.nextInt(16);
            if (i % 3 == 0) crystal(world, surface(world, x, z));
            else caveCrystal(world, rand, x, 8 + rand.nextInt(52), z);
        }
        if (ModConfig.p3xSpireChance > 0 && rand.nextInt(ModConfig.p3xSpireChance) == 0) {
            BlockPos base = surface(world, cx + 10 + rand.nextInt(12), cz + 10 + rand.nextInt(12));
            boolean floating = rand.nextInt(3) == 0;
            if (!nearGate(base)) {
                if (floating) floatingRock(world, rand, base.up(16 + rand.nextInt(14)));
                else spire(world, rand, base);
            }
        }
        if (ModConfig.p3xCrystalCaveChance > 0 && rand.nextInt(ModConfig.p3xCrystalCaveChance) == 0) {
            P3XTemples.crystalCave(world, rand, new BlockPos(cx + 16, 14 + rand.nextInt(24), cz + 16));
        }
        if (ModConfig.p3xGuardianTempleChance > 0 && rand.nextInt(ModConfig.p3xGuardianTempleChance) == 0) {
            BlockPos base = surface(world, cx + 16, cz + 16);
            if (!nearGate(base) && !world.getBlockState(base.down()).getMaterial().isLiquid()) {
                P3XTemples.guardianTemple(world, rand, base);
                return;
            }
        }
        if (ModConfig.p3xTempleChance > 0 && rand.nextInt(ModConfig.p3xTempleChance) == 0) {
            BlockPos base = surface(world, cx + 12 + rand.nextInt(8), cz + 12 + rand.nextInt(8));
            if (!nearGate(base) && !world.getBlockState(base.down()).getMaterial().isLiquid()) {
                P3XTemples.generate(world, rand, base);
                return; // no obelisk on top of a temple
            }
        }
        if (ModConfig.p3xObeliskChance > 0 && rand.nextInt(ModConfig.p3xObeliskChance) == 0) {
            BlockPos base = surface(world, cx + 12 + rand.nextInt(8), cz + 12 + rand.nextInt(8));
            boolean ob = rand.nextBoolean();
            if (!nearGate(base)) {
                if (ob) obelisk(world, rand, base);
                else ruin(world, rand, base);
            }
        }
    }

    /** First free block above the ground column. */
    private static BlockPos surface(World w, int x, int z) {
        return w.getHeight(new BlockPos(x, 0, z));
    }

    private static boolean isGround(IBlockState s) {
        return s.getBlock() == Registration.NAQUADRIC_STONE || s.getBlock() == Registration.ASHEN_REGOLITH
                || s.getBlock() == Blocks.GRAVEL;
    }

    private static void set(World w, BlockPos p, IBlockState s) {
        if (p.getY() > 0 && p.getY() < 255) w.setBlockState(p, s, 2);
    }

    private static void surfaceOre(World w, Random rand, BlockPos top) {
        IBlockState ore = Registration.NATURAL_NAQUADAH_ORE.getDefaultState();
        int n = 3 + rand.nextInt(4);
        for (int i = 0; i < n; i++) {
            BlockPos p = top.add(rand.nextInt(3) - 1, -1 - rand.nextInt(2), rand.nextInt(3) - 1);
            if (isGround(w.getBlockState(p))) set(w, p, ore);
        }
    }

    private static void crystal(World w, BlockPos p) {
        if (w.isAirBlock(p) && NaquadahCrystalBlock.canStay(w, p)) set(w, p, Registration.NAQUADAH_CRYSTAL.getDefaultState());
    }

    private static void caveCrystal(World w, Random rand, int x, int y, int z) {
        for (int dy = 0; dy < 12 && y - dy > 4; dy++) {
            BlockPos p = new BlockPos(x, y - dy, z);
            if (w.isAirBlock(p) && w.canSeeSky(p)) return; // only underground
            if (w.isAirBlock(p) && NaquadahCrystalBlock.canStay(w, p)) {
                set(w, p, Registration.NAQUADAH_CRYSTAL.getDefaultState());
                return;
            }
        }
    }

    private static void spire(World w, Random rand, BlockPos base) {
        IBlockState stone = Registration.NAQUADRIC_STONE.getDefaultState();
        int h = 12 + rand.nextInt(22);
        float r0 = 1.6F + rand.nextFloat() * 1.4F;
        for (int dy = -3; dy <= h; dy++) {
            float r = dy <= 0 ? r0 + 0.5F : r0 * (1.0F - (float) dy / (h + 1)) + 0.45F;
            int ri = (int) Math.ceil(r);
            for (int dx = -ri; dx <= ri; dx++)
                for (int dz = -ri; dz <= ri; dz++)
                    if (dx * dx + dz * dz <= r * r) set(w, base.add(dx, dy, dz), stone);
        }
        if (rand.nextInt(3) == 0) set(w, base.up(h + 1), Registration.NAQUADAH_CRYSTAL.getDefaultState());
    }

    private static void floatingRock(World w, Random rand, BlockPos c) {
        IBlockState stone = Registration.NAQUADRIC_STONE.getDefaultState();
        IBlockState top = Registration.ASHEN_REGOLITH.getDefaultState();
        int rx = 2 + rand.nextInt(2), ry = 2 + rand.nextInt(2), rz = 2 + rand.nextInt(2);
        for (int dx = -rx; dx <= rx; dx++)
            for (int dz = -rz; dz <= rz; dz++)
                for (int dy = -ry; dy <= 1; dy++) {
                    // flat-topped, pointed underside
                    double k = (dx * dx) / (double) (rx * rx) + (dz * dz) / (double) (rz * rz) + (dy < 0 ? (dy * dy) / (double) (ry * ry) : 0);
                    if (k <= 1.0) set(w, c.add(dx, dy, dz), dy == 1 ? top : stone);
                }
        if (rand.nextBoolean()) set(w, c.up(2), Registration.NAQUADAH_CRYSTAL.getDefaultState());
        if (rand.nextInt(3) == 0) set(w, c, Registration.NATURAL_NAQUADAH_ORE.getDefaultState());
    }

    private static void obelisk(World w, Random rand, BlockPos base) {
        IBlockState anc = Registration.ANCIENT_STONE.getDefaultState();
        for (int dx = -1; dx <= 2; dx++)
            for (int dz = -1; dz <= 2; dz++)
                for (int dy = -3; dy <= 0; dy++) set(w, base.add(dx, dy, dz), anc);
        int h = 6 + rand.nextInt(5);
        for (int dy = 1; dy <= h; dy++)
            for (int dx = 0; dx <= 1; dx++)
                for (int dz = 0; dz <= 1; dz++) set(w, base.add(dx, dy, dz), anc);
        set(w, base.add(rand.nextInt(2), h + 1, rand.nextInt(2)), Registration.NAQUADAH_CRYSTAL.getDefaultState());
    }

    private static void ruin(World w, Random rand, BlockPos base) {
        IBlockState anc = Registration.ANCIENT_STONE.getDefaultState();
        IBlockState bricks = Blocks.STONEBRICK.getDefaultState();
        IBlockState cracked = Blocks.STONEBRICK.getStateFromMeta(2);
        int[][] posts = { { -3, -3 }, { 3, -3 }, { -3, 3 }, { 3, 3 }, { 0, -3 }, { 0, 3 }, { -3, 0 }, { 3, 0 } };
        for (int[] q : posts) {
            if (rand.nextInt(4) == 0) continue; // fallen
            BlockPos b = w.getHeight(base.add(q[0], 0, q[1]));
            int h = 1 + rand.nextInt(4);
            for (int dy = -2; dy < h; dy++) set(w, b.up(dy), dy == h - 1 && rand.nextBoolean() ? cracked : (rand.nextInt(3) == 0 ? bricks : anc));
        }
        set(w, w.getHeight(base), Registration.NAQUADAH_CRYSTAL.getDefaultState());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getSkyColorByTemp(float temp) { return 0x9466CC; }

    @SideOnly(Side.CLIENT)
    @Override
    public int getGrassColorAtPos(BlockPos pos) { return 0x6FA39A; }

    @SideOnly(Side.CLIENT)
    @Override
    public int getFoliageColorAtPos(BlockPos pos) { return 0x7E6FA8; }

    public static void addTypes() {
        BiomeDictionary.addTypes(Registration.NAQUADAH_WASTES, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY,
                BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.MAGICAL);
    }
}
