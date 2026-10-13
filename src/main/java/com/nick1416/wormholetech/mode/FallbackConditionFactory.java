package com.nick1416.wormholetech.mode;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

/** {@code {"type": "wormholetech:fallback", "fallback": "nugget" | "frame"}}: true when that fallback is needed. */
public class FallbackConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        String which = JsonUtils.getString(json, "fallback");
        if ("nugget".equals(which)) return Fallbacks::nugget;
        if ("frame".equals(which)) return Fallbacks::frame;
        throw new com.google.gson.JsonSyntaxException("Unknown fallback '" + which + "' (nugget, frame)");
    }
}
