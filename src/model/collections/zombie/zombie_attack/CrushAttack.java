package model.collections.zombie.zombie_attack;

import model.collections.Item;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.utils.GameSession;

public class CrushAttack implements AttackBehavior {
    private final int crushDamage;

    public CrushAttack(int crushDamage) {
        this.crushDamage = crushDamage;
    }

    @Override
    public void attack(Zombie zombie, GameSession session) {
        Item target = ZombieTargeting.findTarget(zombie, session);
        if (target == null || !target.isAlive()) return;

        if (target instanceof Plant p) {
            p.takeDamage(crushDamage, zombie);
        } else {
            target.takeDamage(crushDamage);
        }
    }
}