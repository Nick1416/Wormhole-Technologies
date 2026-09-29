package com.nick1416.wormholetech.compat;

import com.google.gson.Gson;
import com.nick1416.wormholetech.WormholeTech;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Soft integration with Compact Machines 3 (1.12.2).
 *
 * CM3 only reads miniaturization recipes from its own jar and from
 * {@code config/compactmachines3/recipes/}. To ship recipes inside this jar we parse our
 * bundled JSON with CM3's own deserializer and append the result to CM3's recipe list after
 * CM3 has loaded its recipes (our postInit runs after every mod's init). CM3's JEI plugin
 * reads that same list at load-complete, so the recipes show in its miniaturization category.
 *
 * Reflection is used so there is no compile-time dependency on CM3; if CM3 is absent or its
 * internals differ, this logs a warning and does nothing.
 *
 * A config-folder recipe with the same "name" takes precedence (CM3 rejects duplicate names).
 */
public final class CompactMachinesCompat {

    public static final String MODID = "compactmachines3";

    private static final Logger LOG = LogManager.getLogger(WormholeTech.MODID);
    private static final String RECIPE_DIR = "/assets/" + WormholeTech.MODID + "/compactmachines3/recipes/";
    private static final String[] RECIPES = { "quantum_circuit.json" };

    private CompactMachinesCompat() {}

    @SuppressWarnings("unchecked")
    public static void registerMiniaturizationRecipes() {
        if (!Loader.isModLoaded(MODID)) {
            return;
        }
        try {
            Class<?> helper = Class.forName("org.dave.compactmachines3.utility.SerializationHelper");
            Gson gson = (Gson) helper.getField("GSON").get(null);
            Class<?> recipeClass = Class.forName("org.dave.compactmachines3.miniaturization.MultiblockRecipe");
            Class<?> recipesClass = Class.forName("org.dave.compactmachines3.miniaturization.MultiblockRecipes");
            Method getRecipes = recipesClass.getMethod("getRecipes");
            List<Object> recipes = (List<Object>) getRecipes.invoke(null);

            for (String file : RECIPES) {
                InputStream in = CompactMachinesCompat.class.getResourceAsStream(RECIPE_DIR + file);
                if (in == null) {
                    LOG.warn("Missing bundled Compact Machines recipe {}", file);
                    continue;
                }
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    Object recipe = gson.fromJson(reader, recipeClass);
                    if (recipe == null) {
                        LOG.info("Compact Machines recipe {} skipped (overridden by config or disabled)", file);
                        continue;
                    }
                    recipes.add(recipe);
                    LOG.info("Registered Compact Machines miniaturization recipe {}", file);
                }
            }
        } catch (Throwable t) {
            LOG.warn("Could not register Compact Machines miniaturization recipes", t);
        }
    }
}
