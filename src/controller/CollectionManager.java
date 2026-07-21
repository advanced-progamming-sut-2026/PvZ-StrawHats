package controller;

import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match.main.levels.Level;
import model.match_mechanisms.ZombieWave;
import model.user_data.UserState;
import model.utils.LevelLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionManager {
    private static final int PURCHASE_COST = 2000;

    public List<PlantJsonParser.PlantConfig> getAllPlants() {
        return new ArrayList<>(PlantFactory.getBlueprints().values());
    }

    public List<PlantJsonParser.PlantConfig> getUnlockedPlants(UserState state) {
        return getAllPlants().stream()
                .filter(p -> state.isPlantUnlocked(p.id))
                .collect(Collectors.toList());
    }

    public PlantJsonParser.PlantConfig findPlant(String name) {
        return getAllPlants().stream()
                .filter(p -> p.name.equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public Set<String> getAllZombieAliases() {
        return ZombieFactory.getAllZombieAliases();
    }

    public Set<String> getSeenZombieAliases(UserState state) {
        Set<String> seen = new HashSet<>();
        List<Level> levels;
        try {
            levels = LevelLoader.loadLevels("src/resource/Levels.json");
        } catch (Exception e) {
            return seen;
        }
        for (Level level : levels) {
            if (level.getId() > state.lastLevel || level.getWaves() == null) continue;
            for (ZombieWave wave : level.getWaves()) {
                if (wave.getWaveZombies() == null) continue;
                for (Zombie zombie : wave.getWaveZombies()) {
                    seen.add(zombie.getName());
                }
            }
        }
        return seen;
    }

    public Zombie findZombie(String alias) {
        for (String known : getAllZombieAliases()) {
            if (known.equalsIgnoreCase(alias)) return ZombieFactory.create(known, 0, 0);
        }
        return null;
    }

    public String formatPlant(PlantJsonParser.PlantConfig config, boolean unlocked, int level) {
        String tags = config.tags == null || config.tags.isEmpty()
                ? "None" : config.tags.stream().map(Enum::name).collect(Collectors.joining(", "));
        String family = config.tags == null || config.tags.isEmpty() ? "General" : config.tags.get(0).name();
        return "Name: " + config.name +
                " | Type: " + config.category +
                " | Family: " + family +
                " | Tags: " + tags +
                " | Level: " + (unlocked ? level : "-") +
                " | Status: " + (unlocked ? "Unlocked" : "Locked");
    }

    public String formatZombie(Zombie zombie) {
        return "Name: " + zombie.getName() +
                " | Type: " + zombie.getRace() +
                " | Family: " + zombie.getRace() +
                " | HP: " + zombie.getMaxHp();
    }

    public boolean purchasePlant(UserState state, PlantJsonParser.PlantConfig config) {
        if (state.isPlantUnlocked(config.id) || state.coins < PURCHASE_COST) return false;
        state.coins -= PURCHASE_COST;
        state.unlockPlant(config.id);
        return true;
    }

    public boolean upgradePlant(UserState state, PlantJsonParser.PlantConfig config) {
        if (!state.isPlantUnlocked(config.id)) return false;
        int currentLevel = state.getPlantLevel(config.id);
        int coinCost = currentLevel * 500;
        int packetsNeeded = currentLevel;
        if (state.coins < coinCost || state.seedPacketInventory.getOrDefault(config.id, 0) < packetsNeeded) {
            return false;
        }
        state.coins -= coinCost;
        state.seedPacketInventory.merge(config.id, -packetsNeeded, Integer::sum);
        state.setPlantLevel(config.id, currentLevel + 1);
        return true;
    }
}
