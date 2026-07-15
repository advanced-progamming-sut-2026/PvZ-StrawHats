package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import util.GameSession;

public class HypnotizedMoveBehavior implements MoveBehavior {
    private final MoveBehavior baseMovement;

    public HypnotizedMoveBehavior(MoveBehavior baseMovement) {
        this.baseMovement = baseMovement;
    }

    @Override
    public void move(Zombie zombie, double deltaTime, GameSession session) {
        if (baseMovement != null) {
            baseMovement.move(zombie, deltaTime, session);
        }

        if (hasWalkedOffRightBoundary(zombie, session)) {
            zombie.setHp(0);
        }
    }

    private boolean hasWalkedOffRightBoundary(Zombie zombie, GameSession session) {
        return zombie.getPosition() != null
                && session.getLawn() != null
                && zombie.getPosition().x() >= session.getLawn().getCols();
    }
}