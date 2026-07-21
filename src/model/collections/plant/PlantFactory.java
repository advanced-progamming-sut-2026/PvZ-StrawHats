package model.collections.plant;

import model.collections.armour.ArmourFactory;
import model.collections.armour.ArmourType;
import model.collections.armour.PlantArmour;
import model.collections.plant.actstrategy.*;
import model.collections.plant.plantfood.*;
import model.match_mechanisms.vector.Position;
import view.GeneralPrinter;

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
        try (java.io.InputStream is = new java.io.FileInputStream("src/resource/Plants.json")) {
            init(is);
        } catch (java.io.IOException e) {
            GeneralPrinter.print("Could not load Plants.json: " + e.getMessage());
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
        List<String> specialTags = new ArrayList<>();

        if (config.upgrades != null) {
            for (PlantJsonParser.UpgradeConfig upgrade : config.upgrades) {
                if (upgrade.level <= level) {
                    switch (upgrade.type) {
                        case BUFF_HP -> runtimeHp += (int) upgrade.value;
                        case BUFF_COST -> runtimeCost += (int) upgrade.value;
                        case BUFF_ACTION_INTERVAL -> runtimeInterval += upgrade.value;
                        case BUFF_DAMAGE -> runtimeDamage += (int) upgrade.value;
                        case BUFF_RECHARGE -> runtimeRecharge += upgrade.value;
                        case SPECIAL_MECHANIC -> specialTags.add(upgrade.specialTag);
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
        plant.setRecharge(Math.max(0, runtimeRecharge));
        plant.setDamage(runtimeDamage);
        plant.setAbilityValue(runtimeAbility);
        plant.setLevel(level);
        plant.setPlantFoodType(config.plantFoodType);
        plant.setWrampUp(config.wrampUp);
        plant.getRawUpgrades().addAll(specialTags);

        plant.setActStrategy(buildActStrategy(config));

        if (config.abilityType == AbilityType.PASSIVE_SHIELD) {
            PlantArmour plantArmor = (PlantArmour) ArmourFactory.createArmour(
                    ArmourType.PLANT_SHIELD,
                    (int) config.abilityValue,
                    0,
                    false
            );
            plant.setArmor(plantArmor);
        }

        plant.setPlantFoodEffect(buildPlantFoodEffect(config));
        plant.setShootingVectors(buildShootingVectors(config));
        return plant;
    }

    private static ActStrategy buildActStrategy(PlantJsonParser.PlantConfig config) {
        if (config.abilityType == AbilityType.MINT_FAMILY_BOOST) return new MintStrategy();
        if (config.abilityType == AbilityType.MODIFIER_UTILITY) return new ModifyStrategy();

        if (config.category == null) return null;
        return switch (config.category) {
            case SUN_PRODUCER -> new SunProduceStrategy();
            case SHOOTER -> new ShootStrategy();
            case HOMING -> new HomingStrategy();
            case STRIKE_THROUGH -> new StrikeStrategy();
            case LOBBER -> new LobberStrategy();
            case EXPLOSIVE -> new ExplodeStrategy();
            case MELEE -> new MeleeStrategy();
            case WALL_NUT -> new WallNutStrategy();
            case MODIFIER -> new ModifyStrategy();
            default -> null;
        };
    }

    private static PlantFoodEffect buildPlantFoodEffect(PlantJsonParser.PlantConfig config) {
        if (config.plantFoodType == null) return null;
        int value = (int) config.plantFoodValue;

        return switch (config.plantFoodType) {
            case NONE -> null;
            case SPAWN_SUN_ITEMS -> new SpawnSun(value);
            case PROJECTILE_BURST -> new TimedProjectileBurst(Math.max(1, value));
            case SPAWN_CLONES -> new SpawnClones(Math.max(1, value));
            case LOCAL_AOE_ATTACK -> new LocalAttack(2.0, value);
            case GRANT_PERMANENT_ARMOR -> new GrantArmor(value);
            case RANDOM_HYPNOTIZE -> new RandomHypnotize(Math.max(1, value));
            case KNOCKBACK_BLAST -> new KnockBackBlast(value, 2.0);
            case PULL_UNDERWATER -> new PullUnderWater(1);
            case MAP_WIDE_FREEZE -> new MapWideFreeze();
            case INSTANT_KILL -> new InstantKill();
            case LOBBER_BARRAGE -> new LobberBarrage(Math.max(1, value));
        };
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