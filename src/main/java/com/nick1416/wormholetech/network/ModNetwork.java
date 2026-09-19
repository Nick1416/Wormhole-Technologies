package com.nick1416.wormholetech.network;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class ModNetwork {
    public static final SimpleNetworkWrapper CHANNEL =
            NetworkRegistry.INSTANCE.newSimpleChannel(WormholeTech.MODID);

    private ModNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(
                MessageRequestTablet.Handler.class,
                MessageRequestTablet.class,
                0,
                Side.SERVER);
    }
}
