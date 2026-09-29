package com.nick1416.wormholetech.compat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nick1416.wormholetech.WormholeTech;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Soft integration with Compact Machines 3 (1.12.2).
 *
 * CM3 only reads miniaturization recipes from its own jar and from
 * {@code config/compactmachines3/recipes/}. To ship recipes inside this jar we parse our
 * bundled JSON with CM3's own deserializer and append the result to CM3's recipe list after
 * CM3 has loaded its recipes (our postInit runs after every mod's init). CM3's JEI plugin
 * reads that same list at load-complete, so the recipes show in its miniaturization category.
 *
 * <p>Power-state variants: CM3 matches blocks by identity, and some redstone parts change
 * block id with power (unpowered_repeater / powered_repeater). A bundled recipe may declare
 * {@code "wormholetech:state-variants": {"r": ["minecraft:unpowered_repeater", "minecraft:powered_repeater"]}}.
 * Every occurrence of that key in the shape then gets its own reference, and one hidden recipe
 * is registered per state combination (the all-first-state combination is the visible recipe
 * itself). Hidden variants are removed from JEI in {@code WormholeJeiPlugin#onRuntimeAvailable},
 * so JEI shows a single entry. Rotations are handled by CM3 (non-symmetrical recipes are tried
 * in all four horizontal orientations).
 *
 * <p>Reflection is used so there is no compile-time dependency on CM3; if CM3 is absent or its
 * internals differ, this logs a warning and does nothing. A config-folder recipe with the same
 * "name" takes precedence (CM3 rejects duplicate names) and suppresses the variants too.
 */
public final class CompactMachinesCompat {

    public static final String MODID = "compactmachines3";
    public static final String JEI_CATEGORY = "compactmachines3.MultiblockMiniaturization";

    private static final Logger LOG = LogManager.getLogger(WormholeTech.MODID);
    private static final String RECIPE_DIR = "/assets/" + WormholeTech.MODID + "/compactmachines3/recipes/";
    private static final String[] RECIPES = { "quantum_circuit.json" };
    private static final String VARIANTS_KEY = "wormholetech:state-variants";

    /** CM3 recipe objects registered only to accept alternate power states; hidden from JEI. */
    private static final List<Object> HIDDEN_VARIANTS = new ArrayList<Object>();

    private CompactMachinesCompat() {}

    public static List<Object> getHiddenVariants() {
        return Collections.unmodifiableList(HIDDEN_VARIANTS);
    }

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
                JsonObject root;
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    root = new JsonParser().parse(reader).getAsJsonObject();
                }

                Object recipe = gson.fromJson(root, recipeClass);
                if (recipe == null) {
                    LOG.info("Compact Machines recipe {} skipped (overridden by config or disabled)", file);
                    continue;
                }
                recipes.add(recipe);

                int variants = 0;
                for (JsonObject variant : buildStateVariants(root)) {
                    Object v = gson.fromJson(variant, recipeClass);
                    if (v != null) {
                        recipes.add(v);
                        HIDDEN_VARIANTS.add(v);
                        variants++;
                    }
                }
                LOG.info("Registered Compact Machines miniaturization recipe {} (+{} power-state variants)", file, variants);
            }
        } catch (Throwable t) {
            LOG.warn("Could not register Compact Machines miniaturization recipes", t);
        }
    }

    /** One recipe JSON per non-default combination of the declared block states. */
    static List<JsonObject> buildStateVariants(JsonObject root) {
        List<JsonObject> out = new ArrayList<JsonObject>();
        if (!root.has(VARIANTS_KEY)) {
            return out;
        }
        JsonObject spec = root.getAsJsonObject(VARIANTS_KEY);
        JsonArray shape = root.getAsJsonArray("shape");

        // every shape cell whose key has declared states: {layer, row, col}
        List<int[]> cells = new ArrayList<int[]>();
        List<String> cellKeys = new ArrayList<String>();
        for (int y = 0; y < shape.size(); y++) {
            JsonArray layer = shape.get(y).getAsJsonArray();
            for (int z = 0; z < layer.size(); z++) {
                JsonArray row = layer.get(z).getAsJsonArray();
                for (int x = 0; x < row.size(); x++) {
                    String key = row.get(x).getAsString();
                    if (spec.has(key)) {
                        cells.add(new int[] { y, z, x });
                        cellKeys.add(key);
                    }
                }
            }
        }
        if (cells.isEmpty()) {
            return out;
        }

        int[] radix = new int[cells.size()];
        long combos = 1;
        for (int i = 0; i < cells.size(); i++) {
            radix[i] = spec.getAsJsonArray(cellKeys.get(i)).size();
            combos *= radix[i];
        }
        if (combos > 256) {
            LOG.warn("Too many state variants ({}) for {}; skipping variants", combos, root.get("name"));
            return out;
        }

        String baseName = root.get("name").getAsString();
        JsonParser parser = new JsonParser();
        for (int combo = 1; combo < combos; combo++) {
            JsonObject copy = parser.parse(root.toString()).getAsJsonObject();
            copy.remove(VARIANTS_KEY);
            copy.addProperty("name", baseName + "_state" + combo);
            JsonObject types = copy.getAsJsonObject("input-types");
            JsonArray copyShape = copy.getAsJsonArray("shape");

            int rest = combo;
            for (int i = 0; i < cells.size(); i++) {
                int state = rest % radix[i];
                rest /= radix[i];
                String key = cellKeys.get(i);
                String newKey = key + "#" + i; // must not contain ':' (CM3 treats that as an NBT variant ref)

                JsonObject type = parser.parse(types.getAsJsonObject(key).toString()).getAsJsonObject();
                type.addProperty("id", spec.getAsJsonArray(key).get(state).getAsString());
                if (type.has("item") && type.get("item").isJsonObject()) {
                    type.getAsJsonObject("item").addProperty("count", 1);
                }
                types.add(newKey, type);

                int[] c = cells.get(i);
                copyShape.get(c[0]).getAsJsonArray().get(c[1]).getAsJsonArray().set(c[2], new com.google.gson.JsonPrimitive(newKey));
            }
            // drop references no longer used by the shape
            for (Map.Entry<String, JsonElement> e : new ArrayList<Map.Entry<String, JsonElement>>(types.entrySet())) {
                if (spec.has(e.getKey())) {
                    types.remove(e.getKey());
                }
            }
            out.add(copy);
        }
        return out;
    }
}
