package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import util.GameSession;

public class StationaryMove implements MoveBehavior {
    @Override
    public void move(Zombie zombie, GameSession session) {
        // Intentionally does nothing - the zombie holds its position.
    }
}
