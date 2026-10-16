package com.nick1416.wormholetech.utopis;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/** Emerald Droid: a humanoid of metal and emerald that fires green lasers at range, like a skeleton's bow. */
public class EntityEmeraldDroid extends EntityMob implements IRangedAttackMob {
    public static final ResourceLocation LOOT = new ResourceLocation(WormholeTech.MODID, "entities/emerald_droid");

    public EntityEmeraldDroid(World w) {
        super(w);
        setSize(0.6F, 1.95F);
        isImmuneToFire = true;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 30, 16.0F));
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 12.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
    }

    /** Hitscan laser: a visible green beam; damage about that of a skeleton arrow. */
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float pull) {
        if (!canEntityBeSeen(target)) return;
        Vec3d from = new Vec3d(posX, posY + getEyeHeight() - 0.1, posZ);
        Vec3d to = new Vec3d(target.posX, target.posY + target.height * 0.6, target.posZ);
        if (world instanceof WorldServer) {
            Vec3d d = to.subtract(from);
            int steps = (int) (d.lengthVector() * 4);
            for (int i = 0; i <= steps; i++) {
                Vec3d p = from.add(d.scale(i / (double) steps));
                ((WorldServer) world).spawnParticle(EnumParticleTypes.REDSTONE, true, p.x, p.y, p.z, 0, 0.01, 1.0, 0.3, 1.0);
            }
        }
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.8F, 1.8F + rand.nextFloat() * 0.2F);
        float dmg = 2.0F + rand.nextInt(3) + (world.getDifficulty() == EnumDifficulty.HARD ? 1 : 0);
        target.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), dmg);
    }

    @Override public void setSwingingArms(boolean b) {}

    @Override
    public boolean getCanSpawnHere() {
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL || !ModDimensions.isUtopis(world.provider.getDimension())) return false;
        BlockPos pos = new BlockPos(posX, getEntityBoundingBox().minY, posZ);
        return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP)
                && world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(getEntityBoundingBox());
    }

    @Override protected ResourceLocation getLootTable() { return LOOT; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_IRONGOLEM_STEP; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.ENTITY_IRONGOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_IRONGOLEM_DEATH; }
    @Override protected float getSoundPitch() { return 1.5F; }
}
