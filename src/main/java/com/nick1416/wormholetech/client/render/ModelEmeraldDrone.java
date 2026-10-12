package com.nick1416.wormholetech.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/** Low four-legged drone: flat hull, sensor head with an emerald eye, spindly legs. 64x32 texture. */
public class ModelEmeraldDrone extends ModelBase {
    private final ModelRenderer body, head, eye;
    private final ModelRenderer[] legs = new ModelRenderer[4];

    public ModelEmeraldDrone() {
        textureWidth = 64;
        textureHeight = 32;
        body = new ModelRenderer(this, 0, 0);
        body.addBox(-5F, -2F, -6F, 10, 4, 12);
        body.setRotationPoint(0F, 16F, 0F);
        head = new ModelRenderer(this, 0, 16);
        head.addBox(-3F, -2F, -4F, 6, 4, 4);
        head.setRotationPoint(0F, 15F, -6F);
        eye = new ModelRenderer(this, 20, 16);
        eye.addBox(-1F, -1F, -4.6F, 2, 2, 1);
        head.addChild(eye);
        for (int i = 0; i < 4; i++) {
            ModelRenderer l = new ModelRenderer(this, 44, 0);
            l.addBox(-1F, 0F, -1F, 2, 8, 2);
            l.setRotationPoint(i % 2 == 0 ? -5F : 5F, 16F, i < 2 ? -4F : 4F);
            legs[i] = l;
        }
    }

    @Override
    public void render(Entity e, float limbSwing, float limbAmount, float age, float yaw, float pitch, float scale) {
        setRotationAngles(limbSwing, limbAmount, age, yaw, pitch, scale, e);
        body.render(scale);
        head.render(scale);
        for (ModelRenderer l : legs) l.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbAmount, float age, float yaw, float pitch, float scale, Entity e) {
        head.rotateAngleY = yaw * 0.017453292F;
        head.rotateAngleX = pitch * 0.017453292F;
        for (int i = 0; i < 4; i++) {
            float side = i % 2 == 0 ? 1F : -1F;
            float phase = (i == 0 || i == 3) ? 0F : (float) Math.PI;
            legs[i].rotateAngleZ = side * 0.55F;
            legs[i].rotateAngleX = MathHelper.cos(limbSwing * 1.3F + phase) * 1.0F * limbAmount;
        }
        body.rotateAngleZ = MathHelper.sin(age * 0.15F) * 0.02F;
    }
}
