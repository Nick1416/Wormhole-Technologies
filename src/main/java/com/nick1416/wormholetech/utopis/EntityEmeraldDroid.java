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
        tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 60, 80, 16.0F));
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

    /** Charge-up before each shot (ticks): the aim point is locked when charging starts, so the shot can be dodged. */
    public static final int CHARGE = 20;
    private int charge;
    private Vec3d aim;
    private EntityLivingBase aimed;

    /** Starts a charge; the beam fires CHARGE ticks later at where the target was. */
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float pull) {
        if (charge > 0 || !canEntityBeSeen(target)) return;
        aimed = target;
        aim = new Vec3d(target.posX, target.posY + target.height * 0.6, target.posZ);
        if (rand.nextInt(5) == 0) aim = aim.addVector(rand.nextGaussian() * 1.2, rand.nextGaussian() * 0.6, rand.nextGaussian() * 1.2); // ~20% miss
        charge = CHARGE;
        world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_NOTE_PLING, SoundCategory.HOSTILE, 0.6F, 0.5F);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (world.isRemote || charge <= 0) return;
        charge--;
        Vec3d eye = new Vec3d(posX, posY + getEyeHeight() - 0.1, posZ);
        if (world instanceof WorldServer && charge % 2 == 0) // glow building at the visor
            ((WorldServer) world).spawnParticle(EnumParticleTypes.REDSTONE, true, eye.x, eye.y, eye.z, 0, 0.01, 1.0, 0.3, 1.0);
        if (charge == 0 && isEntityAlive()) fire(eye);
    }

    private void fire(Vec3d from) {
        Vec3d d = aim.subtract(from);
        double len = d.lengthVector();
        if (len < 0.1) return;
        Vec3d to = from.add(d.scale(Math.min(24.0, len + 4.0) / len)); // beam carries a little past the aim point
        net.minecraft.util.math.RayTraceResult wall = world.rayTraceBlocks(from, to, false, true, false);
        if (wall != null) to = wall.hitVec;
        if (world instanceof WorldServer) {
            Vec3d seg = to.subtract(from);
            int steps = (int) (seg.lengthVector() * 4);
            for (int i = 0; i <= steps; i++) {
                Vec3d p = from.add(seg.scale(i / (double) Math.max(1, steps)));
                ((WorldServer) world).spawnParticle(EnumParticleTypes.REDSTONE, true, p.x, p.y, p.z, 0, 0.01, 1.0, 0.3, 1.0);
            }
        }
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 0.8F, 1.8F + rand.nextFloat() * 0.2F);
        if (aimed != null && aimed.isEntityAlive() && aimed.getEntityBoundingBox().grow(0.2).calculateIntercept(from, to) != null) {
            float dmg = 1.0F + rand.nextInt(2) + (world.getDifficulty() == EnumDifficulty.HARD ? 1 : 0); // 1-2, max 3 on Hard
            aimed.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this), dmg);
        }
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
