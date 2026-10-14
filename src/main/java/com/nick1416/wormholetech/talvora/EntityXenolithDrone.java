package com.nick1416.wormholetech.talvora;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Harmless Xenolith survey drone of Tal'Vora. Drifts 6-14 blocks above the dunes scanning the ground;
 * now and then it flies to a nearby player, hovers over them and scans them for a few seconds (light
 * beam + soft chime), then leaves and ignores that player for a few minutes. Never attacks.
 */
public class EntityXenolithDrone extends EntityFlying {
    private static final DataParameter<Integer> SCAN = EntityDataManager.createKey(EntityXenolithDrone.class, DataSerializers.VARINT);
    public static final int PLAYER_COOLDOWN = 20 * 60 * 4;
    private final Map<UUID, Long> ignoreUntil = new HashMap<>();

    public EntityXenolithDrone(World w) {
        super(w);
        setSize(0.9F, 0.6F);
        moveHelper = new DroneMoveHelper(this);
    }

    @Override protected void entityInit() { super.entityInit(); dataManager.register(SCAN, -1); }
    /** Entity id being scanned, -2 for a ground scan, -1 when idle. */
    public int scanTarget() { return dataManager.get(SCAN); }

    @Override
    protected void initEntityAI() {
        tasks.addTask(2, new AIScanPlayer());
        tasks.addTask(5, new AIScanGround());
        tasks.addTask(8, new AIWander());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
    }

    /** Open sky in Tal'Vora, and at most two drones within 96 blocks. */
    @Override
    public boolean getCanSpawnHere() {
        if (world.provider.getDimension() != com.nick1416.wormholetech.ModConfig.talVoraDimensionId) return false;
        BlockPos p = new BlockPos(this);
        if (!world.canSeeSky(p) || rand.nextInt(4) != 0) return false;
        if (world.getEntitiesWithinAABB(EntityXenolithDrone.class, getEntityBoundingBox().grow(96, 128, 96)).size() >= 2) return false;
        return world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty();
    }

    @Override public int getMaxSpawnedInChunk() { return 1; }
    @Override protected boolean canDespawn() { return true; }
    @Override public boolean isPushedByWater() { return false; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) beamParticles();
        else if (dataManager.get(SCAN) < 0 && posY - groundY() < 4) motionY += 0.03; // keep clear of the dunes
    }

    private void beamParticles() {
        int t = scanTarget();
        if (t == -1 || (ticksExisted & 1) != 0) return;
        double tx, ty, tz;
        if (t == -2) {
            BlockPos g = world.getHeight(new BlockPos(this));
            tx = posX + (rand.nextDouble() - 0.5) * 3; ty = g.getY() + 0.1; tz = posZ + (rand.nextDouble() - 0.5) * 3;
        } else {
            net.minecraft.entity.Entity e = world.getEntityByID(t);
            if (e == null) return;
            tx = e.posX + (rand.nextDouble() - 0.5) * e.width; ty = e.posY + rand.nextDouble() * e.height; tz = e.posZ + (rand.nextDouble() - 0.5) * e.width;
        }
        double sx = posX, sy = posY + 0.1, sz = posZ;
        for (int i = 0; i <= 8; i++) {
            double f = i / 8.0;
            world.spawnParticle(EnumParticleTypes.END_ROD, sx + (tx - sx) * f, sy + (ty - sy) * f, sz + (tz - sz) * f, 0, 0, 0);
        }
    }

    private double groundY() { return world.getHeight(new BlockPos(this)).getY(); }
    private void chime() { world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.NEUTRAL, 0.35F, 1.8F + rand.nextFloat() * 0.2F); }

    /** Drift to a random point 6-14 blocks above the ground. */
    class AIWander extends EntityAIBase {
        AIWander() { setMutexBits(1); }
        @Override public boolean shouldExecute() { return (!moveHelper.isUpdating() && rand.nextInt(10) == 0) || rand.nextInt(80) == 0; }
        @Override public boolean shouldContinueExecuting() { return false; }
        @Override public void startExecuting() {
            double a = rand.nextDouble() * Math.PI * 2, d = 8 + rand.nextInt(16);
            double x = posX + Math.cos(a) * d, z = posZ + Math.sin(a) * d;
            BlockPos tp = new BlockPos(x, 0, z);
            if (!world.isBlockLoaded(tp)) return;
            double y = world.getHeight(tp).getY() + 6 + rand.nextInt(9);
            moveHelper.setMoveTo(x, Math.min(240, y), z, 0.6D);
        }
    }

    /** Pause and sweep the sand below with the scan light. */
    class AIScanGround extends EntityAIBase {
        int left;
        AIScanGround() { setMutexBits(1); }
        @Override public boolean shouldExecute() { return rand.nextInt(160) == 0 && posY - groundY() < 18; }
        @Override public void startExecuting() { left = 40 + rand.nextInt(40); dataManager.set(SCAN, -2); chime(); }
        @Override public boolean shouldContinueExecuting() { return left > 0; }
        @Override public void updateTask() { left--; motionX *= 0.6; motionZ *= 0.6; rotationYaw += 4; renderYawOffset = rotationYaw; }
        @Override public void resetTask() { dataManager.set(SCAN, -1); }
    }

    /** Fly to a nearby player, hover over them and scan them, then leave. */
    class AIScanPlayer extends EntityAIBase {
        EntityPlayer target;
        int left;
        AIScanPlayer() { setMutexBits(1); }
        @Override public boolean shouldExecute() {
            if (rand.nextInt(100) != 0) return false;
            long now = world.getTotalWorldTime();
            for (EntityPlayer p : world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(24))) {
                Long until = ignoreUntil.get(p.getUniqueID());
                if (p.isSpectator() || !p.isEntityAlive() || (until != null && until > now)) continue;
                target = p;
                return true;
            }
            return false;
        }
        @Override public void startExecuting() { left = 20 * 12; }
        @Override public boolean shouldContinueExecuting() { return target != null && target.isEntityAlive() && left > 0 && getDistanceSq(target) < 40 * 40; }
        @Override public void updateTask() {
            left--;
            double hx = target.posX, hy = target.posY + 3.4, hz = target.posZ;
            getLookHelper().setLookPositionWithEntity(target, 30F, 30F);
            double d2 = (posX - hx) * (posX - hx) + (posY - hy) * (posY - hy) + (posZ - hz) * (posZ - hz);
            if (dataManager.get(SCAN) != target.getEntityId()) {
                moveHelper.setMoveTo(hx, hy, hz, 0.9D);
                if (d2 < 2.5 * 2.5) { dataManager.set(SCAN, target.getEntityId()); left = 60 + rand.nextInt(40); chime(); }
            } else {
                // hold station over the player's head
                motionX += (hx - posX) * 0.02 - motionX * 0.2;
                motionY += (hy - posY) * 0.04 - motionY * 0.2;
                motionZ += (hz - posZ) * 0.02 - motionZ * 0.2;
                if (left % 20 == 0) world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_NOTE_HARP, SoundCategory.NEUTRAL, 0.2F, 1.9F);
            }
        }
        @Override public void resetTask() {
            if (target != null) {
                ignoreUntil.put(target.getUniqueID(), world.getTotalWorldTime() + PLAYER_COOLDOWN);
                if (dataManager.get(SCAN) == target.getEntityId()) world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.NEUTRAL, 0.35F, 1.2F);
                double a = Math.atan2(posZ - target.posZ, posX - target.posX);
                double x = posX + Math.cos(a) * 28, z = posZ + Math.sin(a) * 28;
                BlockPos tp = new BlockPos(x, 0, z);
                double y = world.isBlockLoaded(tp) ? world.getHeight(tp).getY() + 10 : posY + 6;
                moveHelper.setMoveTo(x, y, z, 0.9D);
            }
            target = null;
            dataManager.set(SCAN, -1);
        }
    }

    static class DroneMoveHelper extends EntityMoveHelper {
        DroneMoveHelper(EntityXenolithDrone d) { super(d); }
        @Override public void onUpdateMoveHelper() {
            if (action != Action.MOVE_TO) { entity.motionY += Math.sin(entity.ticksExisted * 0.1) * 0.002; return; }
            double dx = posX - entity.posX, dy = posY - entity.posY, dz = posZ - entity.posZ;
            double d = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            if (d < 0.6D || entity.collidedHorizontally && entity.getRNG().nextInt(10) == 0) {
                action = Action.WAIT;
                entity.motionX *= 0.5D; entity.motionY *= 0.5D; entity.motionZ *= 0.5D;
                return;
            }
            double k = 0.03D * speed / d;
            entity.motionX += dx * k; entity.motionY += dy * k; entity.motionZ += dz * k;
            if (entity.getLookHelper().getIsLooking()) return;
            entity.rotationYaw = -((float) MathHelper.atan2(entity.motionX, entity.motionZ)) * (180F / (float) Math.PI);
            entity.renderYawOffset = entity.rotationYaw;
        }
    }

    @Override public boolean attackEntityFrom(DamageSource s, float a) { return super.attackEntityFrom(s, a); }
    @Override protected SoundEvent getAmbientSound() { return null; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.BLOCK_GLASS_HIT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_GLASS_BREAK; }
    @Override protected float getSoundPitch() { return 1.5F; }
    @Override public float getBrightness() { return 1.0F; }
    @Override protected net.minecraft.util.ResourceLocation getLootTable() { return null; }
    @Override public void writeEntityToNBT(NBTTagCompound c) { super.writeEntityToNBT(c); }
}
