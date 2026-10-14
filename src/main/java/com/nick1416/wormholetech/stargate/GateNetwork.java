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
    public static final String[] KEYS = { "p3x", "derelict", "home" };
    /** Index of the Overworld home marker: always on the map. */
    public static final int HOME = 2;
    /** Marker positions on the galaxy map (0..1 of the map area). */
    public static final float[][] MAP_POS = { { 0.64F, 0.36F }, { 0.31F, 0.68F }, { 0.80F, 0.66F } };

    public static int count() { return KEYS.length; }

    public static int dimension(int i) { return i == HOME ? 0 : i == 0 ? ModDimensions.id() : ModDimensions.derelictId(); }

    public static boolean registered(int i) { return i == HOME || (i == 0 ? ModDimensions.isRegistered() : ModDimensions.isDerelictRegistered()); }

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
        return m;
    }

    private static boolean done(EntityPlayerMP p, ResourceLocation id) {
        Advancement a = p.mcServer.getAdvancementManager().getAdvancement(id);
        return a != null && p.getAdvancements().getProgress(a).isDone();
    }
}
