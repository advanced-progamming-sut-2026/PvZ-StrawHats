package model.projectile.hit;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;

import java.util.ArrayList;

public class PierceHit implements HitEffectStrategy {
    private int pierceNumber;
    private ArrayList<Zombie> hitZombies;
    public PierceHit(int pierceNumber) {
        this.pierceNumber = pierceNumber;
        hitZombies = new ArrayList<>();
        if (pierceNumber == -1)
            for (Zombie zombie : hitZombies) {
                Position position = new Position(zombie.getPosition().x() + 5f ,zombie.getPosition().y());
                zombie.setPosition(position);
            }
    }
    @Override
    public void apply(Zombie zombie) {

    }
}