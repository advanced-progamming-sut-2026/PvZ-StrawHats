package model.match.main.levels.special_levels;

import model.collections.zombie.ZombieFactory;
import model.match.main.season.Season;
import model.match_mechanisms.ZombieWave;

import java.util.Collections;
import java.util.List;

public class BossLevelFactory {
    public static BossLevel createBossLevel(int id, String name, Season season,
                                            int initialSun, List<ZombieWave> waves,
                                            List<String> availablePlants,
                                            String bossType) {
        BossLevel level = new BossLevel();
        level.setId(id);
        level.setName(name);
        level.setSeason(season);
        level.setInitialSun(initialSun);
        level.setWaves(waves);
        level.setAvailablePlants(availablePlants);
        level.setForcedPlants(Collections.emptyList());
        level.setBossZombie(ZombieFactory.create(bossType, 9, 0));
        return level;
    }
}