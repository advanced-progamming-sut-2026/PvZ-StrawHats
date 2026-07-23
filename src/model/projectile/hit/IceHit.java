package model.projectile.hit;

import model.collections.zombie.Zombie;

public class IceHit implements HitEffectStrategy {
    private int areaLength;

    public IceHit(int areaLength) {
        this.areaLength = areaLength;
    }

    @Override
    public void apply(Zombie zombie) {
        if (zombie == null || !zombie.isAlive()) return;
        zombie.setStatus(Zombie.Status.CHILLED);
    }
}
