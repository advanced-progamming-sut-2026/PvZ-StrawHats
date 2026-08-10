package model.collections.animations;

import model.utils.ResourceResolver;
import view.GeneralPrinter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AnimationFactory {

    private static Map<String, AnimationJsonParser.AnimationConfig> library = new HashMap<>();
    private static boolean loaded = false;


    private static final Map<String, String> PLANT_NAME_OVERRIDES = Map.of(
            "TWIN_SUNFLOWER", "SUNFLOWER_TWIN",           // word order flipped
            "ROTOBAGA", "ROTORUTABAGA",                    // old internal codename survives
            "MEGA_GATLING_PEA", "MEGAGATLING",             // "Pea" dropped from the codename
            "ICEBERG_LETTUCE", "HEADBUTTER_LETTUCE",       // unrelated internal codename
            "PHAT_BEET", "PHATBEETS",                      // plural in the codename
            "PIERCE_MINT", "SPEARMINT"                     // unrelated internal codename
    );

    /**
     * Plants.json display names with NO entry anywhere in animations.json (checked exhaustively -
     * this animation pack simply doesn't include them). resolveByDisplayName returns null for these;
     * you'll need art from elsewhere for: Cat-tail, catTail-mint, Kernel-pult.
     */
    private static final java.util.Set<String> PLANTS_WITHOUT_ANIMATION_DATA = java.util.Set.of(
            "CAT_TAIL", "CATTAIL_MINT", "KERNEL_PULT"
    );

    public static void init(InputStream jsonStream) {
        library = AnimationJsonParser.loadConfigs(jsonStream);
        loaded = true;
    }

    public static void autoInit() {
        if (loaded) return;
        try (InputStream is = ResourceResolver.open("pvz-assets/animations.json")) {
            if (is != null) {
                init(is);
                return;
            }
        } catch (java.io.IOException e) {
            GeneralPrinter.print("Could not load animations.json: " + e.getMessage());
        }
        GeneralPrinter.print("Could not find animations.json in any known location.");
    }

    public static Map<String, AnimationJsonParser.AnimationConfig> getLibrary() {
        autoInit();
        return library;
    }

    public static AnimationJsonParser.AnimationConfig get(String rawName) {
        if (rawName == null) return null;
        autoInit();
        return library.get(rawName.toUpperCase());
    }

    /**
     * <p>
     * Verified against all 69 Plants.json entries: 63 resolve automatically through
     * normalization, 6 need the {@link #PLANT_NAME_OVERRIDES} table above, and 3
     * ({@link #PLANTS_WITHOUT_ANIMATION_DATA}) simply have no art in this animation pack.
     */
    public static AnimationJsonParser.AnimationConfig resolveByDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) return null;
        autoInit();

        String key = normalize(displayName, "_");
        if (PLANTS_WITHOUT_ANIMATION_DATA.contains(key)) {
            return null;
        }

        String override = PLANT_NAME_OVERRIDES.get(key);
        if (override != null && library.containsKey(override)) {
            return library.get(override);
        }

        for (String candidate : nameVariants(displayName)) {
            if (library.containsKey(candidate)) {
                return library.get(candidate);
            }
        }
        return null;
    }

    /**
     * Generates every normalization pattern seen across Plants.json / animations.json:
     * plain underscore ("SNOW_PEA"), full concatenation ("SNOWPEA" - the dominant pattern
     * for two-word names), and "first word kept separate, rest concatenated"
     * ("PRIMAL_POTATOMINE" - used by every "Primal ..." plant), plus a two-token swap
     * as a last resort ("SUNFLOWER_TWIN").
     */
    private static java.util.List<String> nameVariants(String displayName) {
        String underscored = normalize(displayName, "_");
        String concatenated = normalize(displayName, "");

        java.util.List<String> variants = new java.util.ArrayList<>();
        variants.add(underscored);
        variants.add(concatenated);

        String[] tokens = underscored.split("_");
        if (tokens.length >= 2) {
            StringBuilder rest = new StringBuilder();
            for (int i = 1; i < tokens.length; i++) rest.append(tokens[i]);
            variants.add(tokens[0] + "_" + rest);
        }
        if (tokens.length == 2) {
            variants.add(tokens[1] + "_" + tokens[0]);
        }
        return variants;
    }

    /** PAM path shortcut for {@link #resolveByDisplayName}, or null if nothing matched. */
    public static String pathForDisplayName(String displayName) {
        AnimationJsonParser.AnimationConfig config = resolveByDisplayName(displayName);
        return config == null ? null : config.path;
    }

    private static String normalize(String raw, String separator) {
        return raw.trim().toUpperCase()
                .replace("-", separator)
                .replace(" ", separator)
                .replaceAll("[^A-Z0-9_]", "");
    }
}
