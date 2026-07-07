package model.projectile.hit;

import model.collections.zombie.Zombie;

public interface HitEffectStrategy {
    void apply(Zombie zombie);
    //todo if the projectile move
    // is arc move it should call zombie.takeDamage(int damage , Object moveStrategy)
}
