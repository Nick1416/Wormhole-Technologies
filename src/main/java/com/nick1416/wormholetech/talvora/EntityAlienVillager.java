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

/** Tall, passive Tal'Vora native that trades exotic goods for emeralds. */
public class EntityAlienVillager extends EntityCreature implements IMerchant, INpc {
    private static final net.minecraft.network.datasync.DataParameter<Boolean> MEDITATING =
            net.minecraft.network.datasync.EntityDataManager.createKey(EntityAlienVillager.class, net.minecraft.network.datasync.DataSerializers.BOOLEAN);
    private EntityPlayer customer;
    private MerchantRecipeList trades;

    public EntityAlienVillager(World w) {
        super(w);
        setSize(0.6F, 2.3F);
    }

    private static final net.minecraft.network.datasync.DataParameter<Boolean> RESEARCHING =
            net.minecraft.network.datasync.EntityDataManager.createKey(EntityAlienVillager.class, net.minecraft.network.datasync.DataSerializers.BOOLEAN);
    @Override protected void entityInit() { super.entityInit(); dataManager.register(MEDITATING, false); dataManager.register(RESEARCHING, false); }
    /** Sel'Vora outpost researcher: stands at a console studying it, does not trade. */
    public boolean isResearching() { return dataManager.get(RESEARCHING); }
    public void setResearching(boolean r) { dataManager.set(RESEARCHING, r); }
    private boolean still() { return isMeditating() || isResearching(); }
    public boolean isMeditating() { return dataManager.get(MEDITATING); }
    public void setMeditating(boolean m) { dataManager.set(MEDITATING, m); }

    @Override public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound c) { super.writeEntityToNBT(c); c.setBoolean("Meditating", isMeditating()); c.setBoolean("Researching", isResearching()); c.setFloat("StudyYaw", rotationYaw); }
    @Override public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound c) { super.readEntityFromNBT(c); setMeditating(c.getBoolean("Meditating")); setResearching(c.getBoolean("Researching")); }

    /** Meditating Vorani stay put (no wandering, no pushing). */
    @Override public boolean canBePushed() { return !still() && super.canBePushed(); }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 0.7D) {
            @Override public boolean shouldExecute() { return !still() && super.shouldExecute(); }
        });
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.5D) {
            @Override public boolean shouldExecute() { return !still() && super.shouldExecute(); }
        });
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F) {
            @Override public boolean shouldExecute() { return !isResearching() && super.shouldExecute(); }
        });
        tasks.addTask(7, new EntityAILookIdle(this) {
            @Override public boolean shouldExecute() { return !isResearching() && super.shouldExecute(); }
        });
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.45D);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (isMeditating()) { // deep in meditation: no trading, just an action-bar note (main hand only = once per click)
            if (!world.isRemote && hand == EnumHand.MAIN_HAND)
                player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation("message.wormholetech.vorani.meditating"), true);
            return true;
        }
        if (isResearching()) {
            if (!world.isRemote) player.sendStatusMessage(new net.minecraft.util.text.TextComponentTranslation("message.wormholetech.vorani.researching"), true);
            return true;
        }
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
        if (isMeditating()) {
            motionX = 0; motionZ = 0; getNavigator().clearPath();
            if (world.isRemote && rand.nextInt(8) == 0)
                world.spawnParticle(net.minecraft.util.EnumParticleTypes.ENCHANTMENT_TABLE, posX + (rand.nextDouble() - 0.5) * 1.2,
                        posY + 1.6 + rand.nextDouble() * 0.6, posZ + (rand.nextDouble() - 0.5) * 1.2, 0, 0.1, 0);
            if (world.isRemote && rand.nextInt(40) == 0)
                world.spawnParticle(net.minecraft.util.EnumParticleTypes.END_ROD, posX, posY + 2.0, posZ, 0, 0.02, 0);
        }
        if (isResearching()) {
            // keep studying the neutronium beyond the console: fixed gaze, data sparks rising between
            motionX = 0; motionZ = 0; getNavigator().clearPath();
            rotationYawHead = rotationYaw; renderYawOffset = rotationYaw; rotationPitch = 8F; // over the console, at the neutronium in the middle of the room
            if (world.isRemote && rand.nextInt(5) == 0) {
                double fx = -Math.sin(Math.toRadians(rotationYaw)) * 1.3, fz = Math.cos(Math.toRadians(rotationYaw)) * 1.3;
                world.spawnParticle(net.minecraft.util.EnumParticleTypes.ENCHANTMENT_TABLE, posX + fx, posY + 1.2, posZ + fz,
                        -fx * 0.6, 0.6, -fz * 0.6);
            }
        }
        if (customer != null) {
            getNavigator().clearPath();
            if (!customer.isEntityAlive() || customer.openContainer == customer.inventoryContainer || customer.getDistanceSq(this) > 64) customer = null;
        }
    }

    private static ItemStack s(net.minecraft.item.Item i, int n) { return new ItemStack(i, n); }
    private static ItemStack s(net.minecraft.block.Block b, int n) { return new ItemStack(b, n); }

    private MerchantRecipeList buildTrades() {
        MerchantRecipeList l = new MerchantRecipeList();
        ItemStack redSand = new ItemStack(net.minecraft.init.Blocks.SAND, 32, 1);
        l.add(new MerchantRecipe(s(Items.EMERALD, 3), ItemStack.EMPTY, s(Registration.XENOLITH, 16), 0, 9999));
        l.add(new MerchantRecipe(s(Items.EMERALD, 4), ItemStack.EMPTY, s(Registration.XENOLITH_TILES, 16), 0, 9999));
        l.add(new MerchantRecipe(s(Items.EMERALD, 5), ItemStack.EMPTY, s(Registration.XENO_LAMP, 4), 0, 9999));
        l.add(new MerchantRecipe(redSand, ItemStack.EMPTY, s(Items.EMERALD, 1), 0, 9999));
        l.add(new MerchantRecipe(s(Registration.RAW_XENOLITH, 8), ItemStack.EMPTY, s(Items.EMERALD, 2), 0, 9999));
        l.add(new MerchantRecipe(s(Items.GOLD_INGOT, 4), ItemStack.EMPTY, s(Items.EMERALD, 1), 0, 9999));
        l.add(new MerchantRecipe(s(Items.EMERALD, 56), s(Registration.RAW_XENOLITH, 16), s(Registration.NANITE_CORE, 1), 0, 9999));
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
