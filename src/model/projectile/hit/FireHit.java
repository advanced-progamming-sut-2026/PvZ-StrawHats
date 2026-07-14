package model.projectile.hit;

import model.collections.zombie.Zombie;

public class FireHit implements HitEffectStrategy {
    private int areaLength;

    public FireHit(int areaLength) {
        this.areaLength = areaLength;
    }

    @Override
    public void apply(Zombie zombie) {
        if (zombie == null || !zombie.isAlive()) return;
        zombie.setStatus(Zombie.Status.FIRED);
    }
}
