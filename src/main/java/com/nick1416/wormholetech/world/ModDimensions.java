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

    public static final String ELYNDRA_NAME = "elyndra";
    public static DimensionType ELYNDRA;

    /** Elyndra, home world of the gate builders on the far side of the universe (one-way gate trip). */
    public static void registerElyndra() {
        if (!ModConfig.stargateEnabled) return;
        int id = ModConfig.elyndraDimensionId;
        if (DimensionManager.isDimensionRegistered(id)) {
            WormholeTech.LOGGER.error("Dimension id {} is already taken; Elyndra is disabled. Change elyndraDimensionId in the config.", id);
            return;
        }
        ELYNDRA = DimensionType.register(ELYNDRA_NAME, "_" + ELYNDRA_NAME, id,
                com.nick1416.wormholetech.elyndra.WorldProviderElyndra.class, false);
        DimensionManager.registerDimension(id, ELYNDRA);
    }

    public static boolean isElyndraRegistered() { return ELYNDRA != null; }

    public static int elyndraId() { return ModConfig.elyndraDimensionId; }

    public static boolean isElyndra(int dim) { return isElyndraRegistered() && dim == elyndraId(); }

    public static final String UTOPIS_NAME = "utopis";
    public static DimensionType UTOPIS;

    /** Utopis: the failed utopia near the Milky Way's central black hole, homeworld of the Emerald Drones. */
    public static void registerUtopis() {
        if (!ModConfig.stargateEnabled) return;
        int id = ModConfig.utopisDimensionId;
        if (DimensionManager.isDimensionRegistered(id)) {
            WormholeTech.LOGGER.error("Dimension id {} is already taken; Utopis is disabled. Change utopisDimensionId in the config.", id);
            return;
        }
        UTOPIS = DimensionType.register(UTOPIS_NAME, "_" + UTOPIS_NAME, id, com.nick1416.wormholetech.utopis.WorldProviderUtopis.class, false);
        DimensionManager.registerDimension(id, UTOPIS);
    }

    public static boolean isUtopisRegistered() { return UTOPIS != null; }
    public static int utopisId() { return ModConfig.utopisDimensionId; }
    public static boolean isUtopis(int dim) { return isUtopisRegistered() && dim == utopisId(); }

    /** Worlds rich in Neutronium (Neutronium Ore generates there): the far gate can draw power from them. */
    public static boolean isNeutroniumWorld(int dim) { return isSelVoraRegistered() && dim == selVoraId(); }

    /** Worlds reached through a gate, with a return gate (Elyndra has none: it is one-way). */
    /** Galaxy of a dimension: 2 = outside every galaxy (Elyndra), 1 = Altra Galaxy (Tal'Vora, Sel'Vora), 0 = Milky Way (everything else). */
    public static int galaxy(int dim) {
        if (isElyndra(dim)) return 2;
        return (isTalVoraRegistered() && dim == talVoraId()) || (isSelVoraRegistered() && dim == selVoraId()) ? 1 : 0;
    }

    public static boolean isGateWorld(int dim) {
        return (isRegistered() && dim == id()) || (isDerelictRegistered() && dim == derelictId())
                || (isTalVoraRegistered() && dim == talVoraId())
                || (isSelVoraRegistered() && dim == selVoraId()) || isUtopis(dim);
    }

    public static boolean isRegistered() { return P3X != null; }

    public static int id() { return ModConfig.p3xDimensionId; }
}
