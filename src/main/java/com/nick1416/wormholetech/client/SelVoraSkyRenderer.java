package com.nick1416.wormholetech.client;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/** Sel'Vora sky: black, dense with stars, and Tal'Vora hanging huge and red above the horizon. */
public class SelVoraSkyRenderer extends IRenderHandler {
    private static final ResourceLocation PLANET = new ResourceLocation(WormholeTech.MODID, "textures/environment/tal_vora_planet.png");
    private static final int STARS = 2200;
    private float[] stars;

    private void buildStars() {
        Random r = new Random(14190L);
        stars = new float[STARS * 4];
        for (int i = 0; i < STARS; i++) {
            double x = r.nextFloat() * 2 - 1, y = r.nextFloat() * 2 - 1, z = r.nextFloat() * 2 - 1;
            double l = Math.sqrt(x * x + y * y + z * z);
            if (l < 0.01 || l > 1) { i--; continue; }
            stars[i * 4] = (float) (x / l * 100); stars[i * 4 + 1] = (float) (y / l * 100); stars[i * 4 + 2] = (float) (z / l * 100);
            stars[i * 4 + 3] = 0.15F + r.nextFloat() * 0.25F;
        }
    }

    @Override
    public void render(float pt, WorldClient world, Minecraft mc) {
        if (stars == null) buildStars();
        Tessellator t = Tessellator.getInstance();
        BufferBuilder b = t.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableFog();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        // stars: small camera-facing-ish quads (axis-aligned in the plane perpendicular to the radius)
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < STARS; i++) {
            float x = stars[i * 4], y = stars[i * 4 + 1], z = stars[i * 4 + 2], s = stars[i * 4 + 3];
            float br = 0.6F + (i % 7) * 0.06F;
            // two tangent vectors
            double ux, uy, uz;
            if (Math.abs(y) < 90) { ux = -z; uy = 0; uz = x; } else { ux = 1; uy = 0; uz = 0; }
            double ul = Math.sqrt(ux * ux + uy * uy + uz * uz); ux *= s / ul; uy *= s / ul; uz *= s / ul;
            double vx = y * uz - z * uy, vy = z * ux - x * uz, vz = x * uy - y * ux;
            double vl = Math.sqrt(vx * vx + vy * vy + vz * vz); vx *= s / vl; vy *= s / vl; vz *= s / vl;
            b.pos(x - ux - vx, y - uy - vy, z - uz - vz).color(br, br, br, 1F).endVertex();
            b.pos(x + ux - vx, y + uy - vy, z + uz - vz).color(br, br, br, 1F).endVertex();
            b.pos(x + ux + vx, y + uy + vy, z + uz + vz).color(br, br, br, 1F).endVertex();
            b.pos(x - ux + vx, y - uy + vy, z - uz + vz).color(br, br, br, 1F).endVertex();
        }
        t.draw();
        // Tal'Vora: fixed in the sky, ~35 degrees above the southern horizon
        GlStateManager.enableTexture2D();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.pushMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.rotate(-20F, 0F, 1F, 0F);
        GlStateManager.rotate(55F, 1F, 0F, 0F);
        mc.getTextureManager().bindTexture(PLANET);
        float s = 38F;
        b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        b.pos(-s, 100D, -s).tex(0D, 0D).endVertex();
        b.pos(s, 100D, -s).tex(1D, 0D).endVertex();
        b.pos(s, 100D, s).tex(1D, 1D).endVertex();
        b.pos(-s, 100D, s).tex(0D, 1D).endVertex();
        t.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableFog();
        GlStateManager.depthMask(true);
    }
}
