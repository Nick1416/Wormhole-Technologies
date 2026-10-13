package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.entity.EntityEmeraldDrone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** Night patrols: groups of Emerald Drones that come looking for players on P3X-1416 after dark. */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class P3XPatrols {
    private P3XPatrols() {}

    public static final int CAP = 6;

    public static boolean isNight(World w) {
        long t = w.getWorldTime() % 24000L;
        return t >= 13000L && t < 23000L;
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        World w = e.world;
        int every = ModConfig.p3xNightPatrolIntervalTicks;
        if (e.phase != TickEvent.Phase.END || w.isRemote || every <= 0 || !P3XWeather.isP3X(w)) return;
        if (w.getTotalWorldTime() % every != 0 || !isNight(w) || w.getDifficulty() == EnumDifficulty.PEACEFUL) return;
        for (EntityPlayer p : w.playerEntities) {
            if (p.isSpectator() || p.isCreative() || w.rand.nextInt(2) != 0) continue;
            trySpawnPatrol(w, p);
        }
    }

    /** @return number of drones spawned */
    public static int trySpawnPatrol(World w, EntityPlayer p) {
        int near = w.getEntitiesWithinAABB(EntityEmeraldDrone.class, new AxisAlignedBB(p.getPosition()).grow(48),
                net.minecraft.util.EntitySelectors.IS_ALIVE::apply).size();
        if (near >= CAP) return 0;
        int size = Math.min(CAP - near, 2 + w.rand.nextInt(3));
        double a = w.rand.nextDouble() * Math.PI * 2, d = 24 + w.rand.nextInt(17);
        BlockPos centre = w.getHeight(new BlockPos(p.posX + Math.cos(a) * d, 0, p.posZ + Math.sin(a) * d));
        int spawned = 0;
        for (int i = 0; i < size * 3 && spawned < size; i++) {
            BlockPos at = w.getHeight(centre.add(w.rand.nextInt(7) - 3, 0, w.rand.nextInt(7) - 3));
            EntityEmeraldDrone drone = new EntityEmeraldDrone(w);
            drone.setLocationAndAngles(at.getX() + 0.5, at.getY(), at.getZ() + 0.5, w.rand.nextFloat() * 360F, 0F);
            if (!drone.getCanSpawnHere()) continue;
            drone.onInitialSpawn(w.getDifficultyForLocation(at), null);
            w.spawnEntity(drone);
            drone.setAttackTarget(p);
            spawned++;
        }
        return spawned;
    }
}
