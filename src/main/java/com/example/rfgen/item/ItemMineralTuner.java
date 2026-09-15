package com.example.rfgen.item;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler.MineralMix;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler.MineralWorldInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Mineral tuner for IE 0.12 (1.12.2) that tolerates registry shape differences and
 * MineralWorldInfo ctor/field differences. No compile-time import of IESaveData is required.
 */
public class ItemMineralTuner extends Item {

    private static final String NBT_TARGET = "targetMineral";
    private static final String DEFAULT_TARGET = "Coal";

    public ItemMineralTuner() {
        setRegistryName("mineral_tuner_coal");
        setUnlocalizedName("rfgen.mineral_tuner_coal");
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }

    public static void setTarget(ItemStack stack, String name) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        tag.setString(NBT_TARGET, name);
        stack.setTagCompound(tag);
    }
    private static String getTarget(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return (tag != null && tag.hasKey(NBT_TARGET)) ? tag.getString(NBT_TARGET) : DEFAULT_TARGET;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    	bootstrapIfEmpty();
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);

        if (!Loader.isModLoaded("immersiveengineering")) {
            msg(player, "Immersive Engineering not found.");
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        ensureIESaveData(world); // make sure IE save is initialized

        // Sneak: list minerals
        if (player.isSneaking()) {
            List<String> names = getAllMineralNames();
            if (names.isEmpty()) {
                msg(player, "No IE minerals registered (disabled by config/scripts?).");
            } else {
                Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
                msg(player, "Minerals (" + names.size() + "):");
                for (int i = 0; i < Math.min(12, names.size()); i++) msg(player, " - " + names.get(i));
                if (names.size() > 12) msg(player, " ...and " + (names.size() - 12) + " more");
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        if (isRegistryEmpty()) {
            msg(player, "No IE minerals registered.");
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        String wanted = getTarget(stack);
        MineralMix mix = resolveByName(wanted);
        if (mix == null) {
            msg(player, "Mineral not found: \"" + wanted + "\"");
            suggest(player, wanted);
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        ChunkPos chunk = new ChunkPos(player.getPosition());
        boolean ok = writeChunkMineral(world, chunk, mix);
        if (!ok) {
            msg(player, "Failed to save IE mineral cache.");
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (!player.capabilities.isCreativeMode) stack.shrink(1); // self-destruct on success

        msg(player, "Set IE mineral here to: " + mix.name);
        world.playSound(null, player.getPosition(),
                net.minecraft.init.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.PLAYERS, 0.6f, 1.2f);

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    /* ---------------- helpers ---------------- */

    private static void msg(EntityPlayer p, String s) {
        p.sendStatusMessage(new net.minecraft.util.text.TextComponentString(s), true);
    }

    private static boolean isRegistryEmpty() {
        // IE 0.12 has mineralList; shape may be Map<String,MineralMix> or Map<MineralMix,Integer>
        return ExcavatorHandler.mineralList == null || ExcavatorHandler.mineralList.isEmpty();
    }

    /** Collect all mineral display names regardless of the Map shape. */
    private static List<String> getAllMineralNames() {
        List<String> out = new ArrayList<String>();
        if (ExcavatorHandler.mineralList == null) return out;

        for (Object eObj : ExcavatorHandler.mineralList.entrySet()) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) eObj;
            Object k = e.getKey();
            Object v = e.getValue();

            if (k instanceof String && v instanceof MineralMix) {
                out.add(((MineralMix) v).name);
            } else if (k instanceof MineralMix) {
                out.add(((MineralMix) k).name);
            }
        }
        return out;
    }

    /** Resolve MineralMix by display name (exact, CI, then substring), independent of Map shape. */
    private static MineralMix resolveByName(String wanted) {
        if (wanted == null) return null;
        String w = wanted.trim();
        if (w.isEmpty()) return null;

        // First pass: exact (case-sensitive) by mix.name
        MineralMix found = findFirstByNameMatch(w, 0);
        if (found != null) return found;

        // Second pass: case-insensitive exact
        found = findFirstByNameMatch(w, 1);
        if (found != null) return found;

        // Third pass: case-insensitive substring
        return findFirstByNameMatch(w, 2);
    }

    /** mode: 0=exact, 1=equalsIgnoreCase, 2=contains (lowercase) */
    private static MineralMix findFirstByNameMatch(String needle, int mode) {
        if (ExcavatorHandler.mineralList == null) return null;
        String nl = needle.toLowerCase(Locale.ROOT);

        for (Object eObj : ExcavatorHandler.mineralList.entrySet()) {
            Map.Entry<?, ?> e = (Map.Entry<?, ?>) eObj;
            MineralMix mix = null;

            if (e.getKey() instanceof String && e.getValue() instanceof MineralMix) {
                mix = (MineralMix) e.getValue();
            } else if (e.getKey() instanceof MineralMix) {
                mix = (MineralMix) e.getKey();
            }

            if (mix == null || mix.name == null) continue;

            String n = mix.name;
            switch (mode) {
                case 0: if (n.equals(needle)) return mix; break;
                case 1: if (n.equalsIgnoreCase(needle)) return mix; break;
                case 2: if (n.toLowerCase(Locale.ROOT).contains(nl)) return mix; break;
            }
        }
        return null;
    }

    private static void suggest(EntityPlayer p, String wanted) {
        String wl = wanted.toLowerCase(Locale.ROOT);
        List<String> names = getAllMineralNames();
        List<String> hits = new ArrayList<String>();
        for (int i = 0; i < names.size(); i++) {
            String n = names.get(i);
            if (n.toLowerCase(Locale.ROOT).contains(wl)) hits.add(n);
            if (hits.size() >= 5) break;
        }
        if (hits.isEmpty()) msg(p, "Try Sneak-Right-Click to list minerals.");
        else msg(p, "Did you mean: " + joinComma(hits));
    }

    private static String joinComma(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    /* -------- IESaveData (reflection; no import needed) -------- */

    private static void ensureIESaveData(World world) {
        try {
            Class<?> sd = Class.forName("blusunrize.immersiveengineering.common.util.IESaveData");
            Object inst = null;
            try {
                Field f = sd.getDeclaredField("INSTANCE");
                f.setAccessible(true);
                inst = f.get(null);
            } catch (Throwable ignored) {}
            if (inst == null) {
                Method getInstance = sd.getDeclaredMethod("getInstance", net.minecraft.world.World.class);
                getInstance.setAccessible(true);
                getInstance.invoke(null, world);
            }
        } catch (Throwable ignored) {}
    }

    /** Write or replace the chunk's MineralWorldInfo and mark dirty. */
    @SuppressWarnings({"rawtypes","unchecked"})
    private static boolean writeChunkMineral(World world, ChunkPos chunk, MineralMix mix) {
        try {
            // Try the public API first
            MineralWorldInfo info = ExcavatorHandler.getMineralWorldInfo(world, chunk.x, chunk.z);
            if (info == null) {
                // Some builds don't create a new entry; build one reflectively
                info = constructInfoReflective(mix);
                if (info == null) return false;
                // Slot into IESaveData cache
                Class<?> sd = Class.forName("blusunrize.immersiveengineering.common.util.IESaveData");
                Field instF = sd.getDeclaredField("INSTANCE");
                instF.setAccessible(true);
                Object inst = instF.get(null);
                if (inst == null) return false;

                Field cacheF = sd.getDeclaredField("mineralCache");
                cacheF.setAccessible(true);
                Object cacheObj = cacheF.get(inst);
                if (!(cacheObj instanceof Map)) return false;
                Map mineralCache = (Map) cacheObj;

                Integer dim = world.provider.getDimension();
                Object dimMapObj = mineralCache.get(dim);
                if (!(dimMapObj instanceof Map)) {
                    dimMapObj = new HashMap();
                    mineralCache.put(dim, dimMapObj);
                }
                Map dimMap = (Map) dimMapObj;
                dimMap.put(chunk, info);
            } else {
                // Update in-place (field name is 'mineral' in 0.12-98)
                info.mineral = mix;
                info.depletion = 0;
            }

            // Persist
            markIESaveDataDirty(world);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    private static MineralWorldInfo constructInfoReflective(MineralMix mix) {
        try {
            Class<?> infoClass = Class.forName("blusunrize.immersiveengineering.api.tool.ExcavatorHandler$MineralWorldInfo");

            // Preferred: ctor(MineralMix,int)
            Constructor<?>[] ctors = infoClass.getDeclaredConstructors();
            for (int i = 0; i < ctors.length; i++) {
                Constructor<?> c = ctors[i];
                Class<?>[] p = c.getParameterTypes();
                if (p.length == 2 &&
                    "blusunrize.immersiveengineering.api.tool.ExcavatorHandler$MineralMix".equals(p[0].getName()) &&
                    p[1] == int.class) {
                    c.setAccessible(true);
                    return (MineralWorldInfo) c.newInstance(mix, 0);
                }
            }

            // Fallback: no-arg then set fields
            MineralWorldInfo info = (MineralWorldInfo) infoClass.newInstance();
            try {
                Field f = infoClass.getDeclaredField("mineral");
                f.setAccessible(true);
                f.set(info, mix);
            } catch (NoSuchFieldException e) {
                // some builds used 'current'
                Field f = infoClass.getDeclaredField("current");
                f.setAccessible(true);
                f.set(info, mix);
            }
            try {
                Field dep = infoClass.getDeclaredField("depletion");
                dep.setAccessible(true);
                dep.setInt(info, 0);
            } catch (NoSuchFieldException ignored) {}
            return info;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    private static void markIESaveDataDirty(World world) {
        try {
            Class<?> sd = Class.forName("blusunrize.immersiveengineering.common.util.IESaveData");
            Field instF = sd.getDeclaredField("INSTANCE");
            instF.setAccessible(true);
            Object inst = instF.get(null);
            if (inst == null) return;
            Method markDirty = sd.getMethod("markDirty");
            markDirty.invoke(inst);
        } catch (Throwable ignored) {}
    }
    
    /** If IE's mineral registry is empty, register a simple Coal mix (API-version tolerant). */
    private static void bootstrapIfEmpty() {
        try {
            // If anything already registered, do nothing.
            if (blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralList != null
                    && !blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralList.isEmpty())
                return;

            // --- reflection setup ---
            Class<?> eh = Class.forName("blusunrize.immersiveengineering.api.tool.ExcavatorHandler");
            Class<?> mixCls = Class.forName("blusunrize.immersiveengineering.api.tool.ExcavatorHandler$MineralMix");

            // Build a MineralMix instance (try several ctor shapes)
            Object mix = null;
            String name = "Coal";
            String[] ores = new String[] { "oreCoal" };
            float[] chances = new float[] { 1.0f };
            float failChance = 0f;
            float depletionMod = 0f;

            // Try: (String, String[], float[], float, float)
            try {
                mix = mixCls.getConstructor(String.class, String[].class, float[].class, float.class, float.class)
                            .newInstance(name, ores, chances, failChance, depletionMod);
            } catch (NoSuchMethodException ignored) {}

            // Try: (String, String[], float[])
            if (mix == null) {
                try {
                    mix = mixCls.getConstructor(String.class, String[].class, float[].class)
                                .newInstance(name, ores, chances);
                } catch (NoSuchMethodException ignored) {}
            }

            // Fallback: no-arg + set fields
            if (mix == null) {
                mix = mixCls.newInstance();
                try { // name
                    java.lang.reflect.Field f = mixCls.getDeclaredField("name");
                    f.setAccessible(true); f.set(mix, name);
                } catch (NoSuchFieldException ignored) {}
                try { // ores (sometimes called "ores" or "ore")
                    java.lang.reflect.Field f;
                    try { f = mixCls.getDeclaredField("ores"); } catch (NoSuchFieldException e) {
                        f = mixCls.getDeclaredField("ore");
                    }
                    f.setAccessible(true); f.set(mix, ores);
                } catch (NoSuchFieldException ignored) {}
                try { // chances (sometimes "chance")
                    java.lang.reflect.Field f;
                    try { f = mixCls.getDeclaredField("chances"); } catch (NoSuchFieldException e) {
                        f = mixCls.getDeclaredField("chance");
                    }
                    f.setAccessible(true); f.set(mix, chances);
                } catch (NoSuchFieldException ignored) {}
                try { // failChance
                    java.lang.reflect.Field f = mixCls.getDeclaredField("failChance");
                    f.setAccessible(true); f.setFloat(mix, failChance);
                } catch (NoSuchFieldException ignored) {}
                try { // depletionModifier
                    java.lang.reflect.Field f = mixCls.getDeclaredField("depletionModifier");
                    f.setAccessible(true); f.setFloat(mix, depletionMod);
                } catch (NoSuchFieldException ignored) {}
            }

            // Register it (try common addMineral overloads)
            boolean added = false;
            for (java.lang.reflect.Method m : eh.getDeclaredMethods()) {
                if (!m.getName().equals("addMineral")) continue;
                Class<?>[] p = m.getParameterTypes();

                // addMineral(MineralMix mix, int weight)
                if (p.length == 2 && p[0] == mixCls && p[1] == int.class) {
                    m.setAccessible(true);
                    m.invoke(null, mix, 20);
                    added = true; break;
                }
                // addMineral(String name, int weight, String[] ores, float[] chances)
                if (p.length == 4 &&
                    p[0] == String.class && p[1] == int.class &&
                    p[2] == String[].class && p[3] == float[].class) {
                    m.setAccessible(true);
                    m.invoke(null, name, 20, ores, chances);
                    added = true; break;
                }
                // addMineral(String name, int weight, float fail, String[] ores, float[] chances)
                if (p.length == 5 &&
                    p[0] == String.class && p[1] == int.class && p[2] == float.class &&
                    p[3] == String[].class && p[4] == float[].class) {
                    m.setAccessible(true);
                    m.invoke(null, name, 20, failChance, ores, chances);
                    added = true; break;
                }
                // addMineral(String name, int weight, float fail, float dep, String[] ores, float[] chances)
                if (p.length == 6 &&
                    p[0] == String.class && p[1] == int.class &&
                    p[2] == float.class && p[3] == float.class &&
                    p[4] == String[].class && p[5] == float[].class) {
                    m.setAccessible(true);
                    m.invoke(null, name, 20, failChance, depletionMod, ores, chances);
                    added = true; break;
                }
            }

            if (!added) {
                // Last-ditch: directly put into mineralList if it's a Map<MineralMix,Integer>
                Object list = eh.getField("mineralList").get(null);
                if (list instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<Object,Integer> map = (java.util.Map<Object,Integer>) list;
                    map.put(mix, Integer.valueOf(20));
                }
            }
        } catch (Throwable ignored) {
            // swallow; bootstrap is best-effort only
        }
    }

    
}
