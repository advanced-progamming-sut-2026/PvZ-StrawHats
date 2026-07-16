package model.collections.zombie.zombie_attack;

import model.collections.Item;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.utils.GameSession;

public class KamikazeAttack implements AttackBehavior {
    private final int damage;

    public KamikazeAttack(int damage) {
        this.damage = damage;
    }

    @Override
    public void attack(Zombie zombie, GameSession session) {
        Item target = ZombieTargeting.findTarget(zombie, session);

        if (target != null && target.isAlive()) {
            if (target instanceof Plant p) {
                p.takeDamage(damage, zombie);
            } else {
                target.takeDamage(damage);
            }
            zombie.takeDamage(zombie.getHP());
        }
    }
}