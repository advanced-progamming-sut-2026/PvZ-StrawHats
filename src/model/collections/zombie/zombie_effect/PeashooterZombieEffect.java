package model.collections.zombie.zombie_effect;

import model.collections.zombie.Zombie;
import model.projectile.zombie_projectile.ZombiePeaProjectile;
import service.GameClock;
import model.utils.GameSession;

public class PeashooterZombieEffect implements ZombieEffectStatus {
    private double firingClock = 0.0;
    private final double shotInterval;
    private final int projectileDamage;

    public PeashooterZombieEffect(int projectileDamage, double shotInterval) {
        this.projectileDamage = projectileDamage;
        this.shotInterval = shotInterval;
    }

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (!target.isAlive() || target.getPosition() == null) return;

        firingClock += GameClock.SECONDS_PER_TICK;
        if (firingClock >= shotInterval) {
            firingClock = 0;
            session.addZombieProjectile(new ZombiePeaProjectile(target.getPosition(), projectileDamage));
        }
    }
}
