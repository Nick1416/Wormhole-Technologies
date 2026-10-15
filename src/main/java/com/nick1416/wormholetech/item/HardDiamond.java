package com.nick1416.wormholetech.item;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Hard Diamond (normal mode): refined diamond, its tools, and the pickaxe's bedrock mining. */
@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public final class HardDiamond {
    private HardDiamond() {}

    /** Sword: 4 + 4 = 8 attack damage. Harvest level 4 (above diamond). */
    public static final Item.ToolMaterial MATERIAL =
            EnumHelper.addToolMaterial("WORMHOLETECH_HARD_DIAMOND", 4, 3122, 10.0F, 4.0F, 12);

    public static final Item GEM = new Item()
            .setRegistryName(WormholeTech.MODID, "hard_diamond")
            .setUnlocalizedName(WormholeTech.MODID + ".hard_diamond");
    public static final Item PICKAXE = name(new ItemPickaxe(MATERIAL) {}, "hard_diamond_pickaxe");
    public static final Item SWORD = name(new ItemSword(MATERIAL), "hard_diamond_sword");
    public static final Item AXE = name(new ItemAxe(MATERIAL, 9.0F, -3.0F) {}, "hard_diamond_axe");
    public static final Item SHOVEL = name(new ItemSpade(MATERIAL), "hard_diamond_shovel");

    /** Armor: diamond x 8/7 -> 3/9/7/3 (head/chest/legs/feet), toughness 2.3, durability x38, enchantability 10. */
    public static final ItemArmor.ArmorMaterial ARMOR = EnumHelper.addArmorMaterial("WORMHOLETECH_HARD_DIAMOND",
            WormholeTech.MODID + ":hard_diamond", 38, new int[] { 3, 7, 9, 3 }, 10,
            net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.3F);
    public static final Item HELMET = name(new ItemArmor(ARMOR, 0, net.minecraft.inventory.EntityEquipmentSlot.HEAD), "hard_diamond_helmet");
    public static final Item CHESTPLATE = name(new ItemArmor(ARMOR, 1, net.minecraft.inventory.EntityEquipmentSlot.CHEST), "hard_diamond_chestplate");
    public static final Item LEGGINGS = name(new ItemArmor(ARMOR, 2, net.minecraft.inventory.EntityEquipmentSlot.LEGS), "hard_diamond_leggings");
    public static final Item BOOTS = name(new ItemArmor(ARMOR, 1, net.minecraft.inventory.EntityEquipmentSlot.FEET), "hard_diamond_boots");

    public static final Item[] ALL = { GEM, PICKAXE, SWORD, AXE, SHOVEL, HELMET, CHESTPLATE, LEGGINGS, BOOTS };

    private static Item name(Item i, String id) {
        return i.setRegistryName(WormholeTech.MODID, id).setUnlocalizedName(WormholeTech.MODID + "." + id);
    }

    static { MATERIAL.setRepairItem(new ItemStack(GEM)); ARMOR.setRepairItem(new ItemStack(GEM)); }

    /** Called from creative-tab assignment: only normal mode lists these. */
    public static void assignTabs() {
        if (ModConfig.isCC()) return;
        GEM.setCreativeTab(CreativeTabs.MATERIALS);
        PICKAXE.setCreativeTab(CreativeTabs.TOOLS);
        AXE.setCreativeTab(CreativeTabs.TOOLS);
        SHOVEL.setCreativeTab(CreativeTabs.TOOLS);
        SWORD.setCreativeTab(CreativeTabs.COMBAT);
        for (Item a : new Item[] { HELMET, CHESTPLATE, LEGGINGS, BOOTS }) a.setCreativeTab(CreativeTabs.COMBAT);
    }

    // ---- bedrock mining ----
    // Vanilla never lets a hardness -1 block be dug (the client stops, the server ignores it), so the
    // client tracks the hold itself (HardDiamondClient) and sends a START and a FINISH packet; the
    // server validates and breaks the block.

    public static final int BEDROCK_TICKS = 100;

    private static final class Dig { BlockPos pos; long start; }
    private static final Map<UUID, Dig> DIGS = new HashMap<UUID, Dig>();

    public static boolean holdsPick(EntityPlayer p) {
        return !ModConfig.isCC() && p.getHeldItemMainhand().getItem() == PICKAXE && !p.isSpectator();
    }

    public static class BedrockMessage implements IMessage {
        public boolean finish; public BlockPos pos = BlockPos.ORIGIN;
        public BedrockMessage() {}
        public BedrockMessage(boolean finish, BlockPos pos) { this.finish = finish; this.pos = pos; }
        @Override public void fromBytes(ByteBuf b) { finish = b.readBoolean(); pos = BlockPos.fromLong(b.readLong()); }
        @Override public void toBytes(ByteBuf b) { b.writeBoolean(finish); b.writeLong(pos.toLong()); }
    }

    public static class BedrockHandler implements IMessageHandler<BedrockMessage, IMessage> {
        @Override public IMessage onMessage(final BedrockMessage m, MessageContext ctx) {
            final EntityPlayerMP p = ctx.getServerHandler().player;
            p.getServerWorld().addScheduledTask(new Runnable() { public void run() { handle(p, m); } });
            return null;
        }
    }

    private static void handle(EntityPlayerMP p, BedrockMessage m) {
        World w = p.world;
        BlockPos pos = m.pos;
        if (!holdsPick(p) || !w.isBlockLoaded(pos) || w.getBlockState(pos).getBlock() != Blocks.BEDROCK
                || p.getDistanceSqToCenter(pos) > 7.5 * 7.5) { DIGS.remove(p.getUniqueID()); return; }
        if (!m.finish) {
            Dig d = new Dig(); d.pos = pos.toImmutable(); d.start = w.getTotalWorldTime();
            DIGS.put(p.getUniqueID(), d);
            return;
        }
        Dig d = DIGS.remove(p.getUniqueID());
        if (d == null || !d.pos.equals(pos) || w.getTotalWorldTime() - d.start < BEDROCK_TICKS - 10) return;
        w.playEvent(2001, pos, net.minecraft.block.Block.getStateId(w.getBlockState(pos)));
        w.setBlockToAir(pos);
        if (!p.isCreative()) {
            net.minecraft.block.Block.spawnAsEntity(w, pos, new ItemStack(Blocks.BEDROCK));
            p.getHeldItemMainhand().damageItem(1, p);
        }
    }
}
