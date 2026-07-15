package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import model.utils.GameSession;

public class StunnedMoveBehavior implements MoveBehavior {
    private final MoveBehavior restoredBehavior;
    private double stunTimer;

    public StunnedMoveBehavior(MoveBehavior restoredBehavior, double stunDuration) {
        this.restoredBehavior = restoredBehavior;
        this.stunTimer = stunDuration;
    }

    @Override
    public void move(Zombie zombie, double deltaTime, GameSession session) {
        stunTimer -= deltaTime;

        if (stunTimer <= 0) {
            zombie.setMoveBehavior(restoredBehavior);
        }
    }
}