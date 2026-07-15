package model.collections.zombie.zombie_attack;

import model.collections.zombie.Zombie;
import model.utils.GameSession;

public interface AttackBehavior {
    void attack(Zombie zombie, GameSession session);
}