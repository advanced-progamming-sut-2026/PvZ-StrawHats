package model.collections.zombie.zombie_attack;

import java.util.HashMap;
import java.util.Map;

import model.collections.zombie.BehaviorSpec;
import model.utils.GameSession;

public final class AttackBehaviorRegistry {
    private static final Map<String, AttackBehaviorFactory> REGISTRY = new HashMap<>();

    static {
        register("ChompAttack", (params, data) -> new ChompAttack());

        register("CrushAttack", (params, data) -> {
            int baseDps = BehaviorSpec.getInt(data, "EatDPS", 0);
            int finalDamage = BehaviorSpec.getInt(params, "crushDamage", baseDps);
            return new CrushAttack(finalDamage);
        });

        register("SmashAttack", (params, data) -> {
            int fallbackDamage = BehaviorSpec.getInt(data, "SmashDamage", 0);
            int finalDamage = BehaviorSpec.getInt(params, "smashDamage", fallbackDamage);
            double windup = BehaviorSpec.getDouble(params, "windupDuration", 0.0);
            boolean singleUse = BehaviorSpec.getBoolean(params, "isOneTime", false);
            double postSpeedScale = BehaviorSpec.getDouble(params, "speedScaleAfter", 1.0);
            return new SmashAttack(finalDamage, windup, singleUse, postSpeedScale);
        });

        register("KamikazeAttack", (params, data) -> {
            int power = BehaviorSpec.getInt(params, "damage", 1800);
            return new KamikazeAttack(power);
        });
    }

    private AttackBehaviorRegistry() {}

    public static void register(String name, AttackBehaviorFactory factory) {
        REGISTRY.put(name, factory);
    }

    public static AttackBehavior create(Object rawConfig, Map<String, Object> zombieData) {
        if (rawConfig == null) {
            return new ChompAttack();
        }
        BehaviorSpec spec = BehaviorSpec.parse(rawConfig);
        AttackBehaviorFactory factory = REGISTRY.get(spec.getType());
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported attack style: " + spec.getType());
        }
        return factory.create(spec.params(), zombieData);
    }
}