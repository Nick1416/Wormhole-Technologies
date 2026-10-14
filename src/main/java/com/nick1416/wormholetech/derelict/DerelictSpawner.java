package com.nick1416.wormholetech.derelict;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.entity.EntityEmeraldDrone;
import com.nick1416.wormholetech.entity.EntityEmeraldWisp;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Keeps the ship crawling: every derelictSpawnIntervalTicks, each player gets up to two crawlers
 * (Emerald Drones, sometimes an Emerald Wisp) 10-24 blocks away until derelictCrawlerCap are near.
 * Vanilla biome spawning runs on top of this.
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class DerelictSpawner {
    private DerelictSpawner() {}

    /** Inside the ship's deck (between floor and ceiling), never on the outer hull. */
    public static boolean onDeck(double y, double height) {
        return y >= DerelictLayout.FLOOR + 1 && y + height <= DerelictLayout.CEIL;
    }

    /** On deck and outside the hub chunk (the arrival room stays clear of spawns). */
    public static boolean canSpawnAt(double x, double y, double z, double height) {
        return onDeck(y, height) && !(((int) Math.floor(x)) >> 4 == 0 && ((int) Math.floor(z)) >> 4 == 0);
    }

    public static boolean isDerelict(World w) {
        return w != null && ModDimensions.isDerelictRegistered() && w.provider.getDimension() == ModDimensions.derelictId();
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        World w = e.world;
        int every = ModConfig.derelictSpawnIntervalTicks;
        if (e.phase != TickEvent.Phase.END || w.isRemote || every <= 0 || !isDerelict(w)) return;
        if (w.getTotalWorldTime() % every != 0 || w.getDifficulty() == EnumDifficulty.PEACEFUL) return;
        for (EntityPlayer p : w.playerEntities) if (!p.isSpectator()) spawnNear(w, p);
    }

    /** @return number of crawlers spawned */
    public static int spawnNear(World w, EntityPlayer p) {
        int near = w.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(p.getPosition()).grow(32, 8, 32),
                e -> e instanceof IMob && EntitySelectors.IS_ALIVE.apply(e)).size();
        int spawned = 0;
        for (int i = 0; i < 12 && spawned < 2 && near + spawned < ModConfig.derelictCrawlerCap; i++) {
            double a = w.rand.nextDouble() * Math.PI * 2, d = 10 + w.rand.nextInt(15);
            BlockPos at = new BlockPos(p.posX + Math.cos(a) * d, DerelictLayout.FLOOR + 1, p.posZ + Math.sin(a) * d);
            if (!w.isBlockLoaded(at)) continue;
            EntityLiving mob = w.rand.nextInt(4) == 0 ? new EntityEmeraldWisp(w) : new EntityEmeraldDrone(w);
            mob.setLocationAndAngles(at.getX() + 0.5, at.getY() + (mob instanceof EntityEmeraldWisp ? 1 : 0), at.getZ() + 0.5,
                    w.rand.nextFloat() * 360F, 0F);
            if (!mob.getCanSpawnHere() || !mob.isNotColliding()) continue;
            mob.onInitialSpawn(w.getDifficultyForLocation(at), null);
            w.spawnEntity(mob);
            spawned++;
        }
        return spawned;
    }
}
