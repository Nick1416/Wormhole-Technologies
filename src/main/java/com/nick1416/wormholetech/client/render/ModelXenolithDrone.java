package com.nick1416.wormholetech.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/** Xenolith survey drone: flat stone disc with a crown, a scanning lens underneath and two side vanes. */
public class ModelXenolithDrone extends ModelBase {
    private final ModelRenderer body, crown, lens, vaneL, vaneR;

    public ModelXenolithDrone() {
        textureWidth = 64;
        textureHeight = 32;
        body = new ModelRenderer(this, 0, 0);
        body.addBox(-5F, -1.5F, -5F, 10, 3, 10);
        body.setRotationPoint(0F, 18F, 0F);
        crown = new ModelRenderer(this, 0, 13);
        crown.addBox(-3F, -3.5F, -3F, 6, 2, 6);
        body.addChild(crown);
        lens = new ModelRenderer(this, 24, 13);
        lens.addBox(-1.5F, 1.5F, -1.5F, 3, 1, 3);
        body.addChild(lens);
        vaneL = new ModelRenderer(this, 40, 0);
        vaneL.addBox(5F, -0.5F, -3F, 2, 1, 6);
        body.addChild(vaneL);
        vaneR = new ModelRenderer(this, 40, 7);
        vaneR.addBox(-7F, -0.5F, -3F, 2, 1, 6);
        body.addChild(vaneR);
    }

    @Override
    public void render(Entity e, float ls, float lsa, float age, float yaw, float pitch, float scale) {
        setRotationAngles(ls, lsa, age, yaw, pitch, scale, e);
        body.render(scale);
    }

    @Override
    public void setRotationAngles(float ls, float lsa, float age, float yaw, float pitch, float scale, Entity e) {
        body.rotationPointY = 18F + MathHelper.sin(age * 0.12F) * 1.0F;
        body.rotateAngleY = yaw * 0.017453292F;
        float tilt = MathHelper.sin(age * 0.5F) * 0.12F;
        vaneL.rotateAngleX = tilt;
        vaneR.rotateAngleX = -tilt;
    }
}
