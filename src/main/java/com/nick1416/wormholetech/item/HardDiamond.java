package com.nick1416.wormholetech.item;

import com.nick1416.wormholetech.ModConfig;
import com.nick1416.wormholetech.WormholeTech;
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

    public static final Item[] ALL = { GEM, PICKAXE, SWORD, AXE, SHOVEL };

    private static Item name(Item i, String id) {
        return i.setRegistryName(WormholeTech.MODID, id).setUnlocalizedName(WormholeTech.MODID + "." + id);
    }

    static { MATERIAL.setRepairItem(new ItemStack(GEM)); }

    /** Called from creative-tab assignment: only normal mode lists these. */
    public static void assignTabs() {
        if (ModConfig.isCC()) return;
        GEM.setCreativeTab(CreativeTabs.MATERIALS);
        PICKAXE.setCreativeTab(CreativeTabs.TOOLS);
        AXE.setCreativeTab(CreativeTabs.TOOLS);
        SHOVEL.setCreativeTab(CreativeTabs.TOOLS);
        SWORD.setCreativeTab(CreativeTabs.COMBAT);
    }

    // ---- bedrock mining (vanilla bedrock is unbreakable, so progress is tracked here) ----

    public static final int BEDROCK_TICKS = 100;

    private static final class Dig { BlockPos pos; int ticks; }
    private static final Map<UUID, Dig> DIGS = new HashMap<UUID, Dig>();

    private static boolean holdsPick(EntityPlayer p) {
        return !ModConfig.isCC() && p.getHeldItemMainhand().getItem() == PICKAXE && !p.isCreative();
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock e) {
        World w = e.getWorld();
        if (w.isRemote || !holdsPick(e.getEntityPlayer())) return;
        if (w.getBlockState(e.getPos()).getBlock() != Blocks.BEDROCK) return;
        Dig d = new Dig();
        d.pos = e.getPos().toImmutable();
        DIGS.put(e.getEntityPlayer().getUniqueID(), d);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.player.world.isRemote) return;
        EntityPlayer p = e.player;
        Dig d = DIGS.get(p.getUniqueID());
        if (d == null) return;
        World w = p.world;
        if (!holdsPick(p) || !p.isSwingInProgress || w.getBlockState(d.pos).getBlock() != Blocks.BEDROCK
                || !lookingAt(p, d.pos)) {
            w.sendBlockBreakProgress(p.getEntityId(), d.pos, -1);
            DIGS.remove(p.getUniqueID());
            return;
        }
        d.ticks++;
        w.sendBlockBreakProgress(p.getEntityId(), d.pos, d.ticks * 10 / BEDROCK_TICKS);
        if (d.ticks >= BEDROCK_TICKS) {
            w.sendBlockBreakProgress(p.getEntityId(), d.pos, -1);
            DIGS.remove(p.getUniqueID());
            w.playEvent(2001, d.pos, net.minecraft.block.Block.getStateId(w.getBlockState(d.pos)));
            w.setBlockToAir(d.pos);
            net.minecraft.block.Block.spawnAsEntity(w, d.pos, new ItemStack(Blocks.BEDROCK));
            p.getHeldItemMainhand().damageItem(1, p);
        }
    }

    private static boolean lookingAt(EntityPlayer p, BlockPos pos) {
        Vec3d eye = p.getPositionEyes(1F);
        Vec3d end = eye.add(p.getLook(1F).scale(6));
        RayTraceResult r = p.world.rayTraceBlocks(eye, end, false, false, true);
        return r != null && r.typeOfHit == RayTraceResult.Type.BLOCK && pos.equals(r.getBlockPos());
    }
}
