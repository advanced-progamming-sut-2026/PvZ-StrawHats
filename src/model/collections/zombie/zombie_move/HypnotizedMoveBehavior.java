package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import util.GameSession;

public class HypnotizedMoveBehavior implements MoveBehavior {
    private final MoveBehavior original;

    public HypnotizedMoveBehavior(MoveBehavior original) {
        this.original = original;
    }

    @Override
    public void move(Zombie zombie, double deltaTimeSeconds, GameSession session) {
        if (zombie.getSpeed() != null) {
            zombie.move(deltaTimeSeconds);
        }
    }
}