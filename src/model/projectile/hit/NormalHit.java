package model.projectile.hit;

import model.collections.zombie.Zombie;

public class NormalHit implements HitEffectStrategy {
    private int areaLength;

    public NormalHit(int areaLength) {
        this.areaLength = areaLength;
    }
    @Override
    public void apply(Zombie zombie) {

    }
}
