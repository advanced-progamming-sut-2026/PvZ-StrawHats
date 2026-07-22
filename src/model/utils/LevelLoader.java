package model.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match.main.levels.Level;
import model.match.main.levels.normal_levels.NormalLevel;
import model.match.main.levels.special_levels.*;
import model.match.main.season.SeasonFactory;
import model.match_mechanisms.Time;
import model.match_mechanisms.ZombieWave;
import model.match_mechanisms.vector.Position;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelLoader {
    private static final Gson gson = new Gson();

    public static List<Level> loadLevels(String resourcePath) throws java.io.IOException {
        try (var is = new java.io.FileInputStream(resourcePath)) {
            Type listType = new TypeToken<List<JsonObject>>() {}.getType();
            List<JsonObject> rawLevels = gson.fromJson(new InputStreamReader(is), listType);
            List<Level> levels = new ArrayList<>();
            for (JsonObject raw : rawLevels) {
                levels.add(parseLevel(raw));
            }
            return levels;
        }
    }

    private static Level parseLevel(JsonObject raw) {
        String type = raw.get("type").getAsString();
        Level level;

        switch (type) {
            case "normal":          level = new NormalLevel(); break;
            case "conveyor":        level = new ConveyorBeltLevel(); break;
            case "locked":          level = new LockedPlantsLevel(); break;
            case "saveSeeds":       level = new SaveOurSeedsLevel(); break;
            case "timedWar":        level = new TimedWarLevel(); break;
            case "nightOps":        level = new NightOpsLevel(); break;
            case "deadLine":        level = new DeadLineLevel(); break;
            case "lovePlants":      level = new LoveYourPlantsLevel(); break;
            case "plantWhatYouGet": level = new PlantWhatYouGetLevel(); break;
            case "boss":            level = new BossLevel(); break;
            case "intro":           level = new IntroductionLevel(); break;
            default: throw new IllegalArgumentException("Unknown level type: " + type);
        }

        level.setId(raw.get("id").getAsInt());
        level.setName(raw.get("name").getAsString());

        String seasonName = raw.get("season").getAsString();
        level.setSeason(SeasonFactory.create(seasonName));

        level.setRows(raw.has("rows") ? raw.get("rows").getAsInt() : 5);
        level.setCols(raw.has("cols") ? raw.get("cols").getAsInt() : 9);
        level.setInitialSun(raw.has("initialSun") ? raw.get("initialSun").getAsInt() : 150);

        List<String> availablePlants = new ArrayList<>();
        if (raw.has("availablePlants")) {
            raw.get("availablePlants").getAsJsonArray().forEach(e -> availablePlants.add(e.getAsString()));
        }
        level.setAvailablePlants(availablePlants);

        List<String> forcedPlants = new ArrayList<>();
        if (raw.has("forcedPlants")) {
            raw.get("forcedPlants").getAsJsonArray().forEach(e -> forcedPlants.add(e.getAsString()));
        }
        level.setForcedPlants(forcedPlants);

        if (raw.has("waves")) {
            JsonArray waveArray = raw.get("waves").getAsJsonArray();
            List<ZombieWave> waves = new ArrayList<>();
            for (var wElem : waveArray) {
                JsonObject w = wElem.getAsJsonObject();
                double delay = w.get("delay").getAsDouble();
                JsonArray zombieTypes = w.get("zombies").getAsJsonArray();
                List<Zombie> zombies = new ArrayList<>();
                for (var zt : zombieTypes) {
                    Zombie z = ZombieFactory.create(zt.getAsString(),9, 0);
                    z.setPosition(new Position(9, 0));
                    zombies.add(z);
                }
                waves.add(new ZombieWave(delay, zombies));
            }
            level.setWaves(waves);
        }

        if (level instanceof ConveyorBeltLevel) {
            List<Plant> conveyorPlants = new ArrayList<>();
            if (raw.has("conveyorPlants")) {
                raw.get("conveyorPlants").getAsJsonArray().forEach(e -> {
                    String plantType = e.getAsString();
                    Integer plantId = null;
                    for (var config : PlantFactory.getBlueprints().values()) {
                        if (config.name.equalsIgnoreCase(plantType)) {
                            plantId = config.id;
                            break;
                        }
                    }
                    if (plantId == null) return;
                    Plant p = PlantFactory.createPlant(plantId, 1, new Position(9, 0));
                    conveyorPlants.add(p);
                });
            }
            ((ConveyorBeltLevel) level).setConveyorPlants(conveyorPlants);
            if (raw.has("maxConveyorSize")) {
                ((ConveyorBeltLevel) level).setMaxConveyorSize(raw.get("maxConveyorSize").getAsInt());
            }
        } else if (level instanceof LockedPlantsLevel) {
            List<String> locked = new ArrayList<>();
            if (raw.has("lockedPlants")) {
                raw.get("lockedPlants").getAsJsonArray().forEach(e -> locked.add(e.getAsString()));
            }
            ((LockedPlantsLevel) level).setLockedPlants(locked);

            List<String> always = new ArrayList<>();
            if (raw.has("alwaysAvailable")) {
                raw.get("alwaysAvailable").getAsJsonArray().forEach(e -> always.add(e.getAsString()));
            }
            ((LockedPlantsLevel) level).setAlwaysAvailable(always);
        } else if (level instanceof SaveOurSeedsLevel) {
            if (raw.has("seedPositions")) {
                JsonObject seedObj = raw.get("seedPositions").getAsJsonObject();
                Map<Position, String> seedMap = new HashMap<>();
                for (var entry : seedObj.entrySet()) {
                    String key = entry.getKey(); // "row,col"
                    String plantType = entry.getValue().getAsString();
                    String[] parts = key.split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    seedMap.put(new Position(col, row), plantType);
                }
                ((SaveOurSeedsLevel) level).setSeedPositions(seedMap);
            }
        } else if (level instanceof TimedWarLevel) {
            double timeSec = raw.get("timeLimitSeconds").getAsDouble();
            ((TimedWarLevel) level).setTimeLimit(new Time(timeSec));
            ((TimedWarLevel) level).setZombiesToKill(raw.get("zombiesToKill").getAsInt());
        } else if (level instanceof DeadLineLevel) {
            int col = raw.get("deadLineColumn").getAsInt();
            ((DeadLineLevel) level).setDeadLine(new Position(col, 0));
        } else if (level instanceof LoveYourPlantsLevel) {
            int maxLoss = raw.has("maxPlantLoss") ? raw.get("maxPlantLoss").getAsInt() : 3;
            ((LoveYourPlantsLevel) level).setMaxPlantLoss(maxLoss);
        } else if (level instanceof BossLevel) {
            String bossType = raw.get("bossType").getAsString();
            ((BossLevel) level).setBossZombie(ZombieFactory.create(bossType, 9, 0));
        }

        return level;
    }
}