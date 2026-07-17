package model.match.mini_games.wallnutbowlling.nut;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

/**
 * Rolls in a straight line; each zombie it hits takes damage equal to a
 * normal zombie's health. The first hit turns it 45 degrees, every hit
 * after that turns it 90 degrees. It also turns when it hits the top/bottom.
 */
public class BowlingWallnut extends Nut {
    private static final int NORMAL_ZOMBIE_HP = 300; // matches ZombieDefault's Hitpoints
    private int hitsSoFar = 0;

    public BowlingWallnut(Position position, Position direction) {
        super(position, direction);
    }

    @Override
    public boolean onHitZombie(Zombie zombie, GameSession session) {
        zombie.takeDamage(NORMAL_ZOMBIE_HP, this);
        turnAfterHit();
        return false; // keeps rolling after impact
    }

    private void turnAfterHit() {
        double angleDegrees = (hitsSoFar == 0) ? 45 : 90;
        hitsSoFar++;
        double radians = Math.toRadians(angleDegrees);
        double newX = direction.x() * Math.cos(radians) - direction.y() * Math.sin(radians);
        double newY = direction.x() * Math.sin(radians) + direction.y() * Math.cos(radians);
        direction = new Position(newX, newY);
    }
}
