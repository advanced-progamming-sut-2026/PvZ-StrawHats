package model.collections.animations;

import model.utils.ResourceResolver;
import view.GeneralPrinter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AnimationFactory {

    private static Map<String, AnimationJsonParser.AnimationConfig> library = new HashMap<>();
    private static boolean loaded = false;

    // A handful of known names that don't survive plain normalization
    // (e.g. "Twin Sunflower" -> SUNFLOWER_TWIN, "Sun-shroom" -> SUNSHROOM).
    // Add more here as you find them while wiring up real screens.
    private static final Map<String, String> NAME_OVERRIDES = Map.of(
            "TWIN_SUNFLOWER", "SUNFLOWER_TWIN"
    );

    public static void init(InputStream jsonStream) {
        library = AnimationJsonParser.loadConfigs(jsonStream);
        loaded = true;
    }

    public static void autoInit() {
        if (loaded) return;
        try (InputStream is = ResourceResolver.open("Animations.json")) {
            if (is != null) {
                init(is);
                return;
            }
        } catch (java.io.IOException e) {
            GeneralPrinter.print("Could not load Animations.json: " + e.getMessage());
        }
        GeneralPrinter.print("Could not find Animations.json in any known location.");
    }

    public static Map<String, AnimationJsonParser.AnimationConfig> getLibrary() {
        autoInit();
        return library;
    }

    /** Exact lookup by the raw animation name as it appears in Animations.json (case-insensitive). */
    public static AnimationJsonParser.AnimationConfig get(String rawName) {
        if (rawName == null) return null;
        autoInit();
        return library.get(rawName.toUpperCase());
    }

    /**
     * Best-effort lookup for a plant/zombie display name (e.g. "Sun-shroom", "Twin Sunflower").
     * Tries a few normalization variants before giving up.
     */
    public static AnimationJsonParser.AnimationConfig resolveByDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) return null;
        autoInit();

        String noHyphenUnderscore = normalize(displayName, "_");
        String noHyphenAtAll = normalize(displayName, "");

        String override = NAME_OVERRIDES.get(noHyphenUnderscore);
        if (override != null && library.containsKey(override)) {
            return library.get(override);
        }
        if (library.containsKey(noHyphenUnderscore)) {
            return library.get(noHyphenUnderscore);
        }
        if (library.containsKey(noHyphenAtAll)) {
            return library.get(noHyphenAtAll);
        }

        String[] tokens = noHyphenUnderscore.split("_");
        if (tokens.length == 2) {
            String swapped = tokens[1] + "_" + tokens[0];
            if (library.containsKey(swapped)) {
                return library.get(swapped);
            }
        }
        return null;
    }

    /** PAM path shortcut for {@link #resolveByDisplayName}, or null if nothing matched. */
    public static String pathForDisplayName(String displayName) {
        AnimationJsonParser.AnimationConfig config = resolveByDisplayName(displayName);
        return config == null ? null : config.path;
    }

    private static String normalize(String raw, String hyphenReplacement) {
        return raw.trim().toUpperCase()
                .replace("-", hyphenReplacement)
                .replaceAll("[^A-Z0-9_ ]", "")
                .trim()
                .replace(' ', '_');
    }
}
