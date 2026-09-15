package com.example.rfgen;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.text.TextFormatting;


@Mod.EventBusSubscriber(modid = RFGen.MODID)
public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().isEmpty()) return;

        // Only apply to your mod's items
        if (event.getItemStack().getItem().getRegistryName() != null &&
        		
    		RFGen.MODID.equals(event.getItemStack().getItem().getRegistryName().getResourceDomain())) {
            
            // Add a blue line with the mod name
            event.getToolTip().add(TextFormatting.BLUE + "Wormhole Technologies");
        }
    }
}
