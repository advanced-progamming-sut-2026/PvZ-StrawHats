package model.collections.zombie.zombie_attack;

import model.collections.zombie.Zombie;
import util.GameSession;

public interface AttackBehavior {
    void attack(Zombie zombie, GameSession session);
}