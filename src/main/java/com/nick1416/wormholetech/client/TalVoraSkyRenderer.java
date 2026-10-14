package com.nick1416.wormholetech.client;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

/** Tal'Vora sky: flat orange dome colour and two suns, a large amber one and a smaller red companion. */
public class TalVoraSkyRenderer extends IRenderHandler {
    private static final ResourceLocation SUN_A = new ResourceLocation(WormholeTech.MODID, "textures/environment/tal_vora_sun_a.png");
    private static final ResourceLocation SUN_B = new ResourceLocation(WormholeTech.MODID, "textures/environment/tal_vora_sun_b.png");

    @Override
    public void render(float pt, WorldClient world, Minecraft mc) {
        Vec3d sky = world.getSkyColor(mc.getRenderViewEntity(), pt);
        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableFog();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        // sky dome (a box around the camera) in the sky colour
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        float r = (float) sky.x, g = (float) sky.y, bl = (float) sky.z, d = 100F;
        b.pos(-d, d, -d).color(r, g, bl, 1F).endVertex(); b.pos(-d, d, d).color(r, g, bl, 1F).endVertex();
        b.pos(d, d, d).color(r, g, bl, 1F).endVertex(); b.pos(d, d, -d).color(r, g, bl, 1F).endVertex();
        t.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        float angle = world.getCelestialAngle(pt) * 360.0F;
        float alpha = 1.0F - world.getRainStrength(pt);
        sun(t, b, mc, SUN_A, -90F, angle, 34F, alpha);
        sun(t, b, mc, SUN_B, -68F, angle + 22F, 16F, alpha);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.depthMask(true);
    }

    private static void sun(Tessellator t, BufferBuilder b, Minecraft mc, ResourceLocation tex, float yaw, float angle, float s, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1F, 1F, 1F, alpha);
        GlStateManager.rotate(yaw, 0F, 1F, 0F);
        GlStateManager.rotate(angle, 1F, 0F, 0F);
        mc.getTextureManager().bindTexture(tex);
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        b.pos(-s, 100D, -s).tex(0D, 0D).endVertex();
        b.pos(s, 100D, -s).tex(1D, 0D).endVertex();
        b.pos(s, 100D, s).tex(1D, 1D).endVertex();
        b.pos(-s, 100D, s).tex(0D, 1D).endVertex();
        t.draw();
        GlStateManager.popMatrix();
    }
}
