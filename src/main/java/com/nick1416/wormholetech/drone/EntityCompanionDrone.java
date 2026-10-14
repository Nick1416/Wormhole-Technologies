package com.nick1416.wormholetech.drone;

import com.google.common.base.Optional;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Player-bound Xenolith Drone. DEFENSIVE follows its owner and zaps monsters that hurt the owner or that
 * the owner attacks (wolf-like). RESEARCH just follows and hovers (reserved for later). Sneak + use with
 * an empty hand to pick it back up as an item.
 */
public class EntityCompanionDrone extends EntityFlying {
    public static final int DEFENSIVE = 0, RESEARCH = 1;
    private static final DataParameter<Integer> MODE = EntityDataManager.createKey(EntityCompanionDrone.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ZAP = EntityDataManager.createKey(EntityCompanionDrone.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.createKey(EntityCompanionDrone.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private int cooldown, zapShow;
    /** A free Quantum Field Modulator is in range (re-checked every 3 s on the server). */
    private boolean powered;
    public int charge = DroneCharge.max();
    /** Charged enough to act (near a modulator, or charge left). */
    private boolean active() { return powered || charge > 0; }

    public EntityCompanionDrone(World w) {
        super(w);
        setSize(0.9F, 0.6F);
        moveHelper = new com.nick1416.wormholetech.talvora.EntityXenolithDrone.DroneMoveHelper(this);
    }

    @Override protected void entityInit() {
        super.entityInit();
        dataManager.register(MODE, DEFENSIVE); dataManager.register(ZAP, -1); dataManager.register(OWNER, Optional.absent());
    }

    public int getMode() { return dataManager.get(MODE); }
    public void setMode(int m) { dataManager.set(MODE, m); }
    public void setOwnerId(UUID id) { dataManager.set(OWNER, Optional.fromNullable(id)); }
    @Nullable public UUID getOwnerId() { return dataManager.get(OWNER).orNull(); }
    @Nullable public EntityPlayer getOwner() { UUID id = getOwnerId(); return id == null ? null : world.getPlayerEntityByUUID(id); }
    public int zapTarget() { return dataManager.get(ZAP); }

    @Override protected void initEntityAI() {
        tasks.addTask(2, new AIDefend());
        tasks.addTask(5, new AIFollow());
    }

    @Override protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override protected boolean canDespawn() { return false; }
    @Override public boolean isPushedByWater() { return false; }
    @Override public boolean canBeLeashedTo(EntityPlayer p) { return false; }
    @Override public float getBrightness() { return 1.0F; }

    @Override
    public boolean attackEntityFrom(DamageSource s, float a) {
        Entity src = s.getTrueSource();
        if (src != null && src.getUniqueID().equals(getOwnerId())) return false;
        return super.attackEntityFrom(s, a);
    }

    @Override
    public boolean processInteract(EntityPlayer p, EnumHand hand) {
        if (p.isSneaking() && p.getHeldItem(hand).isEmpty() && p.getUniqueID().equals(getOwnerId())) {
            if (!world.isRemote) {
                ItemStack s = DroneCharge.with(new ItemStack(Registration.COMPANION_DRONE, 1, getMode()), charge);
                if (hasCustomName()) s.setStackDisplayName(getCustomNameTag());
                if (!p.inventory.addItemStackToInventory(s)) entityDropItem(s, 0.5F);
                playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.6F, 1.4F);
                setDead();
            }
            return true;
        }
        if (!p.isSneaking() && p.getHeldItem(hand).isEmpty() && p.getUniqueID().equals(getOwnerId())) {
            if (!world.isRemote) p.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation(
                    active() ? "status.wormholetech.drone.charge" : "status.wormholetech.drone.empty", DroneCharge.percent(charge)), true);
            return true;
        }
        return super.processInteract(p, hand);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            int t = zapTarget();
            Entity e = t >= 0 ? world.getEntityByID(t) : null;
            if (e != null) for (int i = 0; i <= 10; i++) {
                double f = i / 10.0;
                world.spawnParticle(EnumParticleTypes.FLAME, posX + (e.posX - posX) * f, posY + (e.posY + e.height * 0.5 - posY) * f, posZ + (e.posZ - posZ) * f, 0, 0, 0);
            }
            if (getMode() == RESEARCH && rand.nextInt(12) == 0)
                world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, posX, posY + 0.4, posZ, (rand.nextDouble() - 0.5) * 0.6, 0.3, (rand.nextDouble() - 0.5) * 0.6);
            return;
        }
        if (ticksExisted % 60 == 1) powered = ModulatorTracker.near(world, posX, posY, posZ);
        charge = DroneCharge.tick(charge, powered);
        if (cooldown > 0) cooldown--;
        if (zapShow > 0 && --zapShow == 0) dataManager.set(ZAP, -1);
        if (ticksExisted % 40 == 0 && getHealth() < getMaxHealth()) heal(1.0F);
    }

    private boolean validTarget(EntityLivingBase t, EntityPlayer owner) {
        if (t == null || !t.isEntityAlive() || t == owner || t instanceof EntityPlayer || t instanceof EntityCompanionDrone) return false;
        if (t instanceof com.nick1416.wormholetech.talvora.EntityAlienVillager || t instanceof net.minecraft.entity.passive.EntityVillager) return false;
        if (t instanceof net.minecraft.entity.passive.EntityTameable && ((net.minecraft.entity.passive.EntityTameable) t).isTamed()) return false;
        return getDistanceSq(t) < 24 * 24;
    }

    /** Zap whatever hurt the owner, or the monster the owner just attacked. */
    class AIDefend extends EntityAIBase {
        EntityLivingBase target;
        AIDefend() { setMutexBits(1); }
        @Override public boolean shouldExecute() {
            if (getMode() != DEFENSIVE || !active()) return false;
            EntityPlayer o = getOwner();
            if (o == null) return false;
            EntityLivingBase a = o.getRevengeTarget();
            if (validTarget(a, o)) { target = a; return true; }
            EntityLivingBase b = o.getLastAttackedEntity();
            if (b instanceof IMob && o.ticksExisted - o.getLastAttackedEntityTime() < 200 && validTarget(b, o)) { target = b; return true; }
            return false;
        }
        @Override public boolean shouldContinueExecuting() {
            EntityPlayer o = getOwner();
            return active() && o != null && validTarget(target, o) && getDistanceSq(o) < 32 * 32;
        }
        @Override public void updateTask() {
            getLookHelper().setLookPositionWithEntity(target, 30F, 30F);
            double d = getDistance(target);
            if (d > 6) moveHelper.setMoveTo(target.posX, target.posY + target.height + 2.5, target.posZ, 1.0D);
            if (cooldown <= 0 && d < 10 && getEntitySenses().canSee(target)) {
                cooldown = 25;
                target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(EntityCompanionDrone.this, getOwner()), 5.0F);
                if (target instanceof EntityLiving) ((EntityLiving) target).setRevengeTarget(EntityCompanionDrone.this);
                dataManager.set(ZAP, target.getEntityId()); zapShow = 6;
                world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.NEUTRAL, 0.4F, 1.8F);
            }
        }
        @Override public void resetTask() { target = null; }
    }

    /** Stay near the owner's shoulder; teleport if left far behind. */
    class AIFollow extends EntityAIBase {
        AIFollow() { setMutexBits(1); }
        @Override public boolean shouldExecute() { return getOwner() != null && (active() || getMode() == DEFENSIVE); }
        @Override public void updateTask() {
            EntityPlayer o = getOwner();
            if (o == null) return;
            double d2 = getDistanceSq(o);
            if (d2 > 40 * 40 && o.world == world) { setPosition(o.posX, o.posY + 2.5, o.posZ); return; }
            double a = Math.toRadians(o.rotationYaw + 140);
            double hx = o.posX - Math.sin(a) * 1.6, hz = o.posZ + Math.cos(a) * 1.6, hy = o.posY + o.height + 0.6 + MathHelper.sin(ticksExisted * 0.08F) * 0.2;
            if (d2 > 3.5 * 3.5 || rand.nextInt(20) == 0) moveHelper.setMoveTo(hx, hy, hz, d2 > 100 ? 1.4D : 0.7D);
            if (!getLookHelper().getIsLooking()) getLookHelper().setLookPositionWithEntity(o, 10F, 10F);
        }
    }

    @Override public void writeEntityToNBT(NBTTagCompound c) {
        super.writeEntityToNBT(c);
        c.setInteger("Mode", getMode()); c.setInteger("Charge", charge);
        if (getOwnerId() != null) c.setUniqueId("Owner", getOwnerId());
    }
    @Override public void readEntityFromNBT(NBTTagCompound c) {
        super.readEntityFromNBT(c);
        setMode(c.getInteger("Mode")); charge = c.hasKey("Charge") ? c.getInteger("Charge") : DroneCharge.max();
        if (c.hasUniqueId("Owner")) setOwnerId(c.getUniqueId("Owner"));
    }

    /** A destroyed drone leaves its inactive shell (the chip is lost). */
    @Override public void onDeath(DamageSource s) {
        super.onDeath(s);
        if (!world.isRemote) entityDropItem(new ItemStack(Registration.INACTIVE_DRONE), 0.3F);
    }

    @Override protected SoundEvent getAmbientSound() { return null; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.BLOCK_GLASS_HIT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_GLASS_BREAK; }
    @Override protected float getSoundPitch() { return 1.5F; }
}
