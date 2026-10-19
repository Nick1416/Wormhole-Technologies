package com.nick1416.wormholetech.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

/** Vorani mining drone: robed purple hull with a teal crown, two side vanes and a spinning cutter head below. */
public class ModelVoraniMiningDrone extends ModelBase {
    private final ModelRenderer body, crown, head, bit, vaneL, vaneR;

    public ModelVoraniMiningDrone() {
        textureWidth = 64;
        textureHeight = 32;
        body = new ModelRenderer(this, 0, 0);
        body.addBox(-5F, -1.5F, -5F, 10, 3, 10);
        body.setRotationPoint(0F, 17F, 0F);
        crown = new ModelRenderer(this, 0, 13);
        crown.addBox(-3F, -3.5F, -3F, 6, 2, 6);
        body.addChild(crown);
        head = new ModelRenderer(this, 24, 13);
        head.addBox(-2F, 1.5F, -2F, 4, 2, 4);
        body.addChild(head);
        bit = new ModelRenderer(this, 40, 14);
        bit.addBox(-1F, 0F, -1F, 2, 3, 2);
        bit.setRotationPoint(0F, 3.5F, 0F);
        body.addChild(bit);
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
        body.rotationPointY = 17F + MathHelper.sin(age * 0.1F) * 0.8F;
        body.rotateAngleY = yaw * 0.017453292F;
        boolean cutting = e instanceof com.nick1416.wormholetech.moon.EntityVoraniMiningDrone
                && ((com.nick1416.wormholetech.moon.EntityVoraniMiningDrone) e).beam() == 1;
        bit.rotateAngleY = age * (cutting ? 1.2F : 0.15F);
        float tilt = MathHelper.sin(age * 0.4F) * 0.1F;
        vaneL.rotateAngleX = tilt;
        vaneR.rotateAngleX = -tilt;
    }
}
