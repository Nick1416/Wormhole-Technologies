package com.nick1416.wormholetech.world;

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

/** Dry sand/gravel/stone plains with sparse dead bushes. Only used by P3X-1416. */
public class BiomeNaquadahWastes extends Biome {

    private static final IBlockState SAND = Blocks.SAND.getDefaultState();
    private static final IBlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    private static final IBlockState STONE = Blocks.STONE.getDefaultState();

    public BiomeNaquadahWastes() {
        super(new BiomeProperties("Naquadah Wastes")
                .setBaseHeight(0.2F).setHeightVariation(0.15F)
                .setTemperature(1.6F).setRainfall(0.0F).setRainDisabled()
                .setWaterColor(0x3FCFC4));
        topBlock = SAND;
        fillerBlock = SAND;
        spawnableCreatureList.clear();
        decorator.treesPerChunk = -999;
        decorator.flowersPerChunk = 0;
        decorator.grassPerChunk = 0;
        decorator.deadBushPerChunk = 2;
        decorator.reedsPerChunk = 0;
        decorator.cactiPerChunk = 0;
        decorator.generateFalls = false;
    }

    @Override
    public void genTerrainBlocks(World world, Random rand, ChunkPrimer primer, int x, int z, double noise) {
        if (noise > 1.6D) {
            topBlock = STONE;
            fillerBlock = STONE;
        } else if (noise < -1.4D) {
            topBlock = GRAVEL;
            fillerBlock = STONE;
        } else {
            topBlock = SAND;
            fillerBlock = SAND; // vanilla lines sand with sandstone underneath
        }
        generateBiomeTerrain(world, rand, primer, x, z, noise);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getSkyColorByTemp(float temp) { return 0x9ED9D2; }

    @SideOnly(Side.CLIENT)
    @Override
    public int getGrassColorAtPos(BlockPos pos) { return 0x9A9F6A; }

    @SideOnly(Side.CLIENT)
    @Override
    public int getFoliageColorAtPos(BlockPos pos) { return 0x8F9A5F; }

    public static void addTypes() {
        BiomeDictionary.addTypes(Registration.NAQUADAH_WASTES, BiomeDictionary.Type.HOT, BiomeDictionary.Type.DRY,
                BiomeDictionary.Type.SANDY, BiomeDictionary.Type.WASTELAND);
    }
}
