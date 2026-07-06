package model.collections.plant;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantJsonParser {
    public static class UpgradeConfig {
        public int level;
        public UpgradeType type;
        public double value;
    }
    public static class PlantConfig {
        public int id;
        public String name;
        public model.collections.plant.PlantType category;
        public List<PlantTag> tags;
        public int baseHp;
        public int cost;
        public double actionInterval;
        public int damage;
        public double recharge;
        public double abilityValue;
        public AbilityType abilityType;
        public PlantFoodType plantFoodType;
        public List<UpgradeConfig> upgrades;
        public List<Map<String, Object>> wrampUp;
    }
    public static Map<Integer, PlantConfig> loadConfigs(InputStream is) {
        return new HashMap<>();
    }
}
