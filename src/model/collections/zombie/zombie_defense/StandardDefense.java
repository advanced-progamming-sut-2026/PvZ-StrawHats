package model.collections.zombie.zombie_defense;

import model.collections.zombie.Zombie;
import util.GameSession;

public class StandardDefense implements DefenseBehavior {

    @Override
    public int handleDamage(Zombie zombie, int incomingDamage, Object damageSource, GameSession session) {
        return incomingDamage;
    }
}