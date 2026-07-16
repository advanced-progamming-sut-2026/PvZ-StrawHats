package model.collections.zombie;

import java.util.Collections;
import java.util.Map;

public final class BehaviorSpec {
    private final String type;
    private final Map<String, Object> params;

    private BehaviorSpec(String type, Map<String, Object> params) {
        this.type = type;
        this.params = params;
    }

    @SuppressWarnings("unchecked")
    public static BehaviorSpec parse(Object raw) {
        if (raw instanceof String text) {
            return new BehaviorSpec(text, Collections.emptyMap());
        }

        if (raw instanceof Map<?, ?> rawMap) {
            Object typeVal = rawMap.get("type");
            if (typeVal == null) {
                throw new IllegalArgumentException("Behavior configuration error: 'type' key is missing in " + rawMap);
            }
            return new BehaviorSpec(typeVal.toString(), (Map<String, Object>) rawMap);
        }

        throw new IllegalArgumentException("Invalid behavior configuration format: " + raw);
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> params() {
        return params;
    }

    public static double getDouble(Map<String, Object> source, String key, double defaultValue) {
        if (source == null) return defaultValue;
        Object val = source.get(key);
        if (val instanceof Number num) {
            return num.doubleValue();
        }
        if (val instanceof String str) {
            try { return Double.parseDouble(str); } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public static int getInt(Map<String, Object> source, String key, int defaultValue) {
        if (source == null) return defaultValue;
        Object val = source.get(key);
        if (val instanceof Number num) {
            return num.intValue();
        }
        if (val instanceof String str) {
            try { return Integer.parseInt(str); } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    public static boolean getBoolean(Map<String, Object> source, String key, boolean defaultValue) {
        if (source == null) return defaultValue;
        Object val = source.get(key);
        if (val instanceof Boolean bool) {
            return bool;
        }
        if (val instanceof String str) {
            return Boolean.parseBoolean(str);
        }
        return defaultValue;
    }

    public static String getString(Map<String, Object> source, String key, String defaultValue) {
        if (source == null) return defaultValue;
        Object val = source.get(key);
        return val != null ? val.toString() : defaultValue;
    }
}