package model.collections.zombie.zombie_effect;

import model.collections.zombie.BehaviorSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EffectStatusRegistry {
    private static final Map<String, EffectStatusFactory> REGISTRY = new HashMap<>();

    static {
        register("TombRaiserEffect", (params, data) -> new GraveErectorStatus(
                BehaviorSpec.getDouble(params, "cooldown", 6.0),
                BehaviorSpec.getInt(params, "numTombsToSpawn", 2)
        ));

        register("SunThief", (params, data) -> new SunThief(
                BehaviorSpec.getBoolean(params, "isBankThief", false),
                BehaviorSpec.getInt(params, "maxSunsToSteal", 250),
                BehaviorSpec.getDouble(params, "dropRatioOnDeath", 1.0),
                BehaviorSpec.getDouble(params, "chargingTime", 5.0),
                BehaviorSpec.getInt(params, "laserDamage", 1800)
        ));

        register("SpinEffect", (params, data) -> new RotationalTurbulenceState());

        register("FishermanEffect", (params, data) -> new ReelingTackleStatus(
                BehaviorSpec.getDouble(params, "delayBetweenCasting", 2.5)
        ));

        register("OctopusThrowEffect", (params, data) -> new OctopusThrow(
                BehaviorSpec.getDouble(params, "throwCooldown", 5.0)
        ));

        register("IceAgeHunterEffect", (params, data) -> new IceAgeHunterEffect(
                BehaviorSpec.getDouble(params, "throwCooldown", 4.0)
        ));

        register("FireEffect", (params, data) -> new FireEffect(
                BehaviorSpec.getDouble(params, "reach", 1.0)
        ));

        register("WizardEffect", (params, data) -> new MageState(
                BehaviorSpec.getDouble(params, "transformInterval", 8.0)
        ));

        register("KingBuffEffect", (params, data) -> new KingBuffEffect(
                BehaviorSpec.getDouble(params, "delayBetweenKnighting", 2.5)
        ));

        register("PianistEffect", (params, data) -> new PianistMusicEffect(
                BehaviorSpec.getDouble(params, "danceInterval", 2.5)
        ));

        register("GargantuarImpThrowEffect", (params, data) -> new GigantorImpChucker(
                readHealthPercentThrowImp(data, params),
                BehaviorSpec.getDouble(data, "ImpApex", 250.0),
                BehaviorSpec.getDouble(data, "ImpFlightTime", 1.5),
                BehaviorSpec.getInt(data, "ImpTargetColumn", 2),
                BehaviorSpec.getDouble(data, "MinPosXThrowImp", 0.0),
                BehaviorSpec.getString(params, "impAlias", "ZombieImp")
        ));

        register("JalapenoZombieEffect", (params, data) -> new ThermiteExplosion());

        register("PeashooterZombieEffect", (params, data) -> new PeashooterZombieEffect(
                BehaviorSpec.getInt(params, "damage", 20),
                BehaviorSpec.getDouble(params, "fireRate", 1.5)
        ));
    }

    private EffectStatusRegistry() {}

    public static void register(String name, EffectStatusFactory factory) {
        REGISTRY.put(name, factory);
    }

    public static ZombieEffectStatus createOrNull(Object rawConfigOrNull, Map<String, Object> zombieData) {
        if (rawConfigOrNull == null) return null;

        BehaviorSpec spec = BehaviorSpec.parse(rawConfigOrNull);
        EffectStatusFactory factory = REGISTRY.get(spec.getType());
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported passive/active effect: " + spec.getType());
        }
        return factory.create(spec.params(), zombieData);
    }

    @SuppressWarnings("unchecked")
    private static double readHealthPercentThrowImp(Map<String, Object> data, Map<String, Object> params) {
        if (params != null && params.containsKey("healthPercentThrowImp")) {
            return BehaviorSpec.getDouble(params, "healthPercentThrowImp", 0.5);
        }

        if (data != null && data.containsKey("HealthThresholdToImpAmmoLayers")) {
            Object rawList = data.get("HealthThresholdToImpAmmoLayers");
            if (rawList instanceof List<?> list && !list.isEmpty()) {
                Object firstElem = list.get(0);
                if (firstElem instanceof Map<?, ?> firstMap) {
                    Object threshold = firstMap.get("HealthPercentThrowImp");
                    if (threshold instanceof Number num) {
                        return num.doubleValue();
                    }
                }
            }
        }
        return 0.5;
    }
}