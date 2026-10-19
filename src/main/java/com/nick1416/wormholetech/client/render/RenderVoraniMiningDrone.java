package com.nick1416.wormholetech.client.render;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.moon.EntityVoraniMiningDrone;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderVoraniMiningDrone extends RenderLiving<EntityVoraniMiningDrone> {
    private static final ResourceLocation TEX = new ResourceLocation(WormholeTech.MODID, "textures/entity/vorani_mining_drone.png");
    private static final ResourceLocation GLOW = new ResourceLocation(WormholeTech.MODID, "textures/entity/vorani_mining_drone_glow.png");

    public RenderVoraniMiningDrone(RenderManager rm) {
        super(rm, new ModelVoraniMiningDrone(), 0.4F);
        addLayer(new LayerRenderer<EntityVoraniMiningDrone>() {
            @Override
            public void doRenderLayer(EntityVoraniMiningDrone e, float ls, float lsa, float pt, float age, float yaw, float pitch, float scale) {
                bindTexture(GLOW);
                GlStateManager.enableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                GlStateManager.depthMask(!e.isInvisible());
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680F, 0F);
                float b = e.beam() == 0 ? 0.55F : 1.0F;
                GlStateManager.color(b, b, b, 1F);
                getMainModel().render(e, ls, lsa, age, yaw, pitch, scale);
                int i = e.getBrightnessForRender();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i % 65536, i / 65536);
                GlStateManager.color(1F, 1F, 1F, 1F);
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
            }
            @Override public boolean shouldCombineTextures() { return false; }
        });
    }

    @Override protected ResourceLocation getEntityTexture(EntityVoraniMiningDrone e) { return TEX; }
}
