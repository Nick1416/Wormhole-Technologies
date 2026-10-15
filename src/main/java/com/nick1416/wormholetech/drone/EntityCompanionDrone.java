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
 * Player-bound Xenolith Drone. DEFENSIVE trails its owner at a few blocks' distance, above head height,
 * and zaps monsters that hurt the owner or that the owner attacks (wolf-like). RESEARCH stays home:
 * it roams near where it was placed (or near a Repository of Knowledge), inside modulator range. Sneak + use with
 * an empty hand to pick it back up as an item.
 */
public class EntityCompanionDrone extends EntityFlying {
    public static final int DEFENSIVE = 0, RESEARCH = 1;
    private static final DataParameter<Integer> MODE = EntityDataManager.createKey(EntityCompanionDrone.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> ZAP = EntityDataManager.createKey(EntityCompanionDrone.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.createKey(EntityCompanionDrone.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private int cooldown, zapShow;
    /** Research drone home (anchor); set on first server tick. */
    @Nullable private net.minecraft.util.math.BlockPos home;
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
        tasks.addTask(6, new AIRoam());
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
    /** Never shoves (or gets shoved by) its owner. */
    @Override public boolean canBePushed() { return false; }
    @Override protected void collideWithEntity(Entity e) { if (!e.getUniqueID().equals(getOwnerId())) super.collideWithEntity(e); }
    @Override public void applyEntityCollision(Entity e) { if (!e.getUniqueID().equals(getOwnerId())) super.applyEntityCollision(e); }

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
        if (home == null && getMode() == RESEARCH) home = pickHome();
        if (ticksExisted % 60 == 1) powered = ModulatorTracker.near(world, posX, posY, posZ);
        charge = DroneCharge.tick(charge, powered);
        if (ticksExisted % 20 == 0 && getMode() == RESEARCH && active() && getOwnerId() != null) {
            int planet = com.nick1416.wormholetech.knowledge.Planets.indexOf(world.provider.getDimension());
            if (planet >= 0 && com.nick1416.wormholetech.knowledge.RepositoryTracker.near(world, posX, posY, posZ, com.nick1416.wormholetech.ModConfig.repositoryResearchRange))
                com.nick1416.wormholetech.knowledge.KnowledgeData.get(world).addProgress(getOwnerId(), planet, 20);
        }
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

    /** Defensive only: trail the owner 4-6 blocks behind, above head height; teleport if left far behind. */
    class AIFollow extends EntityAIBase {
        boolean moving;
        AIFollow() { setMutexBits(1); }
        @Override public boolean shouldExecute() { return getMode() == DEFENSIVE && getOwner() != null; }
        @Override public void updateTask() {
            EntityPlayer o = getOwner();
            if (o == null) return;
            double dx = posX - o.posX, dz = posZ - o.posZ, h2 = dx * dx + dz * dz, d2 = getDistanceSq(o);
            if (d2 > 40 * 40 && o.world == world) { setPosition(o.posX - Math.sin(Math.toRadians(o.rotationYaw + 180)) * 4, o.posY + o.height + 1.2, o.posZ + Math.cos(Math.toRadians(o.rotationYaw + 180)) * 4); return; }
            double a = Math.toRadians(o.rotationYaw + 180 + (getEntityId() % 2 == 0 ? 35 : -35)); // behind, off to one side
            double hx = o.posX - Math.sin(a) * 4.5, hz = o.posZ + Math.cos(a) * 4.5, hy = o.posY + o.height + 1.2 + MathHelper.sin(ticksExisted * 0.08F) * 0.2;
            // in front of the owner (within their view cone, close): get out of the way
            double fx = -Math.sin(Math.toRadians(o.rotationYaw)), fz = Math.cos(Math.toRadians(o.rotationYaw));
            boolean inWay = h2 < 2.5 * 2.5 || (h2 < 3.5 * 3.5 && dx * fx + dz * fz > 0); // on top of or in front of the owner
            if (h2 > 6 * 6) moving = true;
            else if (h2 < 4 * 4 && !inWay) moving = false;
            if (moving || inWay) moveHelper.setMoveTo(hx, hy, hz, h2 > 100 ? 1.4D : 0.8D);
            else if (posY < o.posY + o.height + 0.6) motionY += 0.04; // keep above head height
            if (!getLookHelper().getIsLooking()) getLookHelper().setLookPositionWithEntity(o, 10F, 10F);
        }
    }

    public static final int ROAM_RADIUS = 16, REPO_ROAM_RADIUS = 6;

    /** Home: just above the nearest Repository within 16 blocks, else where the drone was placed. */
    private net.minecraft.util.math.BlockPos pickHome() {
        net.minecraft.util.math.BlockPos r = com.nick1416.wormholetech.knowledge.RepositoryTracker.nearest(world, posX, posY, posZ, 16);
        return r != null ? r.up(2) : new net.minecraft.util.math.BlockPos(this);
    }

    private int roamRadius() {
        return home != null && com.nick1416.wormholetech.knowledge.RepositoryTracker.near(world, home.getX() + 0.5, home.getY(), home.getZ() + 0.5, 3)
                ? REPO_ROAM_RADIUS : ROAM_RADIUS;
    }

    /** Spot is acceptable if no modulator is known near home, or it stays within modulator range. */
    private boolean inModulatorRange(double x, double y, double z) {
        if (home == null || !ModulatorTracker.near(world, home.getX() + 0.5, home.getY(), home.getZ() + 0.5)) return true;
        return ModulatorTracker.near(world, x, y, z);
    }

    /** Research only: wander around home; head straight back if pushed or carried away. */
    class AIRoam extends EntityAIBase {
        AIRoam() { setMutexBits(1); }
        @Override public boolean shouldExecute() { return getMode() == RESEARCH && home != null; }
        @Override public void updateTask() {
            int r = roamRadius();
            double hx = home.getX() + 0.5, hy = home.getY() + 0.5, hz = home.getZ() + 0.5;
            double dx = posX - hx, dz = posZ - hz;
            if (dx * dx + dz * dz > (r + 3) * (r + 3) || Math.abs(posY - hy) > 8 || !inModulatorRange(posX, posY, posZ)) {
                moveHelper.setMoveTo(hx, hy + 1, hz, 1.0D);
                return;
            }
            if (moveHelper.isUpdating() || rand.nextInt(60) != 0) return;
            for (int i = 0; i < 6; i++) {
                double a = rand.nextDouble() * Math.PI * 2, d = rand.nextDouble() * r;
                double x = hx + Math.cos(a) * d, z = hz + Math.sin(a) * d, y = hy + rand.nextInt(4) - 0.5;
                net.minecraft.util.math.BlockPos tp = new net.minecraft.util.math.BlockPos(x, y, z);
                if (!world.isBlockLoaded(tp) || !world.isAirBlock(tp) || !inModulatorRange(x, y, z)) continue;
                moveHelper.setMoveTo(x, y, z, 0.5D);
                return;
            }
        }
    }

    @Override public void writeEntityToNBT(NBTTagCompound c) {
        super.writeEntityToNBT(c);
        c.setInteger("Mode", getMode()); c.setInteger("Charge", charge);
        if (getOwnerId() != null) c.setUniqueId("Owner", getOwnerId());
        if (home != null) c.setLong("Home", home.toLong());
    }
    @Override public void readEntityFromNBT(NBTTagCompound c) {
        super.readEntityFromNBT(c);
        setMode(c.getInteger("Mode")); charge = c.hasKey("Charge") ? c.getInteger("Charge") : DroneCharge.max();
        if (c.hasUniqueId("Owner")) setOwnerId(c.getUniqueId("Owner"));
        home = c.hasKey("Home") ? net.minecraft.util.math.BlockPos.fromLong(c.getLong("Home")) : null;
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
