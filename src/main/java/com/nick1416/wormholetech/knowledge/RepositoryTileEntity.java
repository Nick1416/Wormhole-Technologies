package com.nick1416.wormholetech.knowledge;

import net.minecraft.tileentity.TileEntity;

/** Marks a Repository of Knowledge for research drones; also drives the floating crystal renderer. */
public class RepositoryTileEntity extends TileEntity {
    @Override public void onLoad() { RepositoryTracker.add(world, pos); }
    @Override public void invalidate() { super.invalidate(); RepositoryTracker.remove(world, pos); }
    @Override public void onChunkUnload() { RepositoryTracker.remove(world, pos); }
}
