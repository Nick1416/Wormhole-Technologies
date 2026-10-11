package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

/** Registers P3X-1416 under the configured (stable) dimension id. */
public final class ModDimensions {
    public static final String NAME = "p3x_1416";
    public static DimensionType P3X;

    private ModDimensions() {}

    public static void register() {
        if (!ModConfig.stargateEnabled) return;
        int id = ModConfig.p3xDimensionId;
        if (DimensionManager.isDimensionRegistered(id)) {
            WormholeTech.LOGGER.error("Dimension id {} is already taken; P3X-1416 and the gate are disabled. "
                    + "Change p3xDimensionId in the config.", id);
            return;
        }
        P3X = DimensionType.register(NAME, "_" + NAME, id, WorldProviderP3X.class, false);
        DimensionManager.registerDimension(id, P3X);
    }

    public static boolean isRegistered() { return P3X != null; }

    public static int id() { return ModConfig.p3xDimensionId; }
}
