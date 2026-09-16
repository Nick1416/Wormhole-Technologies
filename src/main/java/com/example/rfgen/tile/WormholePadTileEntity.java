package com.example.rfgen.tile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

public class WormholePadTileEntity extends TileEntity implements ITickable {

    public static final int BUFFER = 2_000_000;
    public static final int MAX_RECEIVE = 100_000;
    public static final int UPKEEP_RF_PER_TICK = 50_000;
    public static final int TELEPORT_COOLDOWN_TICKS = 60;
    private static final String CD_KEY = "rfgenPadCD";

    private final PadEnergyStorage energy = new PadEnergyStorage(BUFFER, MAX_RECEIVE);

    private boolean linked;
    private int partnerX;
    private int partnerY;
    private int partnerZ;
    private int partnerDim;
    private boolean powered;

    @Override
    public void update() {
        if (world == null || world.isRemote) return;

        boolean wasPowered = powered;
        if (linked) {
            if (energy.getEnergyStored() >= UPKEEP_RF_PER_TICK) {
                energy.consume(UPKEEP_RF_PER_TICK);
                powered = true;
            } else {
                powered = false;
            }
            markDirty();
        } else {
            powered = false;
        }

        if (wasPowered != powered) {
            markDirty();
        }
    }

    public boolean isLinked() {
        return linked;
    }

    public boolean isPowered() {
        return powered;
    }

    public BlockPos getPartnerPos() {
        return linked ? new BlockPos(partnerX, partnerY, partnerZ) : null;
    }

    public int getPartnerDim() {
        return partnerDim;
    }

    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    /** Clear this pad's link without touching the partner. */
    public void clearLinkLocal() {
        linked = false;
        partnerX = partnerY = partnerZ = 0;
        partnerDim = 0;
        powered = false;
        markDirty();
    }

    /** Unlink and clear partner if it still points here. */
    public void unlinkAndNotifyPartner() {
        if (!linked || world == null || world.isRemote) {
            clearLinkLocal();
            return;
        }
        BlockPos other = new BlockPos(partnerX, partnerY, partnerZ);
        int dim = partnerDim;
        clearLinkLocal();
        if (dim == world.provider.getDimension() && world.isBlockLoaded(other)) {
            TileEntity te = world.getTileEntity(other);
            if (te instanceof WormholePadTileEntity) {
                WormholePadTileEntity partner = (WormholePadTileEntity) te;
                if (partner.linked
                        && partner.partnerX == pos.getX()
                        && partner.partnerY == pos.getY()
                        && partner.partnerZ == pos.getZ()
                        && partner.partnerDim == world.provider.getDimension()) {
                    partner.clearLinkLocal();
                }
            }
        }
    }

    /**
     * Bidirectional same-dimension link. Clears any previous links on both pads
     * (and their old partners).
     */
    public static boolean linkPads(WormholePadTileEntity a, WormholePadTileEntity b) {
        if (a == null || b == null || a.world == null || b.world == null) return false;
        if (a.world.provider.getDimension() != b.world.provider.getDimension()) return false;
        if (a.pos.equals(b.pos)) return false;

        a.unlinkAndNotifyPartner();
        b.unlinkAndNotifyPartner();

        a.linked = true;
        a.partnerX = b.pos.getX();
        a.partnerY = b.pos.getY();
        a.partnerZ = b.pos.getZ();
        a.partnerDim = b.world.provider.getDimension();
        a.markDirty();

        b.linked = true;
        b.partnerX = a.pos.getX();
        b.partnerY = a.pos.getY();
        b.partnerZ = a.pos.getZ();
        b.partnerDim = a.world.provider.getDimension();
        b.markDirty();

        return true;
    }

    public boolean tryTeleport(EntityLivingBase entity) {
        if (world == null || world.isRemote) return false;
        if (!linked || !powered) return false;
        if (partnerDim != world.provider.getDimension()) return false;

        NBTTagCompound data = entity.getEntityData();
        long now = world.getTotalWorldTime();
        if (data.getLong(CD_KEY) > now) return false;

        BlockPos destPad = new BlockPos(partnerX, partnerY, partnerZ);
        if (!world.isBlockLoaded(destPad)) return false;

        TileEntity te = world.getTileEntity(destPad);
        if (!(te instanceof WormholePadTileEntity)) return false;
        WormholePadTileEntity partner = (WormholePadTileEntity) te;
        if (!partner.linked || !partner.powered) return false;
        if (partner.partnerDim != world.provider.getDimension()) return false;
        if (partner.partnerX != pos.getX() || partner.partnerY != pos.getY() || partner.partnerZ != pos.getZ()) {
            return false;
        }

        double x = destPad.getX() + 0.5D;
        double y = destPad.getY() + 1.0D;
        double z = destPad.getZ() + 0.5D;
        float yaw = entity.rotationYaw;
        float pitch = entity.rotationPitch;

        if (entity instanceof EntityPlayerMP) {
            ((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, yaw, pitch);
        } else {
            entity.setPositionAndUpdate(x, y, z);
            entity.rotationYaw = yaw;
            entity.rotationPitch = pitch;
        }

        data.setLong(CD_KEY, now + TELEPORT_COOLDOWN_TICKS);
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Energy", energy.getEnergyStored());
        tag.setBoolean("Linked", linked);
        tag.setInteger("PartnerX", partnerX);
        tag.setInteger("PartnerY", partnerY);
        tag.setInteger("PartnerZ", partnerZ);
        tag.setInteger("PartnerDim", partnerDim);
        tag.setBoolean("Powered", powered);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        energy.setEnergy(tag.getInteger("Energy"));
        linked = tag.getBoolean("Linked");
        partnerX = tag.getInteger("PartnerX");
        partnerY = tag.getInteger("PartnerY");
        partnerZ = tag.getInteger("PartnerZ");
        partnerDim = tag.getInteger("PartnerDim");
        powered = tag.getBoolean("Powered");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) return (T) energy;
        return super.getCapability(capability, facing);
    }

    /** Receive-only Forge energy buffer; internal consume for upkeep. */
    private static class PadEnergyStorage extends EnergyStorage {
        public PadEnergyStorage(int capacity, int maxReceive) {
            super(capacity, maxReceive, 0);
        }

        public void setEnergy(int value) {
            this.energy = Math.min(Math.max(0, value), getMaxEnergyStored());
        }

        public int consume(int amount) {
            int taken = Math.min(amount, this.energy);
            this.energy -= taken;
            return taken;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        @Override
        public boolean canExtract() {
            return false;
        }
    }
}
