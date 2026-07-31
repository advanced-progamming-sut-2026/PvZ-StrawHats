package model.projectile.hit;

import model.collections.zombie.Zombie;

public class NormalHit implements HitEffectStrategy {
    private final int areaLength;

    public NormalHit(int areaLength) {
        this.areaLength = Math.max(1, areaLength);
    }

    @Override
    public void apply(Zombie zombie) {
    }

    @Override
    public int getAreaLength() { return areaLength; }
}
