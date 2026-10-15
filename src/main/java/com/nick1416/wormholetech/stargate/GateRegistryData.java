package com.nick1416.wormholetech.stargate;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Per-dimension list of known titanium gates (centre of the bottom row), for the Gate Locator. */
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public class GateRegistryData extends WorldSavedData {

    /** A finished frame registers as soon as its last titanium block is placed. */
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent
    public static void onPlace(net.minecraftforge.event.world.BlockEvent.PlaceEvent e) {
        World w = e.getWorld();
        if (w.isRemote || !TitaniumFrame.isTitanium(e.getPlacedBlock())) return;
        register(w, GateShape.findFromFrameBlock(w, e.getPos(), TitaniumFrame::isTitanium));
    }

    public static final String NAME = WormholeTech.MODID + "_gates";
    public final List<GateShape> gates = new ArrayList<GateShape>();

    public GateRegistryData() { this(NAME); }
    public GateRegistryData(String name) { super(name); }

    public static GateRegistryData get(World world) {
        GateRegistryData d = (GateRegistryData) world.getPerWorldStorage().getOrLoadData(GateRegistryData.class, NAME);
        if (d == null) { d = new GateRegistryData(); world.getPerWorldStorage().setData(NAME, d); }
        return d;
    }

    public static BlockPos centre(GateShape g) { return g.interior(g.width / 2, 0); }

    /** Remember a gate (server side); replaces any entry with the same centre. */
    public static void register(World world, @Nullable GateShape g) {
        if (g == null || world.isRemote) return;
        GateRegistryData d = get(world);
        BlockPos c = centre(g);
        for (GateShape o : d.gates) if (centre(o).equals(c)) return;
        d.gates.add(g);
        d.markDirty();
    }

    /** Nearest known gate; entries whose chunk is loaded but whose frame is gone are dropped. */
    @Nullable
    public static BlockPos nearest(World world, double x, double z) {
        GateRegistryData d = get(world);
        BlockPos best = null;
        double bd = Double.MAX_VALUE;
        for (Iterator<GateShape> it = d.gates.iterator(); it.hasNext();) {
            GateShape g = it.next();
            BlockPos c = centre(g);
            if (world.isBlockLoaded(c) && GateShape.detect(world, c, g.axis, TitaniumFrame::isTitanium) == null) {
                it.remove(); d.markDirty(); continue;
            }
            double dx = c.getX() + 0.5 - x, dz = c.getZ() + 0.5 - z, dd = dx * dx + dz * dz;
            if (dd < bd) { bd = dd; best = c; }
        }
        return best;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        gates.clear();
        NBTTagList l = tag.getTagList("Gates", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < l.tagCount(); i++) { GateShape g = GateShape.read(l.getCompoundTagAt(i)); if (g != null) gates.add(g); }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList l = new NBTTagList();
        for (GateShape g : gates) l.appendTag(g.write());
        tag.setTag("Gates", l);
        return tag;
    }
}
