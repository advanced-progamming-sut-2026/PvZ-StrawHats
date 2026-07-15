package model.collections.zombie.zombie_effect;

import model.collections.zombie.Zombie;
import model.utils.GameSession;

public interface ZombieEffectStatus {
    void applyTickEffect(Zombie target, GameSession session);
}