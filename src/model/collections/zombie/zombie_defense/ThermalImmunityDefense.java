package model.collections.zombie.zombie_defense;

import model.collections.zombie.Zombie;
import model.projectile.Projectile;
import util.GameSession;

public class ThermalImmunityDefense implements DefenseBehavior {

    @Override
    public int handleDamage(Zombie zombie, int incomingDamage, Object damageSource, GameSession session) {
        if (damageSource instanceof Projectile projectile) {
            if (projectile.getHitEffectStrategy() != null &&
                    projectile.getHitEffectStrategy().getClass().getSimpleName().contains("Fire")) {
                return 0;
            }
        }
        return incomingDamage;
    }
}