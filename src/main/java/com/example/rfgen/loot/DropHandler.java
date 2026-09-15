package com.example.rfgen.loot;

import com.example.rfgen.RFGen;
import com.example.rfgen.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RFGen.MODID)
public class DropHandler {

    @SubscribeEvent
    public static void onHarvestDrops(HarvestDropsEvent event) {
        IBlockState state = event.getState();

        // --- Unstable Naquadah Block: always 4x Unstable Naquadah when mined by a player ---
        if (state.getBlock() == Registration.UNSTABLE_NAQUADAH_BLOCK) {
            event.getDrops().clear();
            event.setDropChance(1.0f);

            // only when harvested by a player (proper tool handled by setHarvestLevel on the block)
            if (event.getHarvester() != null  && !event.isSilkTouching())  {
                event.getDrops().add(new ItemStack(Registration.UNSTABLE_NAQUADAH, 4));
            }
            return;
        }

        // --- Naquadah Ore: 5% chance to drop 1x Raw Naquadah when mined by a player ---
        if (state.getBlock() == Registration.NAQUADAH_ORE) {
            event.getDrops().clear();
            event.setDropChance(1.0f);

            if (event.getHarvester() != null  && !event.isSilkTouching()) {
                if (event.getWorld().rand.nextFloat() < 0.25f) {
                    event.getDrops().add(new ItemStack(Registration.RAW_NAQUADAH));
                }
            }
            return;
        }
    }
}
