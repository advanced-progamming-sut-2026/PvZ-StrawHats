package model.match.main.levels.normal_levels;

import model.match.main.levels.Level;
import model.match.main.season.Season;
import model.match_mechanisms.ZombieWave;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match_mechanisms.vector.Position;

import java.util.ArrayList;
import java.util.List;

public class NormalLevelFactory {
    /**
     * Creates a normal level with the given parameters.
     * these parameters may come from JSON, amazing for updates!
     */
    public static NormalLevel createLevel(int id, String name, Season season,
                                          int initialSun, List<ZombieWave> waves,List<String> availablePlants) {
        NormalLevel level = new NormalLevel();
        level.setId(id);
        level.setName(name);
        level.setSeason(season);
        level.setInitialSun(initialSun);
        level.setWaves(waves);
        level.setAvailablePlants(availablePlants != null ? availablePlants : new ArrayList<>());
        level.setForcedPlants(new ArrayList<>()); // normal levels have no forced plants
        return level;
    }

    /**
     * Helper to build a single wave with a list of zombie types.
     * The actual zombie instances are created here, with default positions.
     * (Positions will be adjusted when spawned in GameSession.)
     */
    public static ZombieWave createWave(double delay, String... zombieTypes) {
        List<Zombie> zombies = new ArrayList<>();
        for (String type : zombieTypes) { // TODO: fix this part after zombies, and their positions
            Zombie z = ZombieFactory.create(type, 9, 0);
            // positions set to (9, 0) as placeholder.

        }
        return new ZombieWave();
    }
}
