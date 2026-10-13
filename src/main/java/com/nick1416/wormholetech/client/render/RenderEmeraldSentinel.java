package com.nick1416.wormholetech.client.render;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.entity.EntityEmeraldSentinel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderEmeraldSentinel extends RenderLiving<EntityEmeraldSentinel> {
    private static final ResourceLocation TEX = new ResourceLocation(WormholeTech.MODID, "textures/entity/emerald_sentinel.png");

    public RenderEmeraldSentinel(RenderManager rm) {
        super(rm, new ModelEmeraldDrone(), 0.9F);
    }

    @Override
    protected void preRenderCallback(EntityEmeraldSentinel e, float partial) {
        GlStateManager.scale(1.75F, 1.75F, 1.75F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEmeraldSentinel e) { return TEX; }
}
