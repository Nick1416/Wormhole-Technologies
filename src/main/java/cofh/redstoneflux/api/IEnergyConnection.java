package cofh.redstoneflux.api;

import net.minecraft.util.EnumFacing;

/** Minimal RF API: connection marker */
public interface IEnergyConnection {
    boolean canConnectEnergy(EnumFacing from);
}
