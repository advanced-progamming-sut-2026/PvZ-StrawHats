package model.collections.zombie;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.collections.armour.Armour;
import model.collections.armour.ArmourType;
import model.collections.armour.ZombieArmour;
import model.match_mechanisms.vector.Position;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ZombieFactory {
    private static final String ZOMBIES_PATH = "resource/Zombie.json";
    private static final String ARMOR_PATH = "resource/ArmorTypeData.json";
    private static final Map<String, Map<String, Object>> blueprints = new HashMap<>();
    private static final Map<String, Integer> armorBaseHp = new HashMap<>();
    private static boolean loaded = false;

    public static void init() {
        if (loaded) return;
        loadZombies();
        loadArmorData();
        loaded = true;
    }

    @SuppressWarnings("unchecked")
    private static void loadZombies() {
        File file = new File(ZOMBIES_PATH);
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> entries = new Gson().fromJson(reader, listType);
            if (entries == null) return;

            for (Map<String, Object> entry : entries) {
                List<String> aliases = (List<String>) entry.get("aliases");
                Map<String, Object> objdata = (Map<String, Object>) entry.get("objdata");
                if (aliases == null || objdata == null) continue;
                for (String alias : aliases) {
                    blueprints.put(alias, objdata);
                }
            }
        } catch (IOException ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadArmorData() {
        File file = new File(ARMOR_PATH);
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> entries = new Gson().fromJson(reader, listType);
            if (entries == null) return;

            for (Map<String, Object> entry : entries) {
                List<String> aliases = (List<String>) entry.get("aliases");
                Map<String, Object> objdata = (Map<String, Object>) entry.get("objdata");
                if (aliases == null || objdata == null || !objdata.containsKey("BaseHealth")) continue;
                int baseHp = ((Number) objdata.get("BaseHealth")).intValue();
                for (String alias : aliases) {
                    armorBaseHp.put(alias, baseHp);
                }
            }
        } catch (IOException ignored) {
        }
    }

    public static int getZombieCost(String alias) {
        init();
        Map<String, Object> data = blueprints.get(alias);
        if (data == null) return Integer.MAX_VALUE;
        return ((Number) data.getOrDefault("WavePointCost", 100)).intValue();
    }

    public static Zombie create(String alias, int row, int col) {
        init();
        Map<String, Object> data = blueprints.get(alias);
        if (data == null) {
            throw new IllegalArgumentException("Unknown zombie alias: " + alias);
        }
        return buildBaseZombie(alias, data, row, col);
    }

    private static Zombie buildBaseZombie(String alias, Map<String, Object> data, int row, int col) {
        int hp = ((Number) data.getOrDefault("Hitpoints", 270)).intValue();
        double speed = ((Number) data.getOrDefault("Speed", 1)).doubleValue();
        double eatDps = ((Number) data.getOrDefault("EatDPS", 60)).doubleValue();

        String sizeStr = data.containsKey("Size") ? (String) data.get("Size") : "default";
        ZombieRace race = switch (sizeStr.toLowerCase()) {
            case "imp" -> ZombieRace.IMP;
            case "large" -> ZombieRace.GARGANTUAR;
            default -> ZombieRace.DEFAULT;
        };

        boolean canSpawnPlantFood = !data.containsKey("CanSpawnPlantFood") || (Boolean) data.get("CanSpawnPlantFood");
        Armour armour = resolveArmor(data);

        Zombie zombie = new Zombie(alias, armour, canSpawnPlantFood);
        zombie.setMaxHp(hp);
        zombie.setHp(hp);
        zombie.setEatDps(eatDps);
        zombie.setRace(race);

        Position spawnPos = Position.of(col, row);
        zombie.setPosition(spawnPos);
        zombie.setSpeed(Position.of(-speed, 0));

        applyDifficultyScaling(zombie, data);

        return zombie;
    }

    @SuppressWarnings("unchecked")
    private static void applyDifficultyScaling(Zombie zombie, Map<String, Object> data) {
        int diff = 1;
        List<Map<String, Object>> scaledProps = (List<Map<String, Object>>) data.get("ScaledProps");
        if (scaledProps == null) return;

        for (Map<String, Object> prop : scaledProps) {
            if (!"standard".equals(prop.get("Formula"))) continue;

            double arg1 = ((Number) prop.get("Arg1")).doubleValue();
            double arg2 = ((Number) prop.get("Arg2")).doubleValue();
            double scale = arg1 + ((diff - 1) * arg2);

            if ("Hitpoints".equals(prop.get("Key"))) {
                zombie.setMaxHp((int) (zombie.getMaxHp() * scale));
                zombie.setHp(zombie.getMaxHp());
            } else if ("EatDPS".equals(prop.get("Key"))) {
                zombie.setEatDps(zombie.getEatDps() * scale);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Armour resolveArmor(Map<String, Object> data) {
        List<String> armorProps = (List<String>) data.get("ZombieArmorProps");
        if (armorProps == null || armorProps.isEmpty()) return null;

        int accumulatedArmorHp = 0;
        ArmourType primaryType = null;

        for (String rtid : armorProps) {
            String armorAlias = parseRtidAlias(rtid);
            ArmourType resolvedType = resolveArmorType(armorAlias);
            if (resolvedType != null) {
                primaryType = resolvedType;
                int hpValue = armorBaseHp.getOrDefault(armorAlias, resolvedType.getArmorHp());
                accumulatedArmorHp += hpValue;
            }
        }

        if (primaryType == null || accumulatedArmorHp <= 0) return null;
        return new ZombieArmour(primaryType, accumulatedArmorHp);
    }

    public static Armour createKnightArmor() {
        int crownHp = armorBaseHp.getOrDefault("CrownDefault", ArmourType.CROWN.getArmorHp());
        int shoulderHp = armorBaseHp.getOrDefault("ShoulderArmorDefault", ArmourType.SHOULDER_ARMOR.getArmorHp());
        return new ZombieArmour(ArmourType.CROWN, crownHp + shoulderHp);
    }

    private static String parseRtidAlias(String rtid) {
        int start = rtid.indexOf('(');
        int at = rtid.indexOf('@');
        if (start < 0 || at < 0) return rtid;
        return rtid.substring(start + 1, at);
    }

    private static ArmourType resolveArmorType(String alias) {
        return switch (alias) {
            case "ConeDefault" -> ArmourType.CONE;
            case "BucketDefault" -> ArmourType.BUCKET;
            case "BrickDefault" -> ArmourType.BRICK;
            case "CrownDefault" -> ArmourType.CROWN;
            case "ShoulderArmorDefault" -> ArmourType.SHOULDER_ARMOR;
            case "NewspaperDefault" -> ArmourType.NEWSPAPER;
            default -> null;
        };
    }

    public static Set<String> getAllZombieAliases() {
        init();
        return blueprints.keySet();
    }
}
