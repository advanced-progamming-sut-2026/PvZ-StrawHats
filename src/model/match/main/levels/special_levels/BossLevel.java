package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.collections.zombie.Zombie;

public class BossLevel extends Level {
    private Zombie bossZombie;

    public Zombie getBossZombie() { return bossZombie; }
    public void setBossZombie(Zombie bossZombie) { this.bossZombie = bossZombie; }
}