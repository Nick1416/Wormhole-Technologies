package com.example.rfgen.loot;

import com.example.rfgen.RFGen;
import com.example.rfgen.registry.Registration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RFGen.MODID)
public class ExplosionConvertHandler {

    @SubscribeEvent
    public static void onExplode(ExplosionEvent.Detonate e) {
        if (e.getWorld().isRemote) return;

        // Iterate over entities affected by this explosion
        for (Entity ent : e.getAffectedEntities()) {
            if (!(ent instanceof EntityItem)) continue;

            EntityItem itemEnt = (EntityItem) ent;
            ItemStack stack = itemEnt.getItem();
            if (stack.isEmpty()) continue;

            // Convert Raw -> Refined on explosion
            if (stack.getItem() == Registration.RAW_NAQUADAH) {
                int count = stack.getCount();

                // Remove the raw item entity
                itemEnt.setDead();

                // Spawn refined items at the same spot (1:1)
                ItemStack out = new ItemStack(Registration.REFINED_NAQUADAH, count);
                EntityItem refinedEnt = new EntityItem(e.getWorld(), itemEnt.posX, itemEnt.posY, itemEnt.posZ, out);
                refinedEnt.setDefaultPickupDelay(); // optional: short pickup delay
                e.getWorld().spawnEntity(refinedEnt);
            }
        }
    }
}
