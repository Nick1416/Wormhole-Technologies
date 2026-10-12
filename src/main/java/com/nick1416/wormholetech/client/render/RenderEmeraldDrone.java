package com.nick1416.wormholetech.client.render;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.entity.EntityEmeraldDrone;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderEmeraldDrone extends RenderLiving<EntityEmeraldDrone> {
    private static final ResourceLocation TEX = new ResourceLocation(WormholeTech.MODID, "textures/entity/emerald_drone.png");

    public RenderEmeraldDrone(RenderManager rm) {
        super(rm, new ModelEmeraldDrone(), 0.45F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityEmeraldDrone e) { return TEX; }
}
