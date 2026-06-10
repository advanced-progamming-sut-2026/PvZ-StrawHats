package model.match.main.levels.special_levels;

import model.collections.zombie.Zombie;
import model.match.main.levels.Level;
import model.match.main.season.Season;

public abstract class BossLevelFactory extends Level {
    public Zombie bossZombie;
    public Season season;
}
