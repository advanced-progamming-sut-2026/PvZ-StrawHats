package model.match.mini_games.wallnutbowlling.nut;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

/**
 * The third bowling plant per spec ("گردوی بزرگ"): rolls dead straight and
 * instantly crushes any zombie it touches without slowing down or turning.
 */
public class BigNut extends Nut {
    public BigNut(Position position, Position direction) {
        super(position, direction);
    }

    @Override
    public boolean onHitZombie(Zombie zombie, GameSession session) {
        zombie.setHp(0);
        return false; // keeps rolling straight through
    }
}
