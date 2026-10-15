package com.nick1416.wormholetech.moon;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

/**
 * Regolith Crawler: a pale, many-legged hunter of Sel'Vora's endless night. Spider-like (climbs walls),
 * a little tougher and harder-hitting than a spider. Never spawns in or near a research outpost.
 * Drops string and sometimes a Titanium Ingot.
 */
public class EntityRegolithCrawler extends EntitySpider {
    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(WormholeTech.MODID, "entities/regolith_crawler"));

    public EntityRegolithCrawler(World w) {
        super(w);
        setSize(1.5F, 0.8F);
        experienceValue = 8;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(22.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.31D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        if (!ModDimensions.isSelVoraRegistered() || world.provider.getDimension() != ModDimensions.selVoraId()) return false;
        if (SelVoraOutposts.inside((int) Math.floor(posX), (int) Math.floor(posZ), 12)) return false;
        return super.getCanSpawnHere();
    }

    /** No spider jockeys or potion effects. */
    @Override
    public net.minecraft.entity.IEntityLivingData onInitialSpawn(net.minecraft.world.DifficultyInstance d, net.minecraft.entity.IEntityLivingData data) {
        return data;
    }

    @Override protected ResourceLocation getLootTable() { return LOOT; }
}
