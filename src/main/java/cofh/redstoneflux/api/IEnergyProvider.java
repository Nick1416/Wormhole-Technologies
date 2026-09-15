package cofh.redstoneflux.api;

import net.minecraft.util.EnumFacing;

/** Minimal RF API: provider (things fluxducts pull from) */
public interface IEnergyProvider extends IEnergyConnection {
    int extractEnergy(EnumFacing from, int maxExtract, boolean simulate);
    int getEnergyStored(EnumFacing from);
    int getMaxEnergyStored(EnumFacing from);
}
