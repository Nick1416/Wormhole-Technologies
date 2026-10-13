package com.nick1416.wormholetech.entity;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

/**
 * Temple Guardian mini-boss of P3X-1416: a large, armoured Emerald Drone that guards a guardian
 * temple. Boss health bar, never despawns, never spawns naturally (only placed by the temple).
 */
public class EntityEmeraldSentinel extends EntityEmeraldDrone {
    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(WormholeTech.MODID, "entities/emerald_sentinel"));

    private final BossInfoServer bossInfo = new BossInfoServer(getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.NOTCHED_10);

    public EntityEmeraldSentinel(World world) {
        super(world);
        setSize(1.4F, 1.2F);
        experienceValue = 60;
        enablePersistence();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(120.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8D);
    }

    /** Only placed by guardian temples. */
    @Override public boolean getCanSpawnHere() { return false; }
    @Override protected boolean canDespawn() { return false; }
    @Override public boolean isNonBoss() { return false; }
    @Override protected ResourceLocation getLootTable() { return LOOT; }
    @Override protected float getSoundPitch() { return 0.9F + rand.nextFloat() * 0.1F; }
    @Override protected float getSoundVolume() { return 1.0F; }

    /** Players the entity tracker sends this Sentinel to (up to 80 blocks, through walls). */
    private final java.util.Set<EntityPlayerMP> trackers = new java.util.HashSet<EntityPlayerMP>();
    /** The boss bar is only shown within this distance, so a guardian in another temple does not show one. */
    public static final double BAR_RANGE = 32.0D;

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) return;
        bossInfo.setPercent(getHealth() / getMaxHealth());
        if (ticksExisted % 5 == 0) refreshBar();
    }

    /** Show the bar to tracking players that are alive, in this world and within BAR_RANGE; hide it from everyone else. */
    private void refreshBar() {
        boolean alive = isEntityAlive();
        for (EntityPlayerMP p : new java.util.ArrayList<EntityPlayerMP>(bossInfo.getPlayers()))
            if (!alive || !trackers.contains(p) || !inBarRange(p)) bossInfo.removePlayer(p);
        if (!alive) return;
        for (EntityPlayerMP p : trackers) if (inBarRange(p)) bossInfo.addPlayer(p); // no-op if already shown
    }

    private boolean inBarRange(EntityPlayerMP p) {
        return p.isEntityAlive() && !p.hasDisconnected() && p.world == world && getDistanceSq(p) <= BAR_RANGE * BAR_RANGE;
    }

    private void clearBar() {
        for (EntityPlayerMP p : new java.util.ArrayList<EntityPlayerMP>(bossInfo.getPlayers())) bossInfo.removePlayer(p);
    }

    @Override public void addTrackingPlayer(EntityPlayerMP p) { super.addTrackingPlayer(p); trackers.add(p); refreshBar(); }
    @Override public void removeTrackingPlayer(EntityPlayerMP p) { super.removeTrackingPlayer(p); trackers.remove(p); bossInfo.removePlayer(p); }

    /** Despawn, kill command, unload and the end of the death animation all pass here. */
    @Override
    public void setDead() {
        super.setDead();
        if (!world.isRemote) clearBar();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (!world.isRemote) { clearBar(); trackers.clear(); }
    }

    @Override
    public void setCustomNameTag(String name) { super.setCustomNameTag(name); bossInfo.setName(getDisplayName()); }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote) { bossInfo.setPercent(0F); clearBar(); }
        if (!world.isRemote && cause.getTrueSource() instanceof EntityPlayer) {
            GuardianRewards.onSentinelDefeated((EntityPlayer) cause.getTrueSource(), this);
        }
    }
}
