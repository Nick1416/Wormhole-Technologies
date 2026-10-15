package com.nick1416.wormholetech.client;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.item.HardDiamond;
import com.nick1416.wormholetech.mode.ModeNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/** Client side of Hard Diamond Pickaxe bedrock mining: tracks the held attack key and shows cracks. */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID, value = Side.CLIENT)
public final class HardDiamondClient {
    private HardDiamondClient() {}
    private static final int BREAKER_ID = -1416;
    private static BlockPos pos;
    private static int ticks;

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.player;
        if (p == null || mc.world == null) { pos = null; return; }
        RayTraceResult r = mc.objectMouseOver;
        BlockPos target = null;
        if (mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && HardDiamond.holdsPick(p)
                && r != null && r.typeOfHit == RayTraceResult.Type.BLOCK
                && mc.world.getBlockState(r.getBlockPos()).getBlock() == Blocks.BEDROCK)
            target = r.getBlockPos().toImmutable();
        if (target == null || !target.equals(pos)) {
            if (pos != null) mc.world.sendBlockBreakProgress(BREAKER_ID, pos, -1);
            pos = target; ticks = 0;
            if (target == null) return;
            ModeNetwork.CHANNEL.sendToServer(new HardDiamond.BedrockMessage(false, target));
        }
        ticks++;
        p.swingArm(EnumHand.MAIN_HAND);
        mc.world.sendBlockBreakProgress(BREAKER_ID, pos, Math.min(9, ticks * 10 / HardDiamond.BEDROCK_TICKS));
        if (ticks >= HardDiamond.BEDROCK_TICKS) {
            ModeNetwork.CHANNEL.sendToServer(new HardDiamond.BedrockMessage(true, pos));
            mc.world.sendBlockBreakProgress(BREAKER_ID, pos, -1);
            pos = null; ticks = 0;
        }
    }
}
