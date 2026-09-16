package com.example.rfgen.item;

import com.example.rfgen.RFGen;
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
        setRegistryName(RFGen.MODID, "ancient_tablet");
        setUnlocalizedName(RFGen.MODID + ".ancient_tablet");
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            RFGen.PROXY.openAncientTabletGui();
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
