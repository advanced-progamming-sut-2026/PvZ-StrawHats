package model.collections.zombie.zombie_defense;

import model.collections.zombie.BehaviorSpec;

import java.util.HashMap;
import java.util.Map;

public final class DefenseBehaviorRegistry {
    private static final Map<String, DefenseBehaviorFactory> REGISTRY = new HashMap<>();

    static {
        register("NormalDefense", params -> new StandardDefense());
        register("ArmorDefense", params -> new ArmorBasedDefense());
        register("ImmuneDefense", params -> new ThermalImmunityDefense());
        register("ParasolDefense", params -> new ParasolDeflection());
        register("JesterDefense", params -> new JesterDeflection());
    }

    private DefenseBehaviorRegistry() {}

    public static void register(String name, DefenseBehaviorFactory factory) {
        REGISTRY.put(name, factory);
    }

    public static DefenseBehavior create(Object rawConfig) {
        if (rawConfig == null) {
            return new StandardDefense();
        }
        BehaviorSpec spec = BehaviorSpec.parse(rawConfig);
        DefenseBehaviorFactory factory = REGISTRY.get(spec.getType());
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported defense style: " + spec.getType());
        }
        return factory.create(spec.params());
    }
}