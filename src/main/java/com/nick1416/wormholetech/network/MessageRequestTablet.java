package com.nick1416.wormholetech.network;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.registry.Registration;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client asks the server for an Ancient Tablet recovery (e.g. opened Advancements).
 * Server gives one if the player already unlocked the root advancement and has none.
 */
public class MessageRequestTablet implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<MessageRequestTablet, IMessage> {
        @Override
        public IMessage onMessage(MessageRequestTablet message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    giveIfMissing(player);
                }
            });
            return null;
        }

        private static void giveIfMissing(EntityPlayerMP player) {
            Advancement adv = player.getServerWorld().getAdvancementManager()
                    .getAdvancement(new ResourceLocation(WormholeTech.MODID, "root"));
            if (adv == null) return;
            if (!player.getAdvancements().getProgress(adv).isDone()) return;

            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack s = player.inventory.getStackInSlot(i);
                if (!s.isEmpty() && s.getItem() == Registration.ANCIENT_TABLET) return;
            }

            ItemStack stack = new ItemStack(Registration.ANCIENT_TABLET);
            if (stack.isEmpty()) return;
            if (!player.inventory.addItemStackToInventory(stack.copy())) {
                player.dropItem(stack.copy(), false);
            }
            player.sendStatusMessage(
                    new TextComponentTranslation("message.wormholetech.tablet_restored"), true);
        }
    }
}
