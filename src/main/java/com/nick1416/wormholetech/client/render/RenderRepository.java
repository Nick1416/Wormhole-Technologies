package com.nick1416.wormholetech.client.render;

import com.nick1416.wormholetech.knowledge.RepositoryTileEntity;
import com.nick1416.wormholetech.registry.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

/** Floating, glowing Raw Xenolith crystal above the Repository: slowly rotating and bobbing, fullbright. */
public class RenderRepository extends TileEntitySpecialRenderer<RepositoryTileEntity> {
    private ItemStack crystal;

    @Override
    public void render(RepositoryTileEntity te, double x, double y, double z, float pt, int destroy, float alpha) {
        if (crystal == null) crystal = new ItemStack(Registration.RAW_XENOLITH);
        float t = (te.getWorld().getTotalWorldTime() % 3600L) + pt;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1.15 + Math.sin(t * 0.06) * 0.08, z + 0.5);
        GlStateManager.rotate(t * 2.0F, 0, 1, 0);
        GlStateManager.scale(0.7F, 0.7F, 0.7F);
        float bx = OpenGlHelper.lastBrightnessX, by = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
        GlStateManager.disableLighting();
        Minecraft.getMinecraft().getRenderItem().renderItem(crystal, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.enableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, bx, by);
        GlStateManager.popMatrix();
    }
}
