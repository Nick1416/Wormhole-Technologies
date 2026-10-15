package com.nick1416.wormholetech.stargate;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * Gate destinations a player has travelled to, for the Gate Network Computer's map.
 * Stored per player in persisted NBT; earlier visits are also picked up from the arrival advancements.
 */
public final class GateNetwork {
    private GateNetwork() {}

    public static final String VISITED_TAG = WormholeTech.MODID + ":visited_gates";
    /** Map destinations, in marker order. */
    public static final String[] KEYS = { "p3x", "derelict", "home", "tal_vora", "sel_vora" };
    /** Index of the Overworld home marker: always on the map. */
    public static final int HOME = 2;
    /** Tal'Vora sits in the other-galaxy inset of the map. */
    public static final int TAL_VORA = 3;
    /** Tal'Vora's moon, next to it in the inset; unlocked by Repository research (> 50% of Tal'Vora). */
    public static final int SEL_VORA = 4;
    public static final ResourceLocation SEL_VORA_ADVANCEMENT = new ResourceLocation(WormholeTech.MODID, "sel_vora");
    public static final ResourceLocation TAL_VORA_ADVANCEMENT = new ResourceLocation(WormholeTech.MODID, "tal_vora");
    /** Marker positions on their own galaxy's map (0..1 of the map area). */
    public static final float[][] MAP_POS = { { 0.64F, 0.36F }, { 0.31F, 0.68F }, { 0.80F, 0.66F }, { 0.58F, 0.47F }, { 0.73F, 0.36F } };
    /** Galaxies on the universe view: 0 = Milky Way, 1 = Tal'Vora Galaxy (see ModDimensions.galaxy). */
    public static final int GALAXIES = 2;
    public static final float[][] GALAXY_POS = { { 0.33F, 0.62F }, { 0.72F, 0.30F } };

    /** Galaxy of marker i (single source of truth: ModDimensions.galaxy, as used by the Modulator rule). */
    public static int galaxy(int i) { return ModDimensions.galaxy(dimension(i)); }

    public static int count() { return KEYS.length; }

    public static int dimension(int i) { return i == SEL_VORA ? ModDimensions.selVoraId() : i == TAL_VORA ? ModDimensions.talVoraId() : i == HOME ? 0 : i == 0 ? ModDimensions.id() : ModDimensions.derelictId(); }

    public static boolean registered(int i) { if (i == SEL_VORA) return ModDimensions.isSelVoraRegistered(); if (i == TAL_VORA) return ModDimensions.isTalVoraRegistered(); return i == HOME || (i == 0 ? ModDimensions.isRegistered() : ModDimensions.isDerelictRegistered()); }

    /** Marker index of the dimension a player is in, or -1. */
    public static int here(int dim) { return indexOf(dim); }

    public static int indexOf(int dim) {
        for (int i = 0; i < count(); i++) if (registered(i) && dimension(i) == dim) return i;
        return -1;
    }

    private static NBTTagCompound persisted(EntityPlayer p) {
        NBTTagCompound data = p.getEntityData();
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG, Constants.NBT.TAG_COMPOUND))
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }

    public static void recordVisit(EntityPlayer p, int dim) {
        int i = indexOf(dim);
        if (i < 0) return;
        NBTTagCompound t = persisted(p);
        t.setInteger(VISITED_TAG, t.getInteger(VISITED_TAG) | 1 << i);
    }

    /** Bit i set = destination i visited (server side). */
    public static int visitedMask(EntityPlayerMP p) {
        int m = persisted(p).getInteger(VISITED_TAG) | 1 << HOME;
        m |= done(p, StargateTravel.ADVANCEMENT) && registered(0) ? 1 : 0;
        m |= done(p, StargateTravel.DERELICT_ADVANCEMENT) && registered(1) ? 2 : 0;
        m |= done(p, TAL_VORA_ADVANCEMENT) && registered(TAL_VORA) ? 1 << TAL_VORA : 0;
        m |= (done(p, SEL_VORA_ADVANCEMENT) || moonKnown(p)) && registered(SEL_VORA) ? 1 << SEL_VORA : 0;
        return m;
    }

    /** The moon's address is known once the player's Tal'Vora research passes 50%. */
    public static boolean moonKnown(EntityPlayerMP p) {
        return com.nick1416.wormholetech.knowledge.KnowledgeData.get(p.world).percent(p.getUniqueID(), com.nick1416.wormholetech.knowledge.Planets.TAL_VORA) > 50;
    }

    private static boolean done(EntityPlayerMP p, ResourceLocation id) {
        Advancement a = p.mcServer.getAdvancementManager().getAdvancement(id);
        return a != null && p.getAdvancements().getProgress(a).isDone();
    }
}
