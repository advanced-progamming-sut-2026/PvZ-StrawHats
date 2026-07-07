package model.projectile.hit;

import model.collections.zombie.Zombie;

import java.util.ArrayList;

public class PierceKnockBackHit implements HitEffectStrategy{
    private final int pierceNumber; // Use -1 for infinite pierce
    private final double knockbackDistance;
    private final ArrayList<Zombie> hitZombies;

    public PierceKnockBackHit(int pierceNumber, double knockbackDistance) {
        this.pierceNumber = pierceNumber;
        this.knockbackDistance = knockbackDistance;
        this.hitZombies = new ArrayList<>();
    }
    @Override
    public void apply(Zombie zombie) {
        //todo : call the zombie.knockback(knockbackdistance) here
    }
}

