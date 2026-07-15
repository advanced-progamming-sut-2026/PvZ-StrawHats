package model.collections.zombie.zombie_effect;

import model.collections.zombie.Zombie;
import util.GameSession;

public interface ZombieEffectStatus {
    void applyTickEffect(Zombie target, GameSession session);
}