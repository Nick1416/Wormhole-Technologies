package com.nick1416.wormholetech.stargate;

import com.nick1416.wormholetech.registry.Registration;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * A Nether-portal-like rectangle: frame blocks around an empty interior, in the X or Z plane.
 * Interior 2..21 wide and 3..21 tall (outer 4x5 up to 23x23, like vanilla). Corners are optional.
 */
public final class GateShape {
    public static final int MIN_W = 2, MIN_H = 3, MAX = 21;

    public final BlockPos bottomLeft; // lowest interior cell on the "left" (negative) side
    public final EnumFacing.Axis axis;
    public final int width, height;

    public GateShape(BlockPos bottomLeft, EnumFacing.Axis axis, int width, int height) {
        this.bottomLeft = bottomLeft;
        this.axis = axis;
        this.width = width;
        this.height = height;
    }

    public EnumFacing right() { return axis == EnumFacing.Axis.X ? EnumFacing.EAST : EnumFacing.SOUTH; }

    public BlockPos interior(int x, int y) { return bottomLeft.offset(right(), x).up(y); }

    /** Floor cell in front of / behind the gate centre, {@code side} = +1 or -1, {@code dist} blocks out. */
    public BlockPos front(int side, int dist) {
        EnumFacing out = axis == EnumFacing.Axis.X ? EnumFacing.SOUTH : EnumFacing.EAST;
        return interior(width / 2, 0).offset(out, side * dist);
    }

    public boolean isFrameCell(BlockPos p) {
        for (int y = -1; y <= height; y++) {
            for (int x = -1; x <= width; x++) {
                boolean edge = y == -1 || y == height || x == -1 || x == width;
                if (edge && interior(x, y).equals(p)) return true;
            }
        }
        return false;
    }

    static boolean isEmpty(World w, BlockPos p) {
        IBlockState s = w.getBlockState(p);
        return s.getBlock() == Registration.STARGATE_PORTAL || w.isAirBlock(p);
    }

    /** Detect a gate whose interior contains {@code inside}. */
    @Nullable
    public static GateShape detect(World w, BlockPos inside, EnumFacing.Axis axis, Predicate<IBlockState> frame) {
        if (axis == EnumFacing.Axis.Y || !isEmpty(w, inside)) return null;
        EnumFacing right = axis == EnumFacing.Axis.X ? EnumFacing.EAST : EnumFacing.SOUTH;
        EnumFacing left = right.getOpposite();

        BlockPos p = inside;
        for (int i = 0; i < MAX && p.getY() > 1 && isEmpty(w, p.down()); i++) p = p.down();
        if (!frame.apply(w.getBlockState(p.down()))) return null;
        for (int i = 0; i < MAX && isEmpty(w, p.offset(left)); i++) p = p.offset(left);
        if (!frame.apply(w.getBlockState(p.offset(left)))) return null;
        BlockPos bl = p;

        int width = 0;
        while (width <= MAX && isEmpty(w, bl.offset(right, width))) width++;
        if (width < MIN_W || width > MAX) return null;
        int height = 0;
        while (height <= MAX && isEmpty(w, bl.up(height))) height++;
        if (height < MIN_H || height > MAX) return null;

        GateShape g = new GateShape(bl, axis, width, height);
        for (int y = 0; y < height; y++) {
            if (!frame.apply(w.getBlockState(g.interior(-1, y)))) return null;
            if (!frame.apply(w.getBlockState(g.interior(width, y)))) return null;
            for (int x = 0; x < width; x++) if (!isEmpty(w, g.interior(x, y))) return null;
        }
        for (int x = 0; x < width; x++) {
            if (!frame.apply(w.getBlockState(g.interior(x, -1)))) return null;
            if (!frame.apply(w.getBlockState(g.interior(x, height)))) return null;
        }
        return g;
    }

    /** A gate whose frame includes {@code frameBlock}. */
    @Nullable
    public static GateShape findFromFrameBlock(World w, BlockPos frameBlock, Predicate<IBlockState> frame) {
        if (!frame.apply(w.getBlockState(frameBlock))) return null;
        for (EnumFacing.Axis axis : new EnumFacing.Axis[] { EnumFacing.Axis.X, EnumFacing.Axis.Z }) {
            EnumFacing right = axis == EnumFacing.Axis.X ? EnumFacing.EAST : EnumFacing.SOUTH;
            // the interior is beside an edge block, or diagonal to a corner block
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dy == 0) continue;
                    BlockPos in = frameBlock.offset(right, dx).up(dy);
                    GateShape g = detect(w, in, axis, frame);
                    if (g != null && g.isFrameCell(frameBlock)) return g;
                }
            }
        }
        return null;
    }

    public NBTTagCompound write() {
        NBTTagCompound t = new NBTTagCompound();
        t.setLong("Pos", bottomLeft.toLong());
        t.setBoolean("AxisX", axis == EnumFacing.Axis.X);
        t.setInteger("W", width);
        t.setInteger("H", height);
        return t;
    }

    @Nullable
    public static GateShape read(NBTTagCompound t) {
        if (t == null || !t.hasKey("Pos")) return null;
        return new GateShape(BlockPos.fromLong(t.getLong("Pos")),
                t.getBoolean("AxisX") ? EnumFacing.Axis.X : EnumFacing.Axis.Z, t.getInteger("W"), t.getInteger("H"));
    }
}
