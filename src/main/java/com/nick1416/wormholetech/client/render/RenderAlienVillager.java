package com.nick1416.wormholetech.client.render;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.talvora.EntityAlienVillager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderAlienVillager extends RenderLiving<EntityAlienVillager> {
    private static final ResourceLocation TEX = new ResourceLocation(WormholeTech.MODID, "textures/entity/alien_villager.png");
    public RenderAlienVillager(RenderManager rm) { super(rm, new ModelAlienVillager(), 0.45F); }
    @Override protected ResourceLocation getEntityTexture(EntityAlienVillager e) { return TEX; }
}
