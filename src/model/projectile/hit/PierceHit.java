package model.projectile.hit;

import model.collections.zombie.Zombie;

public class PierceHit implements HitEffectStrategy {
    private final int pierceNumber;

    public PierceHit(int pierceNumber) {
        this.pierceNumber = pierceNumber == 0 ? 1 : pierceNumber;
    }

    @Override
    public void apply(Zombie zombie) {
    }

    @Override
    public int getPierceCount() { return pierceNumber; }
}
