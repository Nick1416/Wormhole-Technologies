package com.nick1416.wormholetech.entity;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.registry.Registration;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

/** Small hostile scrap robot of P3X-1416. Melee only; drops a few emeralds. Spawns only on the P3X surface. */
public class EntityEmeraldDrone extends EntityMob {
    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(WormholeTech.MODID, "entities/emerald_drone"));

    public EntityEmeraldDrone(World world) {
        super(world);
        setSize(0.8F, 0.7F);
        experienceValue = 6;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackMelee(this, 1.1D, false));
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
    }

    /** Only on the open surface of P3X-1416, day or night. */
    @Override
    public boolean getCanSpawnHere() {
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) return false;
        BlockPos pos = new BlockPos(posX, getEntityBoundingBox().minY, posZ);
        if (com.nick1416.wormholetech.derelict.DerelictSpawner.isDerelict(world)) {
            // anywhere on the ship's deck (not on the outer hull)
            if (!com.nick1416.wormholetech.derelict.DerelictSpawner.canSpawnAt(posX, posY, posZ, height)) return false;
            IBlockState deck = world.getBlockState(pos.down());
            return deck.isSideSolid(world, pos.down(), net.minecraft.util.EnumFacing.UP)
                    && world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(getEntityBoundingBox());
        }
        if (!ModDimensions.isRegistered() || world.provider.getDimension() != ModDimensions.id()) return false;
        // open sky, or bright skylight (under a thin overhang, spire or floating rock) - never in caves
        if (!world.canSeeSky(pos) && world.getLightFor(net.minecraft.world.EnumSkyBlock.SKY, pos) < 10) return false;
        // any solid top surface (also works in P3X chunks generated before Naquadric Stone existed)
        BlockPos down = pos.down();
        IBlockState below = world.getBlockState(down);
        boolean ground = below.isSideSolid(world, down, net.minecraft.util.EnumFacing.UP) && below.getBlock() != Blocks.BEDROCK;
        return ground && world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(getEntityBoundingBox());
    }

    @Override
    public int getMaxSpawnedInChunk() { return 2; }

    private static final java.util.UUID STORM_RANGE = java.util.UUID.fromString("6f1a3c52-2b8e-4f0e-9a51-3d1416e7a001");
    private static final java.util.UUID STORM_SPEED = java.util.UUID.fromString("6f1a3c52-2b8e-4f0e-9a51-3d1416e7a002");

    /** During a P3X dust storm drones see farther (+16 follow range) and move 20% faster. */
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (world.isRemote || ticksExisted % 20 != 0) return;
        boolean storm = com.nick1416.wormholetech.world.P3XWeather.isStorm(world);
        toggle(SharedMonsterAttributes.FOLLOW_RANGE, STORM_RANGE, "Dust storm range", 16.0D, 0, storm);
        toggle(SharedMonsterAttributes.MOVEMENT_SPEED, STORM_SPEED, "Dust storm speed", 0.2D, 1, storm);
    }

    public boolean isStormBoosted() {
        return getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(STORM_SPEED) != null;
    }

    private void toggle(net.minecraft.entity.ai.attributes.IAttribute a, java.util.UUID id, String name, double amount, int op, boolean on) {
        net.minecraft.entity.ai.attributes.IAttributeInstance inst = getEntityAttribute(a);
        boolean has = inst.getModifier(id) != null;
        if (on && !has) inst.applyModifier(new net.minecraft.entity.ai.attributes.AttributeModifier(id, name, amount, op).setSaved(false));
        else if (!on && has) inst.removeModifier(id);
    }

    @Override
    protected ResourceLocation getLootTable() { return LOOT; }

    @Override protected SoundEvent getAmbientSound() { return null; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.ENTITY_IRONGOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_IRONGOLEM_DEATH; }
    @Override protected float getSoundPitch() { return 1.6F + rand.nextFloat() * 0.2F; }
    @Override protected float getSoundVolume() { return 0.6F; }

    @Override
    protected void playStepSound(BlockPos pos, Block block) {
        playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.8F);
    }

    @Override
    public boolean attackEntityAsMob(net.minecraft.entity.Entity target) {
        boolean hit = super.attackEntityAsMob(target);
        if (hit) playSound(SoundEvents.BLOCK_ANVIL_LAND, 0.25F, 1.9F);
        return hit;
    }
}
