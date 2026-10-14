package com.nick1416.wormholetech.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/** Tall, slender native: elongated domed head with a crest, thin torso, long limbs (about 2.3 blocks). Texture 64x64. */
public class ModelAlienVillager extends ModelBase {
    private final ModelRenderer head, crest, body, armL, armR, legL, legR;

    public ModelAlienVillager() {
        textureWidth = 64; textureHeight = 64;
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-3.5F, -10F, -3.5F, 7, 10, 7);
        head.setRotationPoint(0F, -4F, 0F);
        crest = new ModelRenderer(this, 28, 0);
        crest.addBox(-1F, -12F, -1F, 2, 4, 6);
        head.addChild(crest);
        body = new ModelRenderer(this, 0, 18);
        body.addBox(-3.5F, 0F, -2F, 7, 12, 4);
        body.setRotationPoint(0F, -4F, 0F);
        armR = new ModelRenderer(this, 24, 18);
        armR.addBox(-2F, -1F, -1F, 2, 15, 2);
        armR.setRotationPoint(-3.5F, -3F, 0F);
        armL = new ModelRenderer(this, 24, 18);
        armL.mirror = true;
        armL.addBox(0F, -1F, -1F, 2, 15, 2);
        armL.setRotationPoint(3.5F, -3F, 0F);
        legR = new ModelRenderer(this, 34, 18);
        legR.addBox(-1.5F, 0F, -1.5F, 3, 16, 3);
        legR.setRotationPoint(-1.8F, 8F, 0F);
        legL = new ModelRenderer(this, 34, 18);
        legL.mirror = true;
        legL.addBox(-1.5F, 0F, -1.5F, 3, 16, 3);
        legL.setRotationPoint(1.8F, 8F, 0F);
    }

    @Override
    public void render(Entity e, float limbSwing, float amount, float age, float yaw, float pitch, float scale) {
        setRotationAngles(limbSwing, amount, age, yaw, pitch, scale, e);
        boolean sit = meditating(e);
        net.minecraft.client.renderer.GlStateManager.pushMatrix();
        if (sit) net.minecraft.client.renderer.GlStateManager.translate(0F, 14F * scale, 0F);
        head.render(scale); body.render(scale); armL.render(scale); armR.render(scale); legL.render(scale); legR.render(scale);
        net.minecraft.client.renderer.GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float amount, float age, float yaw, float pitch, float scale, Entity e) {
        head.rotateAngleY = yaw * 0.017453292F;
        head.rotateAngleX = pitch * 0.017453292F;
        float sw = MathHelper.cos(limbSwing * 0.5F) * 0.9F * amount;
        legR.rotateAngleX = sw; legL.rotateAngleX = -sw;
        armR.rotateAngleX = -sw * 0.6F; armL.rotateAngleX = sw * 0.6F;
        armR.rotateAngleZ = 0.06F + MathHelper.sin(age * 0.05F) * 0.03F; armL.rotateAngleZ = -armR.rotateAngleZ;
        if (meditating(e)) {
            // cross-legged: thighs forward and splayed, hands resting on the knees, head slightly bowed
            legR.rotateAngleX = legL.rotateAngleX = -1.45F;
            legR.rotateAngleY = 0.35F; legL.rotateAngleY = -0.35F;
            armR.rotateAngleX = armL.rotateAngleX = -0.75F;
            armR.rotateAngleZ = 0.12F; armL.rotateAngleZ = -0.12F;
            head.rotateAngleX = 0.25F + MathHelper.sin(age * 0.04F) * 0.04F;
        } else {
            legR.rotateAngleY = legL.rotateAngleY = 0F;
        }
    }

    private static boolean meditating(Entity e) {
        return e instanceof com.nick1416.wormholetech.talvora.EntityAlienVillager && ((com.nick1416.wormholetech.talvora.EntityAlienVillager) e).isMeditating();
    }
}
