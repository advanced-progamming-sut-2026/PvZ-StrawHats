package model.collections.plant;

import model.collections.plant.actstrategy.ExplodeStrategy;
import model.collections.plant.actstrategy.HomingStrategy;
import model.collections.plant.actstrategy.SunProduceStrategy;
import model.match_mechanisms.vector.Position;
import model.collections.armour.ArmourFactory;
import model.collections.armour.ArmourType;
import model.collections.armour.PlantArmour;
import model.collections.plant.AbilityType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantFactory {

    private static Map<Integer, PlantJsonParser.PlantConfig> blueprints = new HashMap<>();
    private static boolean loaded = false;

    public static void init(InputStream jsonStream) {
        blueprints = PlantJsonParser.loadConfigs(jsonStream);
        loaded = true;
    }

    public static void autoInit() {
        if (loaded) return;
        try (java.io.FileInputStream fis = new java.io.FileInputStream("resource/Plants.json")) {
            init(fis);
        } catch (java.io.IOException e) {
            System.out.println("Could not load resource/Plants.json: " + e.getMessage());
        }
    }

    public static Map<Integer, PlantJsonParser.PlantConfig> getBlueprints() {
        autoInit();
        return blueprints;
    }

    public static Plant createPlant(int id, int level, Position position) {
        autoInit();
        PlantJsonParser.PlantConfig config = blueprints.get(id);
        if (config == null) {
            throw new IllegalArgumentException("Plant ID " + id + " does not exist in dataset.");
        }

        int runtimeHp = config.baseHp;
        int runtimeCost = config.cost;
        double runtimeInterval = config.actionInterval;
        int runtimeDamage = config.damage;
        double runtimeRecharge = config.recharge;
        double runtimeAbility = config.abilityValue;

        if (config.upgrades != null) {
            for (PlantJsonParser.UpgradeConfig upgrade : config.upgrades) {
                if (upgrade.level <= level) {
                    switch (upgrade.type) {
                        case BUFF_HP -> runtimeHp += (int) upgrade.value;
                        case BUFF_COST -> runtimeCost += (int) upgrade.value;
                        case BUFF_ACTION_INTERVAL -> runtimeInterval += upgrade.value;
                        case BUFF_DAMAGE -> runtimeDamage += (int) upgrade.value;
                        case BUFF_RECHARGE -> runtimeRecharge += upgrade.value;
                    }
                }
            }
        }

        Plant plant = new GenericPlant(config.name, position, Math.max(0, runtimeHp));
        plant.setId(config.id);
        plant.setType(config.category);
        if (config.tags != null) {
            plant.getTags().addAll(config.tags);
        }

        plant.setCost(Math.max(0, runtimeCost));
        plant.setActionInterval(Math.max(0.05, runtimeInterval));
        plant.setDamage(runtimeDamage);
        plant.setAbilityValue(runtimeAbility);
        plant.setLevel(level);
        plant.setPlantFoodType(config.plantFoodType);
        plant.setWrampUp(config.wrampUp);

        // انتساب استراتژی‌های اجرایی واقعی بر اساس نوع توانایی گیاه
        if (config.abilityType != null) {
            switch (config.abilityType) {
                case PRODUCE_SUN -> plant.setActStrategy(new SunProduceStrategy());
                case SHOOT_PROJECTILE -> plant.setActStrategy(new HomingStrategy());
                case INSTANT_EXPLOSIVE, DELAYED_EXPLOSIVE -> plant.setActStrategy(new ExplodeStrategy());
                default -> plant.setActStrategy(null);
            }
        }

        if (config.abilityType == AbilityType.PASSIVE_SHIELD) {
            PlantArmour plantArmor = (PlantArmour) ArmourFactory.createArmour(
                    ArmourType.PLANT_SHIELD,
                    (int) config.abilityValue,
                    0,
                    false
            );
            plant.setArmor(plantArmor);
        }

        plant.setShootingVectors(buildShootingVectors(config));
        return plant;
    }

    private static List<Position> buildShootingVectors(PlantJsonParser.PlantConfig config) {
        List<Position> vectors = new ArrayList<>();
        if (config.abilityType == AbilityType.SHOOT_PROJECTILE) {
            vectors.add(new Position(1, 0));
        }
        return vectors;
    }

    private static class GenericPlant extends Plant {
        public GenericPlant(String name, Position position, int HP) { super(name, position, HP); }
    }
}