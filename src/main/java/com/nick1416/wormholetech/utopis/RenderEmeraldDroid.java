package com.nick1416.wormholetech.utopis;

import com.nick1416.wormholetech.WormholeTech;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEmeraldDroid extends RenderBiped<EntityEmeraldDroid> {
    private static final ResourceLocation TEX = new ResourceLocation(WormholeTech.MODID, "textures/entity/emerald_droid.png");
    public RenderEmeraldDroid(RenderManager rm) { super(rm, new ModelBiped(0.0F, 0.0F, 64, 32), 0.5F); }
    @Override protected ResourceLocation getEntityTexture(EntityEmeraldDroid e) { return TEX; }
}
