package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import util.GameSession;

public class HypnotizedMove implements MoveBehavior {

    private final MoveBehavior delegate;

    public HypnotizedMove(MoveBehavior delegate) {
        this.delegate = delegate;
    }

    @Override
    public void move(Zombie zombie, GameSession session) {
        delegate.move(zombie, session);

        if (hasWalkedOffTheRightEdge(zombie, session)) {
            zombie.setAlive(false);
        }
    }

    private boolean hasWalkedOffTheRightEdge(Zombie zombie, GameSession session) {
        return zombie.getPosition() != null
                && session.getLawn() != null
                && zombie.getPosition().x() >= session.getLawn().getCols();
    }
}
