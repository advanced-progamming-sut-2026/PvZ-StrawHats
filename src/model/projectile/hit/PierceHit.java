package model.projectile.hit;

import model.collections.zombie.Zombie;

import java.util.ArrayList;

public class PierceHit implements HitEffectStrategy {
    private int pierceNumber;
    private ArrayList<Zombie> hitZombies;
    public PierceHit(int pierceNumber) {
        this.pierceNumber = pierceNumber;
        hitZombies = new ArrayList<>();
    }
    @Override
    public void apply(Zombie zombie) {

    }
}
