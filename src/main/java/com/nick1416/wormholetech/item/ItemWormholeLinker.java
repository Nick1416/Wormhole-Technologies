package com.nick1416.wormholetech.item;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.tile.WormholePadTileEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

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

        int here = world.provider.getDimension();
        String hereName = WormholePadTileEntity.dimName(here);

        if (!hasStoredLink(nbt)) {
            nbt.setInteger(KEY_X, pos.getX());
            nbt.setInteger(KEY_Y, pos.getY());
            nbt.setInteger(KEY_Z, pos.getZ());
            nbt.setInteger(KEY_DIM, here);
            player.sendStatusMessage(new TextComponentTranslation(
                    "message.wormholetech.linker.stored", pos.getX(), pos.getY(), pos.getZ(), hereName), true);
            return EnumActionResult.SUCCESS;
        }

        int dim = nbt.getInteger(KEY_DIM);
        String dimName = WormholePadTileEntity.dimName(dim);
        BlockPos first = new BlockPos(nbt.getInteger(KEY_X), nbt.getInteger(KEY_Y), nbt.getInteger(KEY_Z));

        // Same position AND same dimension = the stored pad itself. This also fires when the use
        // repeats while right-click is held, so say so instead of reporting an error.
        if (dim == here && first.equals(pos)) {
            player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.already_stored",
                    pos.getX(), pos.getY(), pos.getZ(), hereName), true);
            return EnumActionResult.SUCCESS;
        }

        boolean cross = dim != here;
        // Was the stored pad's area loaded (ticking) before we touch it? Decides whether its
        // computer must be active or only needs Aetherius (see hasActiveComputer).
        World loadedA = dim == here ? world : DimensionManager.getWorld(dim);
        boolean firstWasLoaded = WormholePadTileEntity.isAreaLoaded(loadedA, first);
        World worldA = WormholePadTileEntity.getWorldForDim(world, dim);
        WormholePadTileEntity padA = WormholePadTileEntity.getPadLoading(worldA, first);
        if (padA == null) {
            clearStored(stack);
            player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.missing_first",
                    first.getX(), first.getY(), first.getZ(), dimName), true);
            return EnumActionResult.FAIL;
        }

        if (cross) {
            // Each pad's computer is checked in that pad's own world.
            if (!WormholePadTileEntity.hasActiveComputer(world, pos)) {
                player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.need_computer_here"), true);
                return EnumActionResult.FAIL;
            }
            if (!WormholePadTileEntity.hasActiveComputer(worldA, first, firstWasLoaded)) {
                player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.need_computer_remote",
                        first.getX(), first.getY(), first.getZ(), dimName), true);
                return EnumActionResult.FAIL;
            }
        }

        boolean ok = WormholePadTileEntity.linkPads(padA, pad);
        if (ok) {
            clearStored(stack);
            if (cross) {
                player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.linked_cross",
                        dimName, first.getX(), first.getY(), first.getZ(),
                        hereName, pos.getX(), pos.getY(), pos.getZ()), true);
            } else {
                player.sendStatusMessage(new TextComponentTranslation("message.wormholetech.linker.linked",
                        first.getX(), first.getY(), first.getZ(),
                        pos.getX(), pos.getY(), pos.getZ(), hereName), true);
            }
            return EnumActionResult.SUCCESS;
        }

        player.sendStatusMessage(new TextComponentTranslation(
                cross ? "message.wormholetech.linker.need_computers" : "message.wormholetech.linker.failed"), true);
        return EnumActionResult.FAIL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (hasStoredLink(nbt)) {
            tooltip.add(I18n.format("tooltip.wormholetech.wormhole_linker.stored",
                    nbt.getInteger(KEY_X), nbt.getInteger(KEY_Y), nbt.getInteger(KEY_Z),
                    WormholePadTileEntity.dimName(nbt.getInteger(KEY_DIM))));
        }
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
