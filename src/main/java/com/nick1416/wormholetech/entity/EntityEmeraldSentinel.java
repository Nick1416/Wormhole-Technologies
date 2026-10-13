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

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        bossInfo.setPercent(getHealth() / getMaxHealth());
    }

    @Override public void addTrackingPlayer(EntityPlayerMP p) { super.addTrackingPlayer(p); bossInfo.addPlayer(p); }
    @Override public void removeTrackingPlayer(EntityPlayerMP p) { super.removeTrackingPlayer(p); bossInfo.removePlayer(p); }

    @Override
    public void setCustomNameTag(String name) { super.setCustomNameTag(name); bossInfo.setName(getDisplayName()); }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote && cause.getTrueSource() instanceof EntityPlayer) {
            GuardianRewards.onSentinelDefeated((EntityPlayer) cause.getTrueSource(), this);
        }
    }
}
