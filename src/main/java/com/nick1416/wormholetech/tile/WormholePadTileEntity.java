package com.nick1416.wormholetech.tile;

import com.nick1416.wormholetech.ModConfig;

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
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;

public class WormholePadTileEntity extends TileEntity implements ITickable {

    private static final String CD_KEY = "wormholetechPadCD";

    private final PadEnergyStorage energy = new PadEnergyStorage(
            ModConfig.wormholePadBuffer, ModConfig.wormholePadMaxReceive);

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
            if (energy.getEnergyStored() >= ModConfig.wormholePadUpkeepRfPerTick) {
                energy.consume(ModConfig.wormholePadUpkeepRfPerTick);
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

    /** Readable dimension name for chat/tooltips (Overworld, Nether, The End, else the provider name). */
    public static String dimName(int dim) {
        switch (dim) {
            case 0: return "Overworld";
            case -1: return "Nether";
            case 1: return "The End";
            default:
                try {
                    if (DimensionManager.isDimensionRegistered(dim)) {
                        return DimensionManager.getProviderType(dim).getName() + " (" + dim + ")";
                    }
                } catch (RuntimeException ignored) {
                }
                return "Dim " + dim;
        }
    }

    /**
     * Server world for {@code dim}, initialising it if it is registered but currently unloaded
     * (e.g. the Nether while nobody is in it). Returns null for unknown dimensions or on the client.
     */
    public static World getWorldForDim(World hint, int dim) {
        if (hint != null && hint.provider.getDimension() == dim) return hint;
        if (hint != null && hint.isRemote) return null;
        if (!DimensionManager.isDimensionRegistered(dim)) return null;
        WorldServer w = DimensionManager.getWorld(dim);
        if (w == null) {
            DimensionManager.initDimension(dim);
            w = DimensionManager.getWorld(dim);
        }
        return w;
    }

    /**
     * Makes sure the chunk holding {@code p} is loaded (from disk; never generates new terrain).
     * Returns true if it was already loaded before this call. Chunks loaded here are queued for
     * unload again, so a check never pins a chunk in memory.
     */
    private static boolean ensureLoaded(World w, BlockPos p) {
        if (w.isBlockLoaded(p)) return true;
        if (w instanceof WorldServer) {
            ChunkProviderServer cps = ((WorldServer) w).getChunkProvider();
            Chunk chunk = cps.loadChunk(p.getX() >> 4, p.getZ() >> 4);
            if (chunk != null) cps.queueUnload(chunk);
        }
        return false;
    }

    /**
     * True if the area around {@code p} is loaded right now (so its machines are ticking).
     * Call this BEFORE loading anything there with {@link #getPadLoading}.
     */
    public static boolean isAreaLoaded(@Nullable World w, BlockPos p) {
        return w != null && p != null && w.isBlockLoaded(p);
    }

    /** {@link #hasActiveComputer(World, BlockPos, boolean)} for an area that is loaded and ticking. */
    public static boolean hasActiveComputer(World world, BlockPos pos) {
        return hasActiveComputer(world, pos, true);
    }

    /**
     * True if a Relativistic Computer beside {@code pos} can calculate cross-dimension paths.
     * If the area was loaded (ticking) the computer must be active (Aetherius + power).
     * If it was NOT loaded (typically the far end of a cross-dimension link while you stand at
     * the other end) the computer has not been ticking, so its saved active flag is stale; then
     * it only needs Aetherius seated. Works in any dimension and loads the chunks if needed.
     */
    public static boolean hasActiveComputer(World world, BlockPos pos, boolean areaWasLoaded) {
        if (world == null || pos == null || world.isRemote) return false;
        for (EnumFacing face : EnumFacing.VALUES) {
            BlockPos cp = pos.offset(face);
            ensureLoaded(world, cp);
            if (!world.isBlockLoaded(cp)) continue;
            TileEntity te = world.getTileEntity(cp);
            if (te instanceof RelativisticComputerTileEntity) {
                RelativisticComputerTileEntity c = (RelativisticComputerTileEntity) te;
                if (c.isActive()) return true;
                if (!areaWasLoaded && c.hasAetherius()) return true;
            }
        }
        return false;
    }

    /** Pad at {@code p} in {@code w}, loading its chunk from disk if necessary. */
    public static WormholePadTileEntity getPadLoading(World w, BlockPos p) {
        if (w == null || p == null || w.isRemote) return null;
        ensureLoaded(w, p);
        if (!w.isBlockLoaded(p)) return null;
        TileEntity te = w.getTileEntity(p);
        return te instanceof WormholePadTileEntity ? (WormholePadTileEntity) te : null;
    }

    private static WormholePadTileEntity getPad(World w, BlockPos p) {
        return getPadLoading(w, p);
    }

    private static World resolveWorld(World hint, int dim) {
        return getWorldForDim(hint, dim);
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
     * Bidirectional link; refuses only a pad linked to itself (same position AND dimension).
     * Callers check the cross-dimension computer requirement first, each pad in its own world
     * (see ItemWormholeLinker), because only they know whether the far pad's area was loaded.
     */
    public static boolean linkPads(WormholePadTileEntity a, WormholePadTileEntity b) {
        if (a == null || b == null || a.world == null || b.world == null) return false;
        if (a.world.provider.getDimension() == b.world.provider.getDimension() && a.pos.equals(b.pos)) {
            return false;
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
                            new TextComponentTranslation("message.wormholetech.pad.need_computer"), true);
                }
                return false;
            }
        }

        World destWorld = resolveWorld(world, partnerDim);
        BlockPos destPad = new BlockPos(partnerX, partnerY, partnerZ);
        // A destination in an unloaded area is not ticking, so its upkeep/powered flag is stale;
        // only require power from a destination that is actually loaded.
        boolean destWasLoaded = destWorld != null && destWorld.isBlockLoaded(destPad);
        WormholePadTileEntity partner = getPad(destWorld, destPad);
        if (partner == null || !partner.linked || (destWasLoaded && !partner.powered)) return false;
        if (partner.partnerX != pos.getX() || partner.partnerY != pos.getY() || partner.partnerZ != pos.getZ()
                || partner.partnerDim != world.provider.getDimension()) {
            return false;
        }
        if (cross && !hasActiveComputer(destWorld, destPad, destWasLoaded)) {
            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).sendStatusMessage(
                        new TextComponentTranslation("message.wormholetech.pad.need_computer_dest", dimName(partnerDim)), true);
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

        data.setLong(CD_KEY, now + ModConfig.wormholePadTeleportCooldownTicks);
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
