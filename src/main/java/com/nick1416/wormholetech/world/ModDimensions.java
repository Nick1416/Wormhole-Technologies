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

    public static final String TAL_VORA_NAME = "tal_vora";
    public static DimensionType TAL_VORA;

    /** Tal'Vora, the red desert planet in another galaxy (Intergalactic Coordinates). */
    public static void registerTalVora() {
        if (!ModConfig.stargateEnabled) return;
        int id = ModConfig.talVoraDimensionId;
        if (DimensionManager.isDimensionRegistered(id)) {
            WormholeTech.LOGGER.error("Dimension id {} is already taken; Tal'Vora is disabled. Change talVoraDimensionId in the config.", id);
            return;
        }
        TAL_VORA = DimensionType.register(TAL_VORA_NAME, "_" + TAL_VORA_NAME, id,
                com.nick1416.wormholetech.talvora.WorldProviderTalVora.class, false);
        DimensionManager.registerDimension(id, TAL_VORA);
    }

    public static boolean isTalVoraRegistered() { return TAL_VORA != null; }

    public static int talVoraId() { return ModConfig.talVoraDimensionId; }

    public static final String SEL_VORA_NAME = "sel_vora";
    public static DimensionType SEL_VORA;

    /** Sel'Vora, Tal'Vora's moon (address learned in the Repository of Knowledge). */
    public static void registerSelVora() {
        if (!ModConfig.stargateEnabled) return;
        int id = ModConfig.selVoraDimensionId;
        if (DimensionManager.isDimensionRegistered(id)) {
            WormholeTech.LOGGER.error("Dimension id {} is already taken; Sel'Vora is disabled. Change selVoraDimensionId in the config.", id);
            return;
        }
        SEL_VORA = DimensionType.register(SEL_VORA_NAME, "_" + SEL_VORA_NAME, id,
                com.nick1416.wormholetech.moon.WorldProviderSelVora.class, false);
        DimensionManager.registerDimension(id, SEL_VORA);
    }

    public static boolean isSelVoraRegistered() { return SEL_VORA != null; }

    public static int selVoraId() { return ModConfig.selVoraDimensionId; }

    /** Worlds reached through a gate, with a return gate. */
    /** Galaxy of a dimension: 1 = Tal'Vora galaxy (Tal'Vora, Sel'Vora), 0 = Milky Way (everything else). */
    public static int galaxy(int dim) {
        return (isTalVoraRegistered() && dim == talVoraId()) || (isSelVoraRegistered() && dim == selVoraId()) ? 1 : 0;
    }

    public static boolean isGateWorld(int dim) {
        return (isRegistered() && dim == id()) || (isDerelictRegistered() && dim == derelictId())
                || (isTalVoraRegistered() && dim == talVoraId())
                || (isSelVoraRegistered() && dim == selVoraId());
    }

    public static boolean isRegistered() { return P3X != null; }

    public static int id() { return ModConfig.p3xDimensionId; }
}
