package model.match.mini_games.wallnutbowlling.nut;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

public abstract class Nut {
    protected static final double SPEED = 3.0; // columns per second

    protected Position position;
    protected Position direction; // unit-ish vector, e.g. (-1, 0) rolling left
    protected boolean alive = true;

    protected Nut(Position position, Position direction) {
        this.position = position;
        this.direction = direction;
    }

    public Position getPosition() { return position; }
    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public void move(double deltaSeconds) {
        if (!alive) return;
        position = position.add(direction.scale(SPEED * deltaSeconds));
    }

    public void bounceVertical() {
        direction = new Position(direction.x(), -direction.y());
    }

    /**
     * Called when this nut touches a zombie. Returns true if the nut is
     * consumed by the hit (should be removed from play).
     */
    public abstract boolean onHitZombie(Zombie zombie, GameSession session);
}
