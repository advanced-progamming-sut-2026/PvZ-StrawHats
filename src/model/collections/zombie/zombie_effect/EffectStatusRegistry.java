package model.collections.zombie.zombie_effect;

import java.util.Map;

public class EffectStatusRegistry {
    public static EffectStatus createOrNull(Object spec, Map<String, Object> data) {
        if (spec == null) return null;
        return (zombie, session) -> {
            // افکت‌های خاص زامبی در هر تیک
        };
    }
}