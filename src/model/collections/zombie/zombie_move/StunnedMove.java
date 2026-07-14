package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import service.GameClock;
import util.GameSession;

public class StunnedMove implements MoveBehavior{
    private final MoveBehavior delegate;
    private double stunTimer;

    public StunnedMove(MoveBehavior delegate, double stunDuration) {
        this.delegate = delegate;
        this.stunTimer = stunDuration;
    }

    @Override
    public void move(Zombie zombie, GameSession session) {
        stunTimer -= GameClock.SECONDS_PER_TICK;

        if (stunTimer <= 0) {
            // Restore the original behavior when the stun wears off
            zombie.setMoveBehavior(delegate);
        }
        // By doing nothing else here, the zombie is effectively immobilized
    }


}
