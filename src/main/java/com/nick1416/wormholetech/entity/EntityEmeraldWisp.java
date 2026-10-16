package com.nick1416.wormholetech.entity;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.derelict.DerelictSpawner;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

/**
 * Small flying ship drone with red eyes. Drifts around (vex-like, but it collides with walls) and
 * dives at players it sees for a melee hit. Modest health, small drops.
 */
public class EntityEmeraldWisp extends EntityFlying implements IMob {
    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(WormholeTech.MODID, "entities/emerald_wisp"));

    private int attackCooldown;

    public EntityEmeraldWisp(World world) {
        super(world);
        setSize(0.6F, 0.6F);
        experienceValue = 4;
        moveHelper = new WispMoveHelper(this);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(4, new AIDive());
        tasks.addTask(8, new AIDrift());
        targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.3D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL) setDead();
        if (attackCooldown > 0) attackCooldown--;
    }

    @Override
    public boolean getCanSpawnHere() {
        if (world.getDifficulty() != EnumDifficulty.PEACEFUL && com.nick1416.wormholetech.world.ModDimensions.isUtopis(world.provider.getDimension()))
            return world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty();
        return world.getDifficulty() != EnumDifficulty.PEACEFUL && DerelictSpawner.isDerelict(world)
                && DerelictSpawner.canSpawnAt(posX, posY, posZ, height) && world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(getEntityBoundingBox());
    }

    @Override public int getMaxSpawnedInChunk() { return 2; }
    @Override protected ResourceLocation getLootTable() { return LOOT; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_VEX_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.ENTITY_VEX_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_VEX_DEATH; }
    @Override protected float getSoundPitch() { return 1.4F + rand.nextFloat() * 0.2F; }
    @Override protected float getSoundVolume() { return 0.6F; }
    @Override public float getBrightness() { return 1.0F; }

    /** Fly at the target and hit it on contact (once a second). */
    class AIDive extends EntityAIBase {
        AIDive() { setMutexBits(1); }
        @Override public boolean shouldExecute() {
            EntityLivingBase t = getAttackTarget();
            return t != null && t.isEntityAlive() && getDistanceSq(t) < 24 * 24 && getEntitySenses().canSee(t);
        }
        @Override public boolean shouldContinueExecuting() { return shouldExecute(); }
        @Override public void updateTask() {
            EntityLivingBase t = getAttackTarget();
            if (t == null) return;
            getLookHelper().setLookPositionWithEntity(t, 30F, 30F);
            moveHelper.setMoveTo(t.posX, t.posY + t.height * 0.5, t.posZ, 1.0D);
            if (attackCooldown <= 0 && getEntityBoundingBox().grow(0.4D).intersects(t.getEntityBoundingBox())) {
                attackCooldown = 20;
                float dmg = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                t.attackEntityFrom(DamageSource.causeMobDamage(EntityEmeraldWisp.this), dmg);
            }
        }
    }

    /** Wander to a random free spot nearby. */
    class AIDrift extends EntityAIBase {
        AIDrift() { setMutexBits(1); }
        @Override public boolean shouldExecute() {
            return !moveHelper.isUpdating() && rand.nextInt(4) == 0;
        }
        @Override public boolean shouldContinueExecuting() { return false; }
        @Override public void startExecuting() {
            BlockPos base = new BlockPos(EntityEmeraldWisp.this);
            for (int i = 0; i < 3; i++) {
                BlockPos p = base.add(rand.nextInt(11) - 5, rand.nextInt(3) - 1, rand.nextInt(11) - 5);
                if (world.isAirBlock(p)) {
                    moveHelper.setMoveTo(p.getX() + 0.5, p.getY() + 0.3, p.getZ() + 0.5, 0.45D);
                    break;
                }
            }
        }
    }

    static class WispMoveHelper extends EntityMoveHelper {
        WispMoveHelper(EntityEmeraldWisp w) { super(w); }
        @Override public void onUpdateMoveHelper() {
            if (action != Action.MOVE_TO) return;
            double dx = posX - entity.posX, dy = posY - entity.posY, dz = posZ - entity.posZ;
            double d = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            if (d < 0.5D || entity.collidedHorizontally && entity.getRNG().nextInt(10) == 0) {
                action = Action.WAIT;
                entity.motionX *= 0.5D; entity.motionY *= 0.5D; entity.motionZ *= 0.5D;
                return;
            }
            double k = 0.036D * speed / d;
            entity.motionX += dx * k; entity.motionY += dy * k; entity.motionZ += dz * k;
            entity.rotationYaw = -((float) MathHelper.atan2(entity.motionX, entity.motionZ)) * (180F / (float) Math.PI);
            entity.renderYawOffset = entity.rotationYaw;
        }
    }
}
