package com.nick1416.wormholetech.item;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.tile.WormholePadTileEntity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemWormholeLinker extends Item {

    private static final String KEY_X = "LinkX";
    private static final String KEY_Y = "LinkY";
    private static final String KEY_Z = "LinkZ";
    private static final String KEY_DIM = "LinkDim";

    public ItemWormholeLinker() {
        setRegistryName(WormholeTech.MODID, "wormhole_linker");
        setUnlocalizedName(WormholeTech.MODID + ".wormhole_linker");
        setCreativeTab(CreativeTabs.TOOLS);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            if (!world.isRemote && stack.hasTagCompound() && hasStoredLink(stack.getTagCompound())) {
                clearStored(stack);
                player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.cleared"), true);
            } else if (!world.isRemote) {
                clearStored(stack);
                player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.empty"), true);
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);

        // Sneak + use clears stored link even when aiming at a pad
        if (player.isSneaking()) {
            if (!world.isRemote) {
                boolean had = stack.hasTagCompound() && hasStoredLink(stack.getTagCompound());
                clearStored(stack);
                player.sendStatusMessage(new TextComponentTranslation(
                        had ? "message.wormholetech.linker.cleared" : "message.wormholetech.linker.empty"), true);
            }
            return EnumActionResult.SUCCESS;
        }

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof WormholePadTileEntity)) {
            return EnumActionResult.PASS;
        }

        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }

        WormholePadTileEntity pad = (WormholePadTileEntity) te;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        if (!hasStoredLink(nbt)) {
            nbt.setInteger(KEY_X, pos.getX());
            nbt.setInteger(KEY_Y, pos.getY());
            nbt.setInteger(KEY_Z, pos.getZ());
            nbt.setInteger(KEY_DIM, world.provider.getDimension());
            player.sendStatusMessage(new TextComponentTranslation(
                    "message.wormholetech.linker.stored", pos.getX(), pos.getY(), pos.getZ()), true);
            return EnumActionResult.SUCCESS;
        }

        int dim = nbt.getInteger(KEY_DIM);
        BlockPos first = new BlockPos(nbt.getInteger(KEY_X), nbt.getInteger(KEY_Y), nbt.getInteger(KEY_Z));

        if (dim == world.provider.getDimension() && first.equals(pos)) {
            player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.same_pad"), true);
            return EnumActionResult.FAIL;
        }

        World worldA = world;
        if (dim != world.provider.getDimension()) {
            if (world.getMinecraftServer() == null) return EnumActionResult.FAIL;
            worldA = world.getMinecraftServer().getWorld(dim);
            if (worldA == null) {
                player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.missing_first"), true);
                return EnumActionResult.FAIL;
            }
        }

        TileEntity teA = worldA.getTileEntity(first);
        if (!(teA instanceof WormholePadTileEntity)) {
            clearStored(stack);
            player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.missing_first"), true);
            return EnumActionResult.FAIL;
        }

        WormholePadTileEntity padA = (WormholePadTileEntity) teA;
        boolean cross = dim != world.provider.getDimension();
        if (cross) {
            if (!WormholePadTileEntity.hasActiveComputer(worldA, first)
                    || !WormholePadTileEntity.hasActiveComputer(world, pos)) {
                player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.need_computers"), true);
                return EnumActionResult.FAIL;
            }
        }

        boolean ok = WormholePadTileEntity.linkPads(padA, pad);
        clearStored(stack);
        if (ok) {
            player.sendStatusMessage(new TextComponentTranslation(
                    cross ? "message.wormholetech.linker.linked_cross" : "message.wormholetech.linker.linked",
                    first.getX(), first.getY(), first.getZ(),
                    pos.getX(), pos.getY(), pos.getZ()), true);
            return EnumActionResult.SUCCESS;
        }

        player.sendStatusMessage(new TextComponentTranslation(
                cross ? "message.wormholetech.linker.need_computers" : "message.wormholetech.linker.failed"), true);
        return EnumActionResult.FAIL;
    }

    private static boolean hasStoredLink(NBTTagCompound nbt) {
        return nbt != null && nbt.hasKey(KEY_X) && nbt.hasKey(KEY_Y) && nbt.hasKey(KEY_Z) && nbt.hasKey(KEY_DIM);
    }

    private static void clearStored(ItemStack stack) {
        if (!stack.hasTagCompound()) return;
        NBTTagCompound nbt = stack.getTagCompound();
        nbt.removeTag(KEY_X);
        nbt.removeTag(KEY_Y);
        nbt.removeTag(KEY_Z);
        nbt.removeTag(KEY_DIM);
        if (nbt.hasNoTags()) {
            stack.setTagCompound(null);
        }
    }
}
