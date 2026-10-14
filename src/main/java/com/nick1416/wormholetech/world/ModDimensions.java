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

    public static final String DERELICT_NAME = "derelict_vessel";
    public static DimensionType DERELICT;

    /** The derelict vessel (second gate address). */
    public static void registerDerelict() {
        if (!ModConfig.stargateEnabled) return;
        int id = ModConfig.derelictDimensionId;
        if (DimensionManager.isDimensionRegistered(id)) {
            WormholeTech.LOGGER.error("Dimension id {} is already taken; the derelict vessel is disabled. "
                    + "Change derelictDimensionId in the config.", id);
            return;
        }
        DERELICT = DimensionType.register(DERELICT_NAME, "_" + DERELICT_NAME, id,
                com.nick1416.wormholetech.derelict.WorldProviderDerelict.class, false);
        DimensionManager.registerDimension(id, DERELICT);
    }

    public static boolean isDerelictRegistered() { return DERELICT != null; }

    public static int derelictId() { return ModConfig.derelictDimensionId; }

    /** P3X-1416 or the derelict vessel: worlds reached through a gate, with a return gate. */
    public static boolean isGateWorld(int dim) {
        return (isRegistered() && dim == id()) || (isDerelictRegistered() && dim == derelictId());
    }

    public static boolean isRegistered() { return P3X != null; }

    public static int id() { return ModConfig.p3xDimensionId; }
}
