package com.nick1416.wormholetech.drone;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Active companion drone (meta 0 Defensive, 1 Research). Use on a block to release it; it is bound to you. */
public class ItemCompanionDrone extends Item {
    public ItemCompanionDrone() { setHasSubtypes(true); setMaxDamage(0); setMaxStackSize(1); }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) { items.add(new ItemStack(this, 1, 0)); items.add(new ItemStack(this, 1, 1)); }
    }

    @Override public String getUnlocalizedName(ItemStack s) { return "item.wormholetech." + (s.getMetadata() == 1 ? "research_drone" : "defensive_drone"); }


    @Override public boolean showDurabilityBar(ItemStack s) { return DroneCharge.of(s) < DroneCharge.max(); }
    @Override public double getDurabilityForDisplay(ItemStack s) { return 1.0 - DroneCharge.of(s) / (double) DroneCharge.max(); }
    @Override public int getRGBDurabilityForDisplay(ItemStack s) { return 0x46C8BE; }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World w, BlockPos pos, EnumHand hand, EnumFacing side, float hx, float hy, float hz) {
        ItemStack s = player.getHeldItem(hand);
        if (w.isRemote) return EnumActionResult.SUCCESS;
        BlockPos p = pos.offset(side);
        EntityCompanionDrone d = new EntityCompanionDrone(w);
        d.setMode(s.getMetadata() == 1 ? EntityCompanionDrone.RESEARCH : EntityCompanionDrone.DEFENSIVE);
        d.setOwnerId(player.getUniqueID());
        d.charge = DroneCharge.of(s);
        d.setLocationAndAngles(p.getX() + 0.5, p.getY() + 0.8, p.getZ() + 0.5, player.rotationYaw, 0F);
        if (s.hasDisplayName()) d.setCustomNameTag(s.getDisplayName());
        w.spawnEntity(d);
        if (!player.capabilities.isCreativeMode) s.shrink(1);
        return EnumActionResult.SUCCESS;
    }
}
