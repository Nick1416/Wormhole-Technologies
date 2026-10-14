package com.nick1416.wormholetech.client.render;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.entity.EntityGiantEmeraldSentinel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderGiantEmeraldSentinel extends RenderLiving<EntityGiantEmeraldSentinel> {
    private static final ResourceLocation TEX = new ResourceLocation(WormholeTech.MODID, "textures/entity/giant_emerald_sentinel.png");

    public RenderGiantEmeraldSentinel(RenderManager rm) { super(rm, new ModelEmeraldDrone(), 1.6F); }

    @Override protected void preRenderCallback(EntityGiantEmeraldSentinel e, float partial) { GlStateManager.scale(2.9F, 2.9F, 2.9F); }
    @Override protected ResourceLocation getEntityTexture(EntityGiantEmeraldSentinel e) { return TEX; }
}
