package model.collections.animations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import view.GeneralPrinter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AnimationJsonParser {

    public static class AnimationConfig {
        public String name;
        public String path;
        public float[] canvas;
        public Map<String, Double> clips;

        public float canvasWidth() {
            return canvas != null && canvas.length > 0 ? canvas[0] : 0f;
        }

        public float canvasHeight() {
            return canvas != null && canvas.length > 1 ? canvas[1] : 0f;
        }

        public boolean hasClip(String clipName) {
            return clips != null && clips.containsKey(clipName);
        }
    }

    private static class AnimationCatalog {
        int count;
        java.util.List<AnimationConfig> animations;
    }

    private static Gson buildGson() {
        return new GsonBuilder().create();
    }

    public static Map<String, AnimationConfig> loadConfigs(InputStream is) {
        Map<String, AnimationConfig> result = new HashMap<>();
        if (is == null) return result;
        Gson gson = buildGson();
        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Type catalogType = new TypeToken<AnimationCatalog>() {}.getType();
            AnimationCatalog catalog = gson.fromJson(reader, catalogType);
            if (catalog != null && catalog.animations != null) {
                for (AnimationConfig config : catalog.animations) {
                    if (config != null && config.name != null) {
                        result.put(config.name.toUpperCase(), config);
                    }
                }
            }
        } catch (IOException e) {
            GeneralPrinter.print("Could not load animation data: " + e.getMessage());
        }
        return result;
    }
}
