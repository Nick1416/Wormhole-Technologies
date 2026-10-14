package com.nick1416.wormholetech.block;

import com.nick1416.wormholetech.tile.DialingComputerTileEntity;
import com.nick1416.wormholetech.tile.GateNetworkComputerTileEntity;

public class GateNetworkComputerBlock extends DialingComputerBlock {
    @Override protected DialingComputerTileEntity newTile() { return new GateNetworkComputerTileEntity(); }
}
