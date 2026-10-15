package com.nick1416.wormholetech.item;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.stargate.ReturnGateData;
import com.nick1416.wormholetech.stargate.StargateTravel;
import com.nick1416.wormholetech.world.ModDimensions;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Compass for gates. On P3X-1416 it points at the return gate; elsewhere at the origin gate the
 * player last left from (if it is in the current dimension), otherwise it spins. The server writes
 * the target into the stack ("Target": Dim + Pos) so the client-side "angle" property can use it.
 */
public class ItemGateLocator extends Item {

    public static final String TAG = "Target";

    public ItemGateLocator() {
        setRegistryName(WormholeTech.MODID, "gate_locator");
        setUnlocalizedName(WormholeTech.MODID + ".gate_locator");
        setCreativeTab(CreativeTabs.TOOLS);
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("angle"), new Angle());
    }

    /** Kind of target: 0 none, 1 return gate (on P3X), 2 origin gate, 3 nearest known gate. */
    public static final class Target {
        public final int kind;
        public final BlockPos pos;
        Target(int kind, BlockPos pos) { this.kind = kind; this.pos = pos; }
    }

    /** Server side: what the locator should point at for this player in this world. */
    public static Target computeTarget(EntityPlayer player, World world) {
        int dim = world.provider.getDimension();
        if (ModDimensions.isGateWorld(dim)) {
            ReturnGateData d = ReturnGateData.get(world);
            if (d.gate != null) return new Target(1, d.gate.interior(d.gate.width / 2, 0));
            return new Target(0, null);
        }
        BlockPos known = com.nick1416.wormholetech.stargate.GateRegistryData.nearest(world, player.posX, player.posZ);
        if (known != null) return new Target(3, known);
        NBTTagCompound data = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (data.hasKey(StargateTravel.ORIGIN_TAG, Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound o = data.getCompoundTag(StargateTravel.ORIGIN_TAG);
            if (o.getInteger("Dim") == dim)
                return new Target(2, BlockPos.fromLong(o.hasKey("Gate") ? o.getLong("Gate") : o.getLong("Pos")));
        }
        return new Target(0, null);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isRemote || !(entity instanceof EntityPlayer) || world.getTotalWorldTime() % 10 != 0) return;
        writeTarget(stack, world, computeTarget((EntityPlayer) entity, world));
    }

    /** Store the target on the stack; only touches NBT when it changed (no re-equip flicker). */
    public static void writeTarget(ItemStack stack, World world, Target t) {
        NBTTagCompound tag = stack.getTagCompound();
        NBTTagCompound cur = tag == null ? null : tag.getCompoundTag(TAG);
        if (t.pos == null) {
            if (tag != null && tag.hasKey(TAG)) {
                tag.removeTag(TAG);
                if (tag.hasNoTags()) stack.setTagCompound(null);
            }
            return;
        }
        int dim = world.provider.getDimension();
        if (cur != null && cur.getInteger("Dim") == dim && cur.getLong("Pos") == t.pos.toLong() && cur.getInteger("Kind") == t.kind) return;
        NBTTagCompound n = new NBTTagCompound();
        n.setInteger("Dim", dim);
        n.setLong("Pos", t.pos.toLong());
        n.setInteger("Kind", t.kind);
        if (tag == null) stack.setTagCompound(tag = new NBTTagCompound());
        tag.setTag(TAG, n);
    }

    /** Target stored on the stack, if it belongs to {@code world}'s dimension. */
    @Nullable
    public static BlockPos storedTarget(ItemStack stack, World world) {
        if (world == null || !stack.hasTagCompound() || !stack.getTagCompound().hasKey(TAG)) return null;
        NBTTagCompound t = stack.getTagCompound().getCompoundTag(TAG);
        if (t.getInteger("Dim") != world.provider.getDimension()) return null;
        return BlockPos.fromLong(t.getLong("Pos"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            Target t = computeTarget(player, world);
            writeTarget(stack, world, t);
            player.sendStatusMessage(describe(t, player.posX, player.posZ), true);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    public static TextComponentTranslation describe(Target t, double x, double z) {
        if (t.pos == null) return new TextComponentTranslation("message.wormholetech.gate_locator.unknown");
        double dx = t.pos.getX() + 0.5 - x, dz = t.pos.getZ() + 0.5 - z;
        int dist = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
        String which = t.kind == 1 ? "message.wormholetech.gate_locator.return" : t.kind == 3 ? "message.wormholetech.gate_locator.nearest" : "message.wormholetech.gate_locator.origin";
        return new TextComponentTranslation(which, dist, direction(dx, dz));
    }

    /** 8-way compass direction from a horizontal offset (north = -Z). */
    public static String direction(double dx, double dz) {
        if (Math.abs(dx) < 0.5 && Math.abs(dz) < 0.5) return "here";
        double deg = Math.toDegrees(Math.atan2(dx, -dz)); // 0 = north, 90 = east
        int i = (int) Math.floor(((deg + 360.0) % 360.0 + 22.5) / 45.0) % 8;
        return new String[] { "N", "NE", "E", "SE", "S", "SW", "W", "NW" }[i];
    }

    /**
     * Same maths as the vanilla compass ({@code ItemCompass}), with the gate as the target:
     * 0 (= 1) means "straight ahead" (texture frame 16).
     */
    public static double angleTo(double yaw, double ex, double ez, BlockPos target) {
        double d1 = mod1(yaw / 360.0D);
        double d2 = Math.atan2(target.getZ() + 0.5 - ez, target.getX() + 0.5 - ex) / (Math.PI * 2D);
        return mod1(0.5D - (d1 - 0.25D - d2));
    }

    /** Positive modulo 1 (MathHelper's double variant is client-only in 1.12). */
    static double mod1(double v) {
        double r = v % 1.0D;
        return r < 0 ? r + 1.0D : r;
    }

    private static final class Angle implements IItemPropertyGetter {
        @SideOnly(Side.CLIENT) private double rotation;
        @SideOnly(Side.CLIENT) private double rota;
        @SideOnly(Side.CLIENT) private long lastUpdateTick;

        @SideOnly(Side.CLIENT)
        @Override
        public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase living) {
            if (living == null && !stack.isOnItemFrame()) return 0.0F;
            boolean held = living != null;
            Entity e = held ? living : stack.getItemFrame();
            if (e == null) return 0.0F;
            if (world == null) world = e.world;
            BlockPos target = storedTarget(stack, world);
            double d0;
            if (target != null) {
                double yaw = held ? e.rotationYaw : frameYaw((EntityItemFrame) e);
                d0 = angleTo(yaw, e.posX, e.posZ, target);
            } else {
                d0 = Math.random();
            }
            if (held) d0 = wobble(world, d0);
            return MathHelper.positiveModulo((float) d0, 1.0F);
        }

        @SideOnly(Side.CLIENT)
        private double wobble(World world, double target) {
            if (world.getTotalWorldTime() != lastUpdateTick) {
                lastUpdateTick = world.getTotalWorldTime();
                double d = target - rotation;
                d = mod1(d + 0.5D) - 0.5D;
                rota += d * 0.1D;
                rota *= 0.8D;
                rotation = mod1(rotation + rota);
            }
            return rotation;
        }

        @SideOnly(Side.CLIENT)
        private static double frameYaw(EntityItemFrame frame) {
            return MathHelper.wrapDegrees(180 + frame.facingDirection.getHorizontalIndex() * 90);
        }
    }
}
