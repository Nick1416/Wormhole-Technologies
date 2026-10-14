package com.nick1416.wormholetech.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/** Floating cube drone: core body with a red eye strip, two side fins and an antenna; bobs gently. */
public class ModelEmeraldWisp extends ModelBase {
    private final ModelRenderer body, finL, finR, antenna;

    public ModelEmeraldWisp() {
        textureWidth = 32;
        textureHeight = 32;
        body = new ModelRenderer(this, 0, 0);
        body.addBox(-3F, -3F, -3F, 6, 6, 6);
        body.setRotationPoint(0F, 19F, 0F);
        finL = new ModelRenderer(this, 0, 12);
        finL.addBox(3F, -2F, -2.5F, 1, 4, 5);
        body.addChild(finL);
        finR = new ModelRenderer(this, 12, 12);
        finR.addBox(-4F, -2F, -2.5F, 1, 4, 5);
        body.addChild(finR);
        antenna = new ModelRenderer(this, 24, 0);
        antenna.addBox(-0.5F, -6F, -0.5F, 1, 3, 1);
        body.addChild(antenna);
    }

    @Override
    public void render(Entity e, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, age, yaw, pitch, scale, e);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float age, float yaw, float pitch, float scale, Entity e) {
        body.rotationPointY = 19F + MathHelper.sin(age * 0.15F) * 1.2F;
        body.rotateAngleY = yaw * 0.017453292F;
        body.rotateAngleX = pitch * 0.017453292F;
        float flap = MathHelper.sin(age * 0.6F) * 0.25F;
        finL.rotateAngleZ = flap;
        finR.rotateAngleZ = -flap;
    }
}
