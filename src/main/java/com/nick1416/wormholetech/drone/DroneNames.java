package com.nick1416.wormholetech.drone;

import net.minecraft.util.text.translation.I18n;

final class DroneNames {
    private DroneNames() {}
    @SuppressWarnings("deprecation")
    static String program(DroneProgram p) { return I18n.translateToLocal("program.wormholetech." + p.id); }
}
