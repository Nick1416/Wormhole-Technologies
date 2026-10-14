package com.nick1416.wormholetech.entity;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.derelict.QuantumFieldModulatorBlock;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

/**
 * Guardian of a modulator room on the derelict vessel. Bigger and much tougher than the Emerald
 * Sentinel; boss bar within 32 blocks, never despawns. While it lives the room's Quantum Field
 * Modulator stays locked in its cradle; its death releases it.
 */
public class EntityGiantEmeraldSentinel extends EntityEmeraldSentinel {
    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(WormholeTech.MODID, "entities/giant_emerald_sentinel"));

    private BlockPos home;

    public EntityGiantEmeraldSentinel(World world) {
        super(world);
        setSize(2.2F, 1.9F);
        experienceValue = 200;
    }

    public void setHome(BlockPos p) { home = p; setHomePosAndDistance(p, 12); }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300.0D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(14.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(16.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(6.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override protected ResourceLocation getLootTable() { return LOOT; }
    @Override protected float getSoundPitch() { return 0.6F + rand.nextFloat() * 0.1F; }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (world.isRemote) return;
        BlockPos c = home != null ? home : getPosition();
        int r = QuantumFieldModulatorBlock.RELEASE_RANGE;
        for (BlockPos p : BlockPos.getAllInBoxMutable(c.add(-r, -4, -r), c.add(r, 4, r)))
            if (world.getBlockState(p).getBlock() instanceof QuantumFieldModulatorBlock) QuantumFieldModulatorBlock.unlock(world, p.toImmutable());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if (home != null) tag.setLong("Home", home.toLong());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("Home")) setHome(BlockPos.fromLong(tag.getLong("Home")));
    }
}
