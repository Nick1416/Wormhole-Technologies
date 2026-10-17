package com.nick1416.wormholetech.loot;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = WormholeTech.MODID)
public class DropHandler {
    /** Synthetic ore positions caught in the explosion being processed right now (server thread only). */
    private static final java.util.Set<net.minecraft.util.math.BlockPos> EXPLODED = new java.util.HashSet<>();

    @SubscribeEvent
    public static void onDetonate(net.minecraftforge.event.world.ExplosionEvent.Detonate e) {
        if (e.getWorld().isRemote) return;
        EXPLODED.clear();
        for (net.minecraft.util.math.BlockPos p : e.getAffectedBlocks())
            if (e.getWorld().getBlockState(p).getBlock() == Registration.NAQUADAH_ORE) EXPLODED.add(p.toImmutable());
    }

    @SubscribeEvent
    public static void onTick(net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent e) {
        if (e.phase == net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END) EXPLODED.clear();
    }

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

        // --- Synthetic Naquadah Ore: always drops itself when mined; blown up, 75% chance of Refined Naquadah ---
        if (state.getBlock() == Registration.NAQUADAH_ORE) {
            event.getDrops().clear();
            event.setDropChance(1.0f);
            if (EXPLODED.remove(event.getPos().toImmutable())) {
                if (event.getWorld().rand.nextFloat() < 0.75f) event.getDrops().add(new ItemStack(Registration.REFINED_NAQUADAH));
            } else {
                event.getDrops().add(new ItemStack(Registration.NAQUADAH_ORE));
            }
            return;
        }
    }
}
