package model.collections.zombie.zombie_effect;

import model.collections.zombie.Zombie;
import util.GameSession;

public interface EffectStatus {
    void effect(Zombie zombie, GameSession session);
}