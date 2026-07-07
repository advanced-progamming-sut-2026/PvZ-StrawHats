package model.projectile.hit;

import model.collections.zombie.Zombie;

public class PoisonHit implements HitEffectStrategy {
    private int areaLength;

    public PoisonHit(int areaLength) {
        this.areaLength = areaLength;
    }
    @Override
    public void apply(Zombie zombie) {

    }
}
