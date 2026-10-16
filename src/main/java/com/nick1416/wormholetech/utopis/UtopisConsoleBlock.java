package com.nick1416.wormholetech.utopis;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** A dead city's archive terminal on Utopis. Indestructible; right-click to read its logs. */
public class UtopisConsoleBlock extends Block {
    public static final int ENTRIES = 8;

    public UtopisConsoleBlock() {
        super(Material.IRON, MapColor.GREEN);
        setBlockUnbreakable();
        setResistance(6000000.0F);
        setSoundType(SoundType.METAL);
        setLightLevel(6.0F / 15.0F);
    }

    /** Each console opens the archive at its own entry. */
    public static int entryAt(BlockPos p) { return Math.floorMod(p.getX() * 31 + p.getZ() * 17 + p.getY(), ENTRIES); }

    @Override
    public boolean onBlockActivated(World w, BlockPos pos, IBlockState s, EntityPlayer p, EnumHand hand, EnumFacing f, float hx, float hy, float hz) {
        if (hand != EnumHand.MAIN_HAND) return true;
        if (w.isRemote) UtopisConsoleClient.open(entryAt(pos));
        return true;
    }
}
