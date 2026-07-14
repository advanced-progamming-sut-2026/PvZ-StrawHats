package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import util.GameSession;

public interface MoveBehavior {
    void move(Zombie zombie, GameSession session);
}

