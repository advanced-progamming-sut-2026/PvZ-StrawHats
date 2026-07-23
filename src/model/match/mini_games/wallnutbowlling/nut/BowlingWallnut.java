package model.match.mini_games.wallnutbowlling.nut;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

public class BowlingWallnut extends Nut {
    private static final int NORMAL_ZOMBIE_DAMAGE = 190;
    private int hitsSoFar = 0;

    public BowlingWallnut(Position position, Position direction) {
        super(position, direction);
    }

    @Override
    public boolean onHitZombie(Zombie zombie, GameSession session) {
        zombie.takeDamage(NORMAL_ZOMBIE_DAMAGE, this);
        turnAfterHit();
        return false; // keeps rolling after impact
    }

    @Override
    public String getKindName() {
        return "Bowling Wall-nut";
    }

    public int getHitsSoFar() {
        return hitsSoFar;
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
