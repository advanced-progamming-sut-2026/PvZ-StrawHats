package model.projectile.hit;

import model.collections.zombie.Zombie;

public class ButterHit implements HitEffectStrategy{
    private int areaLength;

    public ButterHit(int areaLength) {
        this.areaLength = areaLength;
    }
    @Override
    public void apply(Zombie zombie) {

    }
}
