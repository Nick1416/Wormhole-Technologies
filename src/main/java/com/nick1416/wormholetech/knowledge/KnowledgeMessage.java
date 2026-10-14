package com.nick1416.wormholetech.knowledge;

import com.nick1416.wormholetech.WormholeTech;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/** Server to client: the player's knowledge snapshot; opens (or refreshes) the Repository GUI. */
public class KnowledgeMessage implements IMessage {
    public NBTTagCompound data = new NBTTagCompound();
    public KnowledgeMessage() {}
    public KnowledgeMessage(NBTTagCompound d) { data = d; }
    @Override public void fromBytes(ByteBuf b) { data = ByteBufUtils.readTag(b); }
    @Override public void toBytes(ByteBuf b) { ByteBufUtils.writeTag(b, data); }

    public static class Handler implements IMessageHandler<KnowledgeMessage, IMessage> {
        @Override public IMessage onMessage(KnowledgeMessage m, MessageContext ctx) {
            WormholeTech.PROXY.openRepositoryGui(m.data);
            return null;
        }
    }
}
