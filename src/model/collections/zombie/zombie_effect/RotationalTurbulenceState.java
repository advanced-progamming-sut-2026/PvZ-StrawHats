package model.collections.zombie.zombie_effect;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;
import service.GameClock;

public class RotationalTurbulenceState implements ZombieEffectStatus {
    private static final double DEFAULT_GYRATION_DURATION = 1.0;
    private static final double VELOCITY_BOOST_FACTOR = 3.0;

    private double durationRemaining = 0.0;
    private boolean hasSpeedBuffActive = false;

    public void triggerGyratingState(double duration) {
        this.durationRemaining = Math.max(this.durationRemaining, duration);
    }

    public void triggerGyratingState() {
        triggerGyratingState(DEFAULT_GYRATION_DURATION);
    }

    public boolean isActivelyGyrating() {
        return durationRemaining > 0;
    }

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (target == null) return;

        if (!target.isAlive()) {
            if (hasSpeedBuffActive) {
                restoreNormalVelocity(target);
                hasSpeedBuffActive = false;
            }
            return;
        }

        boolean shouldBeBoosted = durationRemaining > 0;

        if (shouldBeBoosted && !hasSpeedBuffActive) {
            escalateVelocity(target);
            hasSpeedBuffActive = true;
        } else if (!shouldBeBoosted && hasSpeedBuffActive) {
            restoreNormalVelocity(target);
            hasSpeedBuffActive = false;
        }
        if (durationRemaining > 0) {
            durationRemaining -= GameClock.SECONDS_PER_TICK;
        }
    }

    private void escalateVelocity(Zombie target) {
       Position currentSpeed = target.getSpeed();
        if (currentSpeed == null) return;
        target.setSpeed(currentSpeed.scale(VELOCITY_BOOST_FACTOR));
    }

    private void restoreNormalVelocity(Zombie target) {
        Position currentSpeed = target.getSpeed();
        if (currentSpeed == null) return;
        target.setSpeed(currentSpeed.scale(1.0 / VELOCITY_BOOST_FACTOR));
    }
}