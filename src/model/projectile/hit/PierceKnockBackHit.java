package model.projectile.hit;

import model.collections.zombie.Zombie;

public class PierceKnockBackHit implements HitEffectStrategy {
    private final int pierceNumber;
    private final double knockbackDistance;

    public PierceKnockBackHit(int pierceNumber, double knockbackDistance) {
        this.pierceNumber = pierceNumber == 0 ? 1 : pierceNumber;
        this.knockbackDistance = knockbackDistance;
    }

    @Override
    public void apply(Zombie zombie) {
    }

    @Override
    public int getPierceCount() { return pierceNumber; }

    @Override
    public double getKnockbackDistance() { return knockbackDistance; }
}
