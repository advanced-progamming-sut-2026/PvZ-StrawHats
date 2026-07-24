package model.projectile.hit;

import model.collections.zombie.Zombie;

public class PoisonHit implements HitEffectStrategy {
    private final int areaLength;

    public PoisonHit(int areaLength) {
        this.areaLength = areaLength;
    }
    @Override
    public void apply(Zombie zombie) {
        if (zombie == null || !zombie.isAlive()) return;
        zombie.setStatus(Zombie.Status.POISONED);
    }
}