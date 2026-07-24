package model.projectile.hit;

import model.collections.zombie.Zombie;

public interface HitEffectStrategy {
    void apply(Zombie zombie);
    
    // is arc move it should call zombie.takeDamage(int damage , Object moveStrategy)
}
