package model.collections.zombie.zombie_defense;

import model.collections.zombie.Zombie;
import util.GameSession;

public interface DefenseBehavior {
    int handleDamage(Zombie zombie, int incomingDamage, Object damageSource, GameSession session);
}