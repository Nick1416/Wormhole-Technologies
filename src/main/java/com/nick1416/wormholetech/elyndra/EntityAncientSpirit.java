package com.nick1416.wormholetech.elyndra;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.knowledge.KnowledgeData;
import com.nick1416.wormholetech.stargate.StargateTravel;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A pale, floating spirit at the Elyndra shrines. Talk to it (right-click) and it reveals that it ascended;
 * at the end of the talk it sends the player home to the Overworld, the secret way back.
 */
public class EntityAncientSpirit extends EntityLiving {
    public static final int LINES = 4;
    public static final ResourceLocation ADVANCEMENT = new ResourceLocation(WormholeTech.MODID, "ascended_guide");
    private final Map<UUID, Integer> talk = new HashMap<>();

    public EntityAncientSpirit(World w) {
        super(w);
        setSize(0.6F, 1.95F);
        setNoGravity(true);
        setEntityInvulnerable(true);
        enablePersistence();
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(1, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F, 1.0F));
        tasks.addTask(2, new EntityAILookIdle(this));
    }

    @Override public boolean isEntityInvulnerable(DamageSource s) { return s != DamageSource.OUT_OF_WORLD; }
    @Override public boolean canBePushed() { return false; }
    @Override protected boolean canDespawn() { return false; }
    @Override public boolean canBreatheUnderwater() { return true; }
    @Override public void fall(float d, float m) {}
    @Override public int getBrightnessForRender() { return 0xF000F0; }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        motionX = motionY = motionZ = 0;
        if (world.isRemote && rand.nextInt(4) == 0)
            world.spawnParticle(EnumParticleTypes.END_ROD, posX + (rand.nextDouble() - 0.5) * 0.8, posY + rand.nextDouble() * 1.9,
                    posZ + (rand.nextDouble() - 0.5) * 0.8, 0, 0.02, 0);
    }

    @Override
    protected boolean processInteract(EntityPlayer p, EnumHand hand) {
        if (hand != EnumHand.MAIN_HAND) return false;
        if (world.isRemote || !(p instanceof EntityPlayerMP)) return true;
        EntityPlayerMP mp = (EntityPlayerMP) p;
        int line = talk.getOrDefault(p.getUniqueID(), 0) + 1;
        if (line <= LINES) {
            talk.put(p.getUniqueID(), line);
            TextComponentTranslation t = new TextComponentTranslation("message.wormholetech.spirit." + line);
            t.getStyle().setColor(TextFormatting.AQUA).setItalic(true);
            p.sendMessage(t);
            world.playSound(null, getPosition(), SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.NEUTRAL, 0.6F, 0.6F + line * 0.15F);
            return true;
        }
        talk.remove(p.getUniqueID());
        KnowledgeData.get(world).ascendTo(p.getUniqueID(), 1);
        world.playSound(null, getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 0.5F);
        StargateTravel.sendToOverworldBed(mp);
        StargateTravel.grant(mp, ADVANCEMENT);
        TextComponentTranslation t = new TextComponentTranslation("message.wormholetech.spirit.ascended");
        t.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        mp.sendStatusMessage(t, true);
        return true;
    }
}
