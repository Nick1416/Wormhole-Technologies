package com.nick1416.wormholetech.stargate;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.block.StargatePortalBlock;
import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Moves players through gates. Collisions only queue a request; the actual dimension change runs
 * at the end of the server tick (changing dimension from inside a block collision callback is
 * unsafe while the world is iterating entities).
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class StargateTravel {

    public static final String ORIGIN_TAG = WormholeTech.MODID + ":gate_origin";
    public static final int COOLDOWN_TICKS = 60;
    public static final ResourceLocation ADVANCEMENT = new ResourceLocation(WormholeTech.MODID, "p3x_1416");
    public static final ResourceLocation DERELICT_ADVANCEMENT = new ResourceLocation(WormholeTech.MODID, "derelict_vessel");

    private static final Map<UUID, Request> PENDING = new HashMap<UUID, Request>();

    private static final class Request {
        final int dim;
        final BlockPos portal;
        Request(int dim, BlockPos portal) { this.dim = dim; this.portal = portal; }
    }

    private StargateTravel() {}

    public static void request(EntityPlayerMP player, World world, BlockPos portal) {
        if (!ModDimensions.isRegistered() && !ModDimensions.isDerelictRegistered()) return;
        player.timeUntilPortal = COOLDOWN_TICKS; // ignore further collisions while queued/after travel
        PENDING.put(player.getUniqueID(), new Request(world.provider.getDimension(), portal.toImmutable()));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || PENDING.isEmpty()) return;
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        Map<UUID, Request> copy = new HashMap<UUID, Request>(PENDING);
        PENDING.clear();
        for (Map.Entry<UUID, Request> en : copy.entrySet()) {
            EntityPlayerMP p = server.getPlayerList().getPlayerByUUID(en.getKey());
            Request r = en.getValue();
            if (p == null || p.isDead || p.dimension != r.dim) continue;
            try {
                travel(p, (WorldServer) p.world, r.portal);
            } catch (RuntimeException ex) {
                WormholeTech.LOGGER.error("Gate travel failed for {}", p.getName(), ex);
            }
        }
    }

    public static void travel(EntityPlayerMP p, WorldServer from, BlockPos portal) {
        IBlockState st = from.getBlockState(portal);
        if (st.getBlock() != Registration.STARGATE_PORTAL) return;
        if (ModDimensions.isGateWorld(from.provider.getDimension())) {
            goHome(p);
        } else {
            goOut(p, from, portal, st.getValue(StargatePortalBlock.AXIS));
        }
    }

    // ---------------------------------------------------------------- outbound

    /** Destination of the dialing computer that opened this gate (P3X-1416 if none is found). */
    private static int destinationOf(WorldServer w, GateShape g) {
        if (g != null) {
            for (int y = -1; y <= g.height; y++)
                for (int x = -1; x <= g.width; x++) {
                    if (!(y == -1 || y == g.height || x == -1 || x == g.width)) continue;
                    BlockPos f = g.interior(x, y);
                    for (EnumFacing d : EnumFacing.VALUES) {
                        net.minecraft.tileentity.TileEntity te = w.getTileEntity(f.offset(d));
                        if (te instanceof com.nick1416.wormholetech.tile.DialingComputerTileEntity
                                && ((com.nick1416.wormholetech.tile.DialingComputerTileEntity) te).isOpen())
                            return ((com.nick1416.wormholetech.tile.DialingComputerTileEntity) te).destination();
                    }
                }
        }
        return ModDimensions.id();
    }

    private static void goOut(EntityPlayerMP p, WorldServer from, BlockPos portal, EnumFacing.Axis axis) {
        GateShape g = GateShape.detect(from, portal, axis, TitaniumFrame::isTitanium);
        BlockPos back;
        if (g != null) {
            // step out on the side the player came from
            int side = sideOf(g, p);
            back = g.front(side, 2);
        } else {
            back = portal;
        }
        NBTTagCompound o = new NBTTagCompound();
        o.setInteger("Dim", from.provider.getDimension());
        o.setLong("Pos", back.toLong());
        o.setFloat("Yaw", p.rotationYaw + 180.0F);
        o.setLong("Gate", (g != null ? g.interior(g.width / 2, 0) : portal).toLong());
        persisted(p).setTag(ORIGIN_TAG, o);

        int dest = destinationOf(from, g);
        WorldServer target = p.mcServer.getWorld(dest);
        final BlockPos arrival = ReturnGates.ensure(target);
        final float yaw = target == null ? p.rotationYaw : ReturnGates.arrivalYaw();
        teleport(p, dest, arrival, yaw);
        GateNetwork.recordVisit(p, dest);
        grantAdvancement(p, ModDimensions.isDerelictRegistered() && dest == ModDimensions.derelictId() ? DERELICT_ADVANCEMENT : ADVANCEMENT);
    }

    private static int sideOf(GateShape g, EntityPlayer p) {
        BlockPos c = g.interior(g.width / 2, 0);
        double d = g.axis == EnumFacing.Axis.X ? p.posZ - (c.getZ() + 0.5) : p.posX - (c.getX() + 0.5);
        return d >= 0 ? 1 : -1;
    }

    // ---------------------------------------------------------------- inbound

    private static void goHome(EntityPlayerMP p) {
        NBTTagCompound tag = persisted(p);
        int dim = 0;
        BlockPos pos = null;
        float yaw = p.rotationYaw;
        if (tag.hasKey(ORIGIN_TAG, Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound o = tag.getCompoundTag(ORIGIN_TAG);
            dim = o.getInteger("Dim");
            pos = BlockPos.fromLong(o.getLong("Pos"));
            yaw = o.getFloat("Yaw");
        }
        WorldServer target = p.mcServer.getWorld(dim);
        if (target == null) { dim = 0; target = p.mcServer.getWorld(0); pos = null; }
        if (pos == null) pos = target.getTopSolidOrLiquidBlock(target.getSpawnPoint());
        teleport(p, dim, safeSpot(target, pos), yaw);
    }

    // ---------------------------------------------------------------- helpers

    private static void teleport(EntityPlayerMP p, int dim, BlockPos feet, float yaw) {
        final double x = feet.getX() + 0.5, y = feet.getY(), z = feet.getZ() + 0.5;
        p.dismountRidingEntity();
        if (p.dimension != dim) {
            p.changeDimension(dim, new ITeleporter() {
                @Override
                public void placeEntity(World world, Entity entity, float y0) {
                    entity.setLocationAndAngles(x, y, z, yaw, entity.rotationPitch);
                    entity.motionX = entity.motionY = entity.motionZ = 0;
                }
            });
        }
        p.connection.setPlayerLocation(x, y, z, yaw, p.rotationPitch);
        p.fallDistance = 0;
        p.timeUntilPortal = COOLDOWN_TICKS;
        p.world.playSound(null, feet, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.8F, 0.7F);
    }

    /** Nearest spot (search up first, then down) with solid floor and two free blocks. */
    public static BlockPos safeSpot(World w, BlockPos start) {
        for (int r = 0; r <= 2; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != r) continue;
                    for (int dy = 0; dy <= 6; dy++) {
                        BlockPos a = start.add(dx, dy, dz);
                        if (isSafe(w, a)) return a;
                        BlockPos b = start.add(dx, -dy, dz);
                        if (dy > 0 && isSafe(w, b)) return b;
                    }
                }
            }
        }
        // Nothing suitable: make a pocket with a floor at the original spot.
        BlockPos s = new BlockPos(start.getX(), Math.max(2, Math.min(250, start.getY())), start.getZ());
        if (!w.getBlockState(s.down()).getMaterial().isSolid()) w.setBlockState(s.down(), Blocks.COBBLESTONE.getDefaultState());
        clear(w, s);
        clear(w, s.up());
        return s;
    }

    static boolean isSafe(World w, BlockPos feet) {
        if (feet.getY() < 1 || feet.getY() > 254) return false;
        IBlockState below = w.getBlockState(feet.down());
        return below.getMaterial().isSolid() && below.getMaterial() != Material.LAVA
                && free(w, feet) && free(w, feet.up());
    }

    private static boolean free(World w, BlockPos p) {
        IBlockState s = w.getBlockState(p);
        Material m = s.getMaterial();
        return !m.blocksMovement() && !m.isLiquid() && m != Material.FIRE && s.getBlock() != Registration.STARGATE_PORTAL;
    }

    static void clear(World w, BlockPos p) {
        if (!free(w, p) && w.getBlockState(p).getBlock() != Registration.STARGATE_PORTAL
                && w.getBlockState(p).getBlockHardness(w, p) >= 0) {
            w.setBlockToAir(p);
        }
    }

    private static NBTTagCompound persisted(EntityPlayer p) {
        NBTTagCompound data = p.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG, Constants.NBT.TAG_COMPOUND)) {
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    private static void grantAdvancement(EntityPlayerMP p, ResourceLocation id) {
        Advancement adv = p.mcServer.getAdvancementManager().getAdvancement(id);
        if (adv == null) return;
        if (p.getAdvancements().getProgress(adv).isDone()) return;
        for (String c : p.getAdvancements().getProgress(adv).getRemaningCriteria()) {
            p.getAdvancements().grantCriterion(adv, c);
        }
    }
}
