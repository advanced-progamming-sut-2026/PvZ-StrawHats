package model.collections.zombie.zombie_effect;

import java.util.HashMap;
import java.util.Map;

public class EffectStatusRegistry {

    public static ZombieEffectStatus createOrNull(Object spec, Map<String, Object> zombieData) {
        if (spec == null) return null;

        String type;
        Map<String, Object> params;

        if (spec instanceof String s) {
            type = s;
            params = Map.of();
        } else if (spec instanceof Map<?, ?> rawMap) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                map.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            Object typeValue = map.get("type");
            type = typeValue != null ? typeValue.toString() : null;
            params = map;
        } else {
            return null;
        }

        if (type == null) return null;

        return switch (type) {
            case "FireEffect" -> new FireEffect(getDouble(params, "blastRadius", 1.0));

            case "GargantuarImpThrowEffect", "GigantorImpChucker" -> new GigantorImpChucker(
                    getDouble(params, "launchThreshold", 0.5),
                    getDouble(params, "arcApex", 150),
                    getDouble(params, "flightDuration", 1.0),
                    getInt(params, "targetCol", 0),
                    getDouble(params, "minSpawnX", 0),
                    getString(params, "impAlias", "ZombieImp"));

            case "TombRaiserEffect", "GraveErectorStatus" -> new GraveErectorStatus(
                    getDouble(params, "cooldown", 15.0),
                    getInt(params, "spawnCount", 1));

            case "IceAgeHunterEffect" -> new IceAgeHunterEffect(
                    getDouble(params, "snowballDelay", 4.0));

            case "FishermanEffect", "ReelingTackleStatus" -> new ReelingTackleStatus(
                    getDouble(params, "reelCooldown", 6.0));

            case "OctopusThrowEffect", "OctopusThrow" -> new OctopusThrow(
                    getDouble(params, "snareCooldown", 5.0));

            case "SpinEffect", "RotationalTurbulenceState" -> new RotationalTurbulenceState();

            case "WizardEffect", "MageState" -> new MageState(
                    getDouble(params, "hexCooldown", 4.0));

            case "KingBuffEffect" -> new KingBuffEffect(
                    getDouble(params, "coronationCooldown", 10.0));

            case "SunThief" -> new SunThief(
                    getBoolean(params, "isBankThief", false),
                    getInt(params, "maxSunsToSteal", 50),
                    getDouble(params, "dropRatioOnDeath", 0.5),
                    getDouble(params, "chargingTime", 3.0),
                    getInt(params, "laserDamage", 40));

            case "PeashooterZombieEffect" -> new PeashooterZombieEffect(
                    getInt(params, "damage", 20),
                    getDouble(params, "fireRate", 1.5));

            case "JalapenoZombieEffect", "ThermiteExplosion" -> new ThermiteExplosion(
                    getDouble(params, "fuseSeconds", 10.0));

            case "SunProducer" -> new SunProducer();

            default -> null;
        };
    }

    private static double getDouble(Map<String, Object> data, String key, double defaultValue) {
        Object value = data.get(key);
        return (value instanceof Number number) ? number.doubleValue() : defaultValue;
    }

    private static int getInt(Map<String, Object> data, String key, int defaultValue) {
        Object value = data.get(key);
        return (value instanceof Number number) ? number.intValue() : defaultValue;
    }

    private static boolean getBoolean(Map<String, Object> data, String key, boolean defaultValue) {
        Object value = data.get(key);
        return (value instanceof Boolean bool) ? bool : defaultValue;
    }

    private static String getString(Map<String, Object> data, String key, String defaultValue) {
        Object value = data.get(key);
        return (value instanceof String str) ? str : defaultValue;
    }
}
