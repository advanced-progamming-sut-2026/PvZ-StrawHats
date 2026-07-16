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
     * The actual zombie instances are created here, spread one per row so
     * every lane gets its share when the wave count exceeds the row count.
     * (Exact spawn columns are still adjusted when the wave is actually spawned.)
     */
    public static ZombieWave createWave(double delay, String... zombieTypes) {
        List<Zombie> zombies = new ArrayList<>();
        int row = 0;
        for (String type : zombieTypes) {
            Zombie zombie = ZombieFactory.create(type, row, 9);
            zombie.setPosition(new Position(9, row));
            zombies.add(zombie);
            row = (row + 1) % 5;
        }
        return new ZombieWave(delay, zombies);
    }
}
