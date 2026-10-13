package com.nick1416.wormholetech.world;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.mode.ModeNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * P3X-1416's own weather: no rain, only dust storms. The state lives in the dimension's
 * per-world storage, so it does not follow overworld rain. Clients get the state by packet.
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public class P3XWeather extends WorldSavedData {
    private static final String NAME = WormholeTech.MODID + "_dust_storm";

    public boolean storm;
    public int timer = -1; // ticks until the state flips; -1 = not rolled yet

    /** Client-side copy of the storm state of the dimension the player is in. */
    public static volatile boolean clientStorm;

    public P3XWeather(String name) { super(name); }

    public static boolean isP3X(World w) {
        return w != null && ModDimensions.isRegistered() && w.provider.getDimension() == ModDimensions.id();
    }

    public static P3XWeather get(World w) {
        P3XWeather d = (P3XWeather) w.getPerWorldStorage().getOrLoadData(P3XWeather.class, NAME);
        if (d == null) { d = new P3XWeather(NAME); w.getPerWorldStorage().setData(NAME, d); }
        return d;
    }

    /** Server: is a dust storm raging in this world? */
    public static boolean isStorm(World w) {
        if (!isP3X(w) || !ModConfig.p3xDustStorms) return false;
        if (w.isRemote) return clientStorm;
        return get(w).storm;
    }

    private static int roll(World w, int avg) { return avg / 2 + w.rand.nextInt(Math.max(1, avg)); }

    /** Force a state (also used by tests/commands). */
    public void set(World w, boolean on, int ticks) {
        storm = on;
        timer = ticks;
        markDirty();
        ModeNetwork.CHANNEL.sendToDimension(new StormMessage(on), w.provider.getDimension());
        WormholeTech.LOGGER.debug("P3X dust storm {} for {} ticks", on ? "started" : "ended", ticks);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.world.isRemote || !isP3X(e.world)) return;
        P3XWeather d = get(e.world);
        if (!ModConfig.p3xDustStorms) {
            if (d.storm) d.set(e.world, false, -1);
            return;
        }
        if (d.timer < 0) { d.timer = roll(e.world, ModConfig.p3xDustStormClearTicks); d.markDirty(); }
        if (--d.timer <= 0) {
            boolean on = !d.storm;
            d.set(e.world, on, roll(e.world, on ? ModConfig.p3xDustStormDurationTicks : ModConfig.p3xDustStormClearTicks));
        } else if (d.timer % 200 == 0) d.markDirty();
    }

    private static void sync(EntityPlayer p) {
        if (p instanceof EntityPlayerMP) ModeNetwork.CHANNEL.sendTo(new StormMessage(isStorm(p.world)), (EntityPlayerMP) p);
    }
    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) { sync(e.player); }
    @SubscribeEvent public static void onDim(PlayerEvent.PlayerChangedDimensionEvent e) { sync(e.player); }
    @SubscribeEvent public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) { sync(e.player); }

    @Override
    public void readFromNBT(NBTTagCompound nbt) { storm = nbt.getBoolean("Storm"); timer = nbt.getInteger("Timer"); }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt.setBoolean("Storm", storm); nbt.setInteger("Timer", timer); return nbt; }

    public static class StormMessage implements IMessage {
        public boolean storm;
        public StormMessage() {}
        public StormMessage(boolean storm) { this.storm = storm; }
        @Override public void fromBytes(ByteBuf buf) { storm = buf.readBoolean(); }
        @Override public void toBytes(ByteBuf buf) { buf.writeBoolean(storm); }
    }

    public static class StormHandler implements IMessageHandler<StormMessage, IMessage> {
        @Override public IMessage onMessage(StormMessage msg, MessageContext ctx) { clientStorm = msg.storm; return null; }
    }
}
