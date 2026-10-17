package com.nick1416.wormholetech.jei;

import com.nick1416.wormholetech.WormholeTech;
import com.nick1416.wormholetech.pressurizer.PressurizerRecipe;
import com.nick1416.wormholetech.registry.Registration;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PressurizerCategory implements IRecipeCategory<PressurizerCategory.Wrapper> {
    public static final String UID = WormholeTech.MODID + ".hydrothermal_pressurizer";
    private static final int[][] POS = {{0, 0}, {0, 22}, {26, 11}, {44, 11}, {62, 11}, {120, 11}};
    private final IDrawable background, icon, slot, arrow;

    public PressurizerCategory(IGuiHelper g) {
        background = g.createBlankDrawable(142, 48);
        icon = g.createDrawableIngredient(new ItemStack(Registration.PRESSURIZER));
        slot = g.getSlotDrawable();
        arrow = g.createDrawable(new ResourceLocation("minecraft", "textures/gui/container/furnace.png"), 79, 35, 24, 17);
    }

    @Override public void drawExtras(Minecraft mc) {
        for (int[] p : POS) slot.draw(mc, p[0], p[1]);
        arrow.draw(mc, 88, 12);
    }

    @Override public String getUid() { return UID; }
    @Override public String getTitle() { return I18n.format("tile.wormholetech.hydrothermal_pressurizer.name"); }
    @Override public String getModName() { return "Wormhole Technologies"; }
    @Override public IDrawable getBackground() { return background; }
    @Override public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayout layout, Wrapper w, IIngredients ing) {
        IGuiItemStackGroup s = layout.getItemStacks();
        for (int i = 0; i < 6; i++) s.init(i, i < 5, POS[i][0], POS[i][1]);
        List<List<ItemStack>> in = ing.getInputs(VanillaTypes.ITEM);
        for (int i = 0; i < 5; i++) s.set(i, in.get(i));
        s.set(5, ing.getOutputs(VanillaTypes.ITEM).get(0));
    }

    public static List<Wrapper> recipes() { return Collections.singletonList(new Wrapper()); }

    private static List<ItemStack> withCount(List<ItemStack> l, int n) {
        for (ItemStack s : l) s.setCount(n);
        return l;
    }

    public static class Wrapper implements IRecipeWrapper {
        @Override public void getIngredients(IIngredients ing) {
            ing.setInputLists(VanillaTypes.ITEM, Arrays.asList(
                    Collections.singletonList(new ItemStack(Items.WATER_BUCKET)),
                    withCount(PressurizerRecipe.stacks(PressurizerRecipe.COAL), PressurizerRecipe.COAL_PER_EMERALD),
                    PressurizerRecipe.stacks(PressurizerRecipe.ALUMINUM),
                    PressurizerRecipe.stacks(PressurizerRecipe.CHROMIUM),
                    PressurizerRecipe.stacks(PressurizerRecipe.SILICON)));
            ing.setOutput(VanillaTypes.ITEM, new ItemStack(Items.EMERALD));
        }
        @Override public void drawInfo(Minecraft mc, int w, int h, int mx, int my) {
            mc.fontRenderer.drawString(I18n.format("jei.wormholetech.pressurizer.hint"), 26, 38, 0x444444);
        }
    }
}
