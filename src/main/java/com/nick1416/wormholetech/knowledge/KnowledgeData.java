package com.nick1416.wormholetech.knowledge;

import com.nick1416.wormholetech.ModConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Per-player knowledge, saved with the world (overworld map storage), so it is independent of any
 * Repository block. Per player: "Planets" {key: progress ticks} for found planets, "Tech" {key: 1}.
 */
public class KnowledgeData extends WorldSavedData {
    public static final String NAME = "wormholetech_knowledge";
    private final Map<UUID, NBTTagCompound> players = new HashMap<>();

    public KnowledgeData() { super(NAME); }
    public KnowledgeData(String n) { super(n); }

    public static KnowledgeData get(World w) {
        MapStorage s = w.getMinecraftServer() != null ? w.getMinecraftServer().getWorld(0).getMapStorage() : w.getMapStorage();
        KnowledgeData d = (KnowledgeData) s.getOrLoadData(KnowledgeData.class, NAME);
        if (d == null) { d = new KnowledgeData(); s.setData(NAME, d); }
        return d;
    }

    public static int fullTicks() { return Math.max(20, ModConfig.repositoryResearchTicks); }

    private NBTTagCompound of(UUID id) { return players.computeIfAbsent(id, k -> new NBTTagCompound()); }
    private NBTTagCompound sub(UUID id, String key) {
        NBTTagCompound p = of(id);
        if (!p.hasKey(key)) p.setTag(key, new NBTTagCompound());
        return p.getCompoundTag(key);
    }

    /** Mark a planet found; returns true if it was new. */
    public boolean findPlanet(UUID id, int planet) {
        NBTTagCompound pl = sub(id, "Planets");
        if (pl.hasKey(Planets.KEYS[planet])) return false;
        pl.setInteger(Planets.KEYS[planet], 0);
        markDirty();
        return true;
    }

    /** Add research ticks to a planet (finding it if needed). */
    public void addProgress(UUID id, int planet, int ticks) {
        findPlanet(id, planet);
        NBTTagCompound pl = sub(id, "Planets");
        String k = Planets.KEYS[planet];
        int v = pl.getInteger(k);
        if (v >= fullTicks()) return;
        pl.setInteger(k, Math.min(fullTicks(), v + ticks));
        markDirty();
    }

    /** Research percent (0..100) of a planet, 0 if not found. */
    public int percent(UUID id, int planet) {
        NBTTagCompound pl = sub(id, "Planets");
        String k = Planets.KEYS[planet];
        return pl.hasKey(k) ? (int) Math.floor(100.0 * pl.getInteger(k) / fullTicks()) : 0;
    }

    public boolean discover(UUID id, String tech) {
        NBTTagCompound t = sub(id, "Tech");
        if (t.hasKey(tech)) return false;
        t.setBoolean(tech, true);
        markDirty();
        return true;
    }

    /** Snapshot for the client GUI: Planets {key: percent}, Tech {key}. */
    public NBTTagCompound clientView(UUID id) {
        NBTTagCompound out = new NBTTagCompound(), pl = new NBTTagCompound();
        NBTTagCompound src = sub(id, "Planets");
        for (String k : src.getKeySet()) pl.setInteger(k, (int) Math.floor(100.0 * src.getInteger(k) / fullTicks()));
        out.setTag("Planets", pl);
        out.setTag("Tech", sub(id, "Tech").copy());
        return out;
    }

    @Override
    public void readFromNBT(NBTTagCompound c) {
        players.clear();
        for (String k : c.getKeySet()) {
            try { players.put(UUID.fromString(k), c.getCompoundTag(k)); } catch (IllegalArgumentException ignored) {}
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound c) {
        for (Map.Entry<UUID, NBTTagCompound> e : players.entrySet()) c.setTag(e.getKey().toString(), e.getValue());
        return c;
    }
}
