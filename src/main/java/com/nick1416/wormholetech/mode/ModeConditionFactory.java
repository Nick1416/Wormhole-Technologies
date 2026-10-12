package com.nick1416.wormholetech.mode;

import com.google.gson.JsonObject;
import com.nick1416.wormholetech.ModConfig;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

/** JSON condition {@code {"type": "wormholetech:mode", "mode": "cc"}}: true when the configured mode matches. */
public class ModeConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        final String want = JsonUtils.getString(json, "mode");
        return () -> ModConfig.mode.equals(want);
    }
}
