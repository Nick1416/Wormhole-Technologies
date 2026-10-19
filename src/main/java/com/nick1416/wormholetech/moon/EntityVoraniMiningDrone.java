package com.nick1416.wormholetech.moon;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.talvora.EntityXenolithDrone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
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
import net.minecraft.world.World;

/**
 * Vorani Mining Drone of Sel'Vora. Works the open pit of a Vorani mining site: hovers over a block,
 * cuts at it with a blue beam (sparks, block dust, a soft grinding sound) and now and then returns to the
 * site's Neutronium Reactor to recharge, linked to it by a stream of light. It never breaks blocks.
 * Neutral: it leaves players alone until one hurts it or a drone next to it, then it zaps back for a
 * while ({@code voraniMiningDroneDamage} per hit) and returns to work.
 */
public class EntityVoraniMiningDrone extends EntityFlying {
    /** Beam target: 0 = none, 1 = the mined block, 2 = the reactor link, 3 = an attacker. */
    private static final DataParameter<Integer> BEAM = EntityDataManager.createKey(EntityVoraniMiningDrone.class, DataSerializers.VARINT);
    private static final DataParameter<BlockPos> TARGET = EntityDataManager.createKey(EntityVoraniMiningDrone.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Integer> FOE = EntityDataManager.createKey(EntityVoraniMiningDrone.class, DataSerializers.VARINT);

    private BlockPos pit, reactor;
    private EntityLivingBase angryAt;
    private int angryTicks;

    public EntityVoraniMiningDrone(World w) {
        super(w);
        setSize(0.9F, 0.7F);
        moveHelper = new EntityXenolithDrone.DroneMoveHelper(this);
        experienceValue = 3;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(BEAM, 0);
        dataManager.register(TARGET, BlockPos.ORIGIN);
        dataManager.register(FOE, -1);
    }

    public int beam() { return dataManager.get(BEAM); }
    public BlockPos beamTarget() { return dataManager.get(TARGET); }

    public void setSite(BlockPos pit, BlockPos reactor) { this.pit = pit; this.reactor = reactor; }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new AIDefend());
        tasks.addTask(3, new AIRecharge());
        tasks.addTask(5, new AIMine());
        tasks.addTask(8, new AIHover());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
    }

    /** Natural spawns only at a mining site of Sel'Vora, and at most voraniMiningDronesPerSite + 1 there. */
    @Override
    public boolean getCanSpawnHere() {
        if (world.provider.getDimension() != ModConfig.selVoraDimensionId) return false;
        int[] s = SelVoraMiningSites.siteNear((int) Math.floor(posX), (int) Math.floor(posZ));
        if (s == null || (s[0] - posX) * (s[0] - posX) + (s[1] - posZ) * (s[1] - posZ) > 20 * 20) return false;
        if (world.getEntitiesWithinAABB(EntityVoraniMiningDrone.class, getEntityBoundingBox().grow(32, 64, 32)).size()
                > ModConfig.voraniMiningDronesPerSite) return false;
        return world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty();
    }

    @Override public int getMaxSpawnedInChunk() { return 1; }
    @Override protected boolean canDespawn() { return false; }
    @Override public boolean isPushedByWater() { return false; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) { beamParticles(); return; }
        if (pit == null) adoptSite();
        if (angryTicks > 0 && --angryTicks == 0) angryAt = null;
    }

    /** Spawned naturally (or lost its NBT): take the nearest site and its reactor. */
    private void adoptSite() {
        int[] s = SelVoraMiningSites.siteNear((int) Math.floor(posX), (int) Math.floor(posZ));
        if (s == null) { pit = new BlockPos(this).down(3); return; }
        int g = world.getHeight(new BlockPos(s[0], 0, s[1])).getY() - 1;
        BlockPos r = SelVoraMiningSites.reactorPos(s[0], g, s[1]);
        // the reactor pillar raises the height map at the reactor column, so look around it
        for (int dy = -3; dy <= 3; dy++)
            if (world.getBlockState(r.up(dy)).getBlock() == com.nick1416.wormholetech.registry.Registration.NEUTRONIUM_REACTOR) { r = r.up(dy); break; }
        reactor = r;
        pit = SelVoraMiningSites.pitPos(s[0], r.getY() - 2, s[1]);
    }

    private void beamParticles() {
        int b = beam();
        if (b == 0) return;
        double tx, ty, tz;
        if (b == 3) {
            net.minecraft.entity.Entity e = world.getEntityByID(dataManager.get(FOE));
            if (e == null) return;
            tx = e.posX; ty = e.posY + e.height * 0.6; tz = e.posZ;
        } else {
            BlockPos t = beamTarget();
            tx = t.getX() + 0.5; ty = t.getY() + (b == 1 ? 1.0 : 0.5); tz = t.getZ() + 0.5;
        }
        double sx = posX, sy = posY + (b == 2 ? 0.6 : 0.0), sz = posZ;
        int n = b == 2 ? 14 : 8;
        for (int i = 0; i <= n; i++) {
            double f = i / (double) n;
            double x = sx + (tx - sx) * f, y = sy + (ty - sy) * f, z = sz + (tz - sz) * f;
            // blue neutronium light (coloured redstone dust) with a white core
            world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0001D, 0.65D, 1.0D);
            if ((i & 1) == 0 && b != 2) world.spawnParticle(EnumParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
        if (b == 1 && (ticksExisted & 1) == 0) {
            BlockPos t = beamTarget();
            IBlockState st = world.getBlockState(t);
            if (!st.getBlock().isAir(st, world, t))
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, tx + (rand.nextDouble() - 0.5) * 0.8, ty + 0.05, tz + (rand.nextDouble() - 0.5) * 0.8,
                        0, 0.08, 0, net.minecraft.block.Block.getStateId(st));
            world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, tx, ty + 0.05, tz,
                    (rand.nextDouble() - 0.5) * 0.15, 0.08, (rand.nextDouble() - 0.5) * 0.15);
        }
        if (b == 2 && (ticksExisted & 3) == 0)
            world.spawnParticle(EnumParticleTypes.PORTAL, posX, posY + 0.3, posZ, 0, -0.2, 0);
    }

    private void setBeam(int b, BlockPos t) { dataManager.set(BEAM, b); if (t != null) dataManager.set(TARGET, t); }

    private boolean reactorPresent() {
        return reactor != null && world.isBlockLoaded(reactor)
                && world.getBlockState(reactor).getBlock() == com.nick1416.wormholetech.registry.Registration.NEUTRONIUM_REACTOR;
    }

    /** Hover over a pit block and cut at it with the beam (purely visual; nothing is removed). */
    class AIMine extends EntityAIBase {
        BlockPos block;
        int left;
        AIMine() { setMutexBits(1); }
        @Override public boolean shouldExecute() {
            if (pit == null || rand.nextInt(20) != 0) return false;
            for (int tries = 0; tries < 8; tries++) {
                BlockPos c = pit.add(rand.nextInt(7) - 3, 0, rand.nextInt(7) - 3);
                for (int dy = 1; dy >= -5; dy--) {
                    BlockPos p = c.up(dy);
                    IBlockState s = world.getBlockState(p);
                    if (s.isOpaqueCube() && world.isAirBlock(p.up())) { block = p; return true; }
                }
            }
            return false;
        }
        @Override public void startExecuting() { left = 80 + rand.nextInt(80); }
        @Override public boolean shouldContinueExecuting() { return left > 0 && angryAt == null; }
        @Override public void updateTask() {
            left--;
            double hx = block.getX() + 0.5, hy = block.getY() + 3.2, hz = block.getZ() + 0.5;
            double d2 = (posX - hx) * (posX - hx) + (posY - hy) * (posY - hy) + (posZ - hz) * (posZ - hz);
            if (d2 > 1.5 * 1.5) { moveHelper.setMoveTo(hx, hy, hz, 0.6D); setBeam(0, null); return; }
            motionX += (hx - posX) * 0.02 - motionX * 0.2;
            motionY += (hy - posY) * 0.04 - motionY * 0.2;
            motionZ += (hz - posZ) * 0.02 - motionZ * 0.2;
            getLookHelper().setLookPosition(hx, block.getY() + 1, hz, 10F, 30F);
            if (beam() != 1) setBeam(1, block);
            if (left % 12 == 0) {
                IBlockState s = world.getBlockState(block);
                world.playSound(null, hx, block.getY() + 1, hz, s.getBlock().getSoundType(s, world, block, null).getHitSound(),
                        SoundCategory.NEUTRAL, 0.35F, 0.7F + rand.nextFloat() * 0.2F);
            }
            if (left % 40 == 0) world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.NEUTRAL, 0.2F, 0.7F);
        }
        @Override public void resetTask() { setBeam(0, null); block = null; }
    }

    /** Fly up to the reactor and draw power from it for a few seconds. */
    class AIRecharge extends EntityAIBase {
        int left;
        AIRecharge() { setMutexBits(1); }
        @Override public boolean shouldExecute() { return angryAt == null && rand.nextInt(500) == 0 && reactorPresent(); }
        @Override public void startExecuting() { left = 60 + rand.nextInt(60); }
        @Override public boolean shouldContinueExecuting() { return left > 0 && angryAt == null && reactorPresent(); }
        @Override public void updateTask() {
            left--;
            double hx = reactor.getX() + 0.5 + (getEntityId() % 3 - 1) * 1.5, hy = reactor.getY() + 2.5, hz = reactor.getZ() + 0.5 + (getEntityId() % 2 == 0 ? 1.2 : -1.2);
            double d2 = (posX - hx) * (posX - hx) + (posY - hy) * (posY - hy) + (posZ - hz) * (posZ - hz);
            if (d2 > 2.5 * 2.5) { moveHelper.setMoveTo(hx, hy, hz, 0.7D); return; }
            motionX *= 0.6; motionY *= 0.6; motionZ *= 0.6;
            if (beam() != 2) {
                setBeam(2, reactor);
                world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.NEUTRAL, 0.25F, 1.6F);
            }
        }
        @Override public void resetTask() { setBeam(0, null); }
    }

    /** Drift slowly over the site. */
    class AIHover extends EntityAIBase {
        AIHover() { setMutexBits(1); }
        @Override public boolean shouldExecute() { return pit != null && !moveHelper.isUpdating() && rand.nextInt(30) == 0; }
        @Override public boolean shouldContinueExecuting() { return false; }
        @Override public void startExecuting() {
            moveHelper.setMoveTo(pit.getX() + 0.5 + rand.nextInt(9) - 4, pit.getY() + 3 + rand.nextInt(3), pit.getZ() + 0.5 + rand.nextInt(9) - 4, 0.4D);
        }
    }

    /** Zap whoever hurt this drone (or one nearby) until it gives up or angryTicks run out. */
    class AIDefend extends EntityAIBase {
        int cooldown;
        AIDefend() { setMutexBits(1); }
        @Override public boolean shouldExecute() { return angryAt != null && angryAt.isEntityAlive(); }
        @Override public boolean shouldContinueExecuting() { return angryAt != null && angryAt.isEntityAlive() && getDistanceSq(angryAt) < 32 * 32; }
        @Override public void updateTask() {
            double hx = angryAt.posX, hy = angryAt.posY + 3.0, hz = angryAt.posZ;
            if (getDistanceSq(hx, hy, hz) > 3 * 3) moveHelper.setMoveTo(hx, hy, hz, 1.0D);
            getLookHelper().setLookPositionWithEntity(angryAt, 30F, 30F);
            if (--cooldown <= 0 && getDistanceSq(angryAt) < 8 * 8 && canEntityBeSeen(angryAt)) {
                cooldown = 30;
                dataManager.set(FOE, angryAt.getEntityId());
                dataManager.set(BEAM, 3);
                angryAt.attackEntityFrom(DamageSource.causeIndirectMagicDamage(EntityVoraniMiningDrone.this, EntityVoraniMiningDrone.this),
                        (float) ModConfig.voraniMiningDroneDamage);
                world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.HOSTILE, 0.4F, 1.8F);
            } else if (cooldown < 20 && beam() == 3) {
                dataManager.set(BEAM, 0);
            }
        }
        @Override public void resetTask() { dataManager.set(BEAM, 0); angryAt = null; angryTicks = 0; }
    }

    private void provoke(EntityLivingBase who) {
        if (who == null || who == this || who instanceof EntityVoraniMiningDrone) return;
        if (who instanceof EntityPlayer && (((EntityPlayer) who).isCreative() || ((EntityPlayer) who).isSpectator())) return;
        angryAt = who;
        angryTicks = 20 * 20;
    }

    @Override
    public boolean attackEntityFrom(DamageSource s, float a) {
        boolean hurt = super.attackEntityFrom(s, a);
        if (hurt && !world.isRemote && s.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase who = (EntityLivingBase) s.getTrueSource();
            provoke(who);
            for (EntityVoraniMiningDrone d : world.getEntitiesWithinAABB(EntityVoraniMiningDrone.class, getEntityBoundingBox().grow(16)))
                if (d != this && d.angryAt == null) d.provoke(who);
        }
        return hurt;
    }

    @Override protected SoundEvent getAmbientSound() { return null; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.BLOCK_METAL_HIT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BLOCK_GLASS_BREAK; }
    @Override protected float getSoundPitch() { return 1.3F; }
    @Override public float getBrightness() { return 1.0F; }
    @Override protected net.minecraft.util.ResourceLocation getLootTable() {
        return new net.minecraft.util.ResourceLocation(com.nick1416.wormholetech.WormholeTech.MODID, "entities/vorani_mining_drone");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound c) {
        super.writeEntityToNBT(c);
        if (pit != null) c.setLong("Pit", pit.toLong());
        if (reactor != null) c.setLong("Reactor", reactor.toLong());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound c) {
        super.readEntityFromNBT(c);
        if (c.hasKey("Pit")) pit = BlockPos.fromLong(c.getLong("Pit"));
        if (c.hasKey("Reactor")) reactor = BlockPos.fromLong(c.getLong("Reactor"));
    }
}
