package model.projectile.hit;

import model.collections.zombie.Zombie;

public class PoisonHit implements HitEffectStrategy {
    private final int areaLength;

    public PoisonHit(int areaLength) {
        this.areaLength = Math.max(1, areaLength);
    }

    @Override
    public void apply(Zombie zombie) {
        if (zombie == null || !zombie.isAlive()) return;
        zombie.applyStatus(Zombie.Status.POISONED, 6.0);
    }

    @Override
    public int getAreaLength() { return areaLength; }

    @Override
    public boolean bypassesArmor() { return true; }
}
