package model.projectile.hit;

import model.collections.zombie.Zombie;

public class ButterHit implements HitEffectStrategy{
    private final int areaLength;

    public ButterHit(int areaLength) {
        this.areaLength = areaLength;
    }
    @Override
    public void apply(Zombie zombie) {
        if (zombie == null || !zombie.isAlive()) return;
        zombie.setStatus(Zombie.Status.BUTTER);
    }
}