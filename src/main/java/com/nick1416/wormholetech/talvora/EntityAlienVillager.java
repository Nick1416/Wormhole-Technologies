package com.nick1416.wormholetech.talvora;

import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/** Tall, passive Tal'Vora native that trades exotic goods for emeralds and naquadah. */
public class EntityAlienVillager extends EntityCreature implements IMerchant, INpc {
    private EntityPlayer customer;
    private MerchantRecipeList trades;

    public EntityAlienVillager(World w) {
        super(w);
        setSize(0.6F, 2.3F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 0.7D));
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.5D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.45D);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (isEntityAlive() && customer == null && !player.isSneaking()) {
            if (!world.isRemote) {
                setCustomer(player);
                player.displayVillagerTradeGui(this);
            }
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (customer != null) {
            getNavigator().clearPath();
            if (!customer.isEntityAlive() || customer.openContainer == customer.inventoryContainer || customer.getDistanceSq(this) > 64) customer = null;
        }
    }

    private static ItemStack s(net.minecraft.item.Item i, int n) { return new ItemStack(i, n); }
    private static ItemStack s(net.minecraft.block.Block b, int n) { return new ItemStack(b, n); }

    private MerchantRecipeList buildTrades() {
        MerchantRecipeList l = new MerchantRecipeList();
        l.add(new MerchantRecipe(s(Items.EMERALD, 3), ItemStack.EMPTY, s(Registration.XENOLITH, 16), 0, 9999));
        l.add(new MerchantRecipe(s(Items.EMERALD, 5), ItemStack.EMPTY, s(Registration.XENO_LAMP, 4), 0, 9999));
        l.add(new MerchantRecipe(s(Registration.REFINED_NAQUADAH, 8), ItemStack.EMPTY, s(Registration.PRYSMIAN, 2), 0, 9999));
        l.add(new MerchantRecipe(s(Items.EMERALD, 12), s(Registration.REFINED_NAQUADAH, 4), s(Registration.ITEM_NAQUADAH_CRYSTAL, 2), 0, 9999));
        l.add(new MerchantRecipe(s(Items.EMERALD, 32), s(Items.DIAMOND, 2), s(Registration.NANITE_CORE, 1), 0, 9999));
        return l;
    }

    @Override public void setCustomer(@Nullable EntityPlayer p) { customer = p; }
    @Nullable @Override public EntityPlayer getCustomer() { return customer; }

    @Nullable @Override
    public MerchantRecipeList getRecipes(EntityPlayer p) {
        if (trades == null) trades = buildTrades();
        return trades;
    }

    @SideOnly(Side.CLIENT) @Override public void setRecipes(@Nullable MerchantRecipeList l) { trades = l; }

    @Override
    public void useRecipe(MerchantRecipe r) {
        livingSoundTime = -getTalkInterval();
        playSound(SoundEvents.ENTITY_VILLAGER_YES, getSoundVolume(), 0.55F);
    }

    @Override public void verifySellingItem(ItemStack s) {}
    @Override public World getWorld() { return world; }
    @Override public BlockPos getPos() { return new BlockPos(this); }

    @Override protected SoundEvent getAmbientSound() { return customer != null ? SoundEvents.ENTITY_VILLAGER_TRADING : SoundEvents.ENTITY_VILLAGER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(net.minecraft.util.DamageSource d) { return SoundEvents.ENTITY_VILLAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_VILLAGER_DEATH; }
    @Override protected float getSoundPitch() { return 0.55F + rand.nextFloat() * 0.1F; }
    @Override protected boolean canDespawn() { return false; }
}
