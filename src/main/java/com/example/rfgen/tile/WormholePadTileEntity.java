package com.example.rfgen.tile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.entity.player.EntityPlayer;

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

    /** True if an adjacent Relativistic Computer is actively calculating. */
    public static boolean hasActiveComputer(net.minecraft.world.World world, BlockPos pos) {
        if (world == null || pos == null) return false;
        for (EnumFacing face : EnumFacing.VALUES) {
            TileEntity te = world.getTileEntity(pos.offset(face));
            if (te instanceof RelativisticComputerTileEntity
                    && ((RelativisticComputerTileEntity) te).isActive()) {
                return true;
            }
        }
        return false;
    }

    private static WormholePadTileEntity getPad(net.minecraft.world.World w, BlockPos p) {
        if (w == null || p == null || !w.isBlockLoaded(p)) return null;
        TileEntity te = w.getTileEntity(p);
        return te instanceof WormholePadTileEntity ? (WormholePadTileEntity) te : null;
    }

    private static net.minecraft.world.World resolveWorld(net.minecraft.world.World hint, int dim) {
        if (hint != null && hint.provider.getDimension() == dim) return hint;
        if (hint != null && hint.getMinecraftServer() != null) {
            return hint.getMinecraftServer().getWorld(dim);
        }
        return null;
    }

    /** Unlink and clear partner if it still points here (supports cross-dim). */
    public void unlinkAndNotifyPartner() {
        if (!linked || world == null || world.isRemote) {
            clearLinkLocal();
            return;
        }
        BlockPos other = new BlockPos(partnerX, partnerY, partnerZ);
        int dim = partnerDim;
        int myDim = world.provider.getDimension();
        BlockPos myPos = pos.toImmutable();
        clearLinkLocal();
        net.minecraft.world.World otherWorld = resolveWorld(world, dim);
        WormholePadTileEntity partner = getPad(otherWorld, other);
        if (partner != null && partner.linked
                && partner.partnerX == myPos.getX()
                && partner.partnerY == myPos.getY()
                && partner.partnerZ == myPos.getZ()
                && partner.partnerDim == myDim) {
            partner.clearLinkLocal();
        }
    }

    /**
     * Bidirectional link. Same-dimension always OK.
     * Cross-dimension requires an active Relativistic Computer adjacent to both pads.
     */
    public static boolean linkPads(WormholePadTileEntity a, WormholePadTileEntity b) {
        if (a == null || b == null || a.world == null || b.world == null) return false;
        if (a.world.provider.getDimension() == b.world.provider.getDimension() && a.pos.equals(b.pos)) {
            return false;
        }

        boolean cross = a.world.provider.getDimension() != b.world.provider.getDimension();
        if (cross) {
            if (!hasActiveComputer(a.world, a.pos) || !hasActiveComputer(b.world, b.pos)) {
                return false;
            }
        }

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

        NBTTagCompound data = entity.getEntityData();
        long now = world.getTotalWorldTime();
        if (data.getLong(CD_KEY) > now) return false;

        boolean cross = partnerDim != world.provider.getDimension();
        if (cross) {
            if (!(entity instanceof EntityPlayerMP)) return false;
            if (!hasActiveComputer(world, pos)) {
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer) entity).sendStatusMessage(
                            new TextComponentTranslation("message.rfgen.pad.need_computer"), true);
                }
                return false;
            }
        }

        net.minecraft.world.World destWorld = resolveWorld(world, partnerDim);
        BlockPos destPad = new BlockPos(partnerX, partnerY, partnerZ);
        WormholePadTileEntity partner = getPad(destWorld, destPad);
        if (partner == null || !partner.linked || !partner.powered) return false;
        if (partner.partnerX != pos.getX() || partner.partnerY != pos.getY() || partner.partnerZ != pos.getZ()
                || partner.partnerDim != world.provider.getDimension()) {
            return false;
        }
        if (cross && !hasActiveComputer(destWorld, destPad)) {
            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).sendStatusMessage(
                        new TextComponentTranslation("message.rfgen.pad.need_computer_dest"), true);
            }
            return false;
        }

        final double x = destPad.getX() + 0.5D;
        final double y = destPad.getY() + 1.0D;
        final double z = destPad.getZ() + 0.5D;
        final float yaw = entity.rotationYaw;
        final float pitch = entity.rotationPitch;

        world.playSound(null, entity.posX, entity.posY, entity.posZ,
                SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        if (cross) {
            final EntityPlayerMP player = (EntityPlayerMP) entity;
            player.changeDimension(partnerDim, new net.minecraftforge.common.util.ITeleporter() {
                @Override
                public void placeEntity(net.minecraft.world.World world, net.minecraft.entity.Entity entity, float yawIn) {
                    entity.setLocationAndAngles(x, y, z, yaw, pitch);
                }
            });
            // ensure exact spot after transfer
            if (player.connection != null) {
                player.connection.setPlayerLocation(x, y, z, yaw, pitch);
            }
        } else if (entity instanceof EntityPlayerMP) {
            ((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, yaw, pitch);
        } else {
            entity.setPositionAndUpdate(x, y, z);
            entity.rotationYaw = yaw;
            entity.rotationPitch = pitch;
        }

        if (destWorld != null) {
            destWorld.playSound(null, x, y, z,
                    SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
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
