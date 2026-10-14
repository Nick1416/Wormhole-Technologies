package com.nick1416.wormholetech.client.render;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.entity.EntityEmeraldWisp;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderEmeraldWisp extends RenderLiving<EntityEmeraldWisp> {
    private static final ResourceLocation TEX = new ResourceLocation(WormholeTech.MODID, "textures/entity/emerald_wisp.png");
    private static final ResourceLocation EYES = new ResourceLocation(WormholeTech.MODID, "textures/entity/emerald_wisp_eyes.png");

    public RenderEmeraldWisp(RenderManager rm) {
        super(rm, new ModelEmeraldWisp(), 0.3F);
        addLayer(new LayerRenderer<EntityEmeraldWisp>() {
            @Override
            public void doRenderLayer(EntityEmeraldWisp e, float ls, float lsa, float pt, float age, float yaw, float pitch, float scale) {
                bindTexture(EYES);
                GlStateManager.enableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                GlStateManager.depthMask(!e.isInvisible());
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680F, 0F);
                GlStateManager.color(1F, 1F, 1F, 1F);
                getMainModel().render(e, ls, lsa, age, yaw, pitch, scale);
                int i = e.getBrightnessForRender();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i % 65536, i / 65536);
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();
            }
            @Override public boolean shouldCombineTextures() { return false; }
        });
    }

    @Override protected ResourceLocation getEntityTexture(EntityEmeraldWisp e) { return TEX; }
}
