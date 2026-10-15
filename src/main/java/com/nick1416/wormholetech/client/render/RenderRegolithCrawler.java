package com.nick1416.wormholetech.client.render;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.moon.EntityRegolithCrawler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;

/** Spider body, pale regolith colours, a bit larger. */
public class RenderRegolithCrawler extends RenderSpider<EntitySpider> {
    private static final ResourceLocation TEX = new ResourceLocation(WormholeTech.MODID, "textures/entity/regolith_crawler.png");
    public RenderRegolithCrawler(RenderManager rm) { super(rm); shadowSize *= 1.15F; }
    @Override protected void preRenderCallback(EntitySpider e, float pt) { GlStateManager.scale(1.15F, 1.15F, 1.15F); }
    @Override protected ResourceLocation getEntityTexture(EntitySpider e) { return TEX; }
}
