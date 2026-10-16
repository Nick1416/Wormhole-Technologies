package com.nick1416.wormholetech.elyndra;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** A translucent, glowing robed figure that bobs gently. */
@SideOnly(Side.CLIENT)
public class RenderAncientSpirit extends RenderLiving<EntityAncientSpirit> {
    private static final ResourceLocation TEX = new ResourceLocation(WormholeTech.MODID, "textures/entity/ancient_spirit.png");

    public RenderAncientSpirit(RenderManager rm) { super(rm, new ModelBiped(0.0F, 0.0F, 64, 32), 0.0F); }

    @Override
    protected void preRenderCallback(EntityAncientSpirit e, float pt) {
        GlStateManager.translate(0, Math.sin((e.ticksExisted + pt) * 0.08) * 0.08, 0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(0.85F, 0.95F, 1.0F, 0.55F);
        GlStateManager.depthMask(false);
    }

    @Override
    public void doRender(EntityAncientSpirit e, double x, double y, double z, float yaw, float pt) {
        super.doRender(e, x, y, z, yaw, pt);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Override protected ResourceLocation getEntityTexture(EntityAncientSpirit e) { return TEX; }
}
