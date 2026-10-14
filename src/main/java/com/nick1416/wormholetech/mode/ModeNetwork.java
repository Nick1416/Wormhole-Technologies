package com.nick1416.wormholetech.mode;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Mode handshake: on login the server sends its mode; a client whose own mode differs answers
 * with its mode and the server disconnects it with an explanation.
 */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class ModeNetwork {
    private ModeNetwork() {}

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(WormholeTech.MODID);

    public static void register() {
        CHANNEL.registerMessage(SyncHandler.class, ModeMessage.class, 0, Side.CLIENT);
        CHANNEL.registerMessage(MismatchHandler.class, ModeMessage.class, 1, Side.SERVER);
        CHANNEL.registerMessage(com.nick1416.wormholetech.world.P3XWeather.StormHandler.class,
                com.nick1416.wormholetech.world.P3XWeather.StormMessage.class, 2, Side.CLIENT);
        CHANNEL.registerMessage(com.nick1416.wormholetech.knowledge.KnowledgeMessage.Handler.class,
                com.nick1416.wormholetech.knowledge.KnowledgeMessage.class, 3, Side.CLIENT);
    }

    public static String kickText(String serverMode, String clientMode) {
        return "Wormhole Technologies mode mismatch: this server uses mode=" + serverMode
                + " but your client uses mode=" + clientMode + ".\n"
                + "Set mode=" + serverMode + " in config/wormholetech.cfg and restart Minecraft.";
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.player instanceof EntityPlayerMP) CHANNEL.sendTo(new ModeMessage(ModConfig.mode), (EntityPlayerMP) e.player);
    }

    public static class ModeMessage implements IMessage {
        public String mode = "";
        public ModeMessage() {}
        public ModeMessage(String mode) { this.mode = mode; }
        @Override public void fromBytes(ByteBuf buf) { mode = ByteBufUtils.readUTF8String(buf); }
        @Override public void toBytes(ByteBuf buf) { ByteBufUtils.writeUTF8String(buf, mode); }
    }

    /** Client: compare the server's mode with ours; report a mismatch. */
    public static class SyncHandler implements IMessageHandler<ModeMessage, IMessage> {
        @Override
        public IMessage onMessage(ModeMessage msg, MessageContext ctx) {
            if (ModConfig.mode.equals(msg.mode)) return null;
            WormholeTech.LOGGER.error(kickText(msg.mode, ModConfig.mode));
            return new ModeMessage(ModConfig.mode);
        }
    }

    /** Server: a client reported a different mode, disconnect it. */
    public static class MismatchHandler implements IMessageHandler<ModeMessage, IMessage> {
        @Override
        public IMessage onMessage(final ModeMessage msg, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            if (ModConfig.mode.equals(msg.mode)) return null;
            player.getServerWorld().addScheduledTask(() -> kick(player, msg.mode));
            return null;
        }
    }

    public static void kick(EntityPlayerMP player, String clientMode) {
        WormholeTech.LOGGER.warn("Disconnecting {}: client mode={} but server mode={}", player.getName(), clientMode, ModConfig.mode);
        player.connection.disconnect(new TextComponentString(kickText(ModConfig.mode, clientMode)));
    }
}
