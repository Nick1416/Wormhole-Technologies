package com.nick1416.wormholetech.item;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemAncientTablet extends Item {

    public ItemAncientTablet() {
        setRegistryName(WormholeTech.MODID, "ancient_tablet");
        setUnlocalizedName(WormholeTech.MODID + ".ancient_tablet");
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            WormholeTech.PROXY.openAncientTabletGui();
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
