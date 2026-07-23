package model.match.mini_games.wallnutbowlling.nut;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

public abstract class Nut {
    protected static final double SPEED = 3.0; // columns per second

    protected Position position;
    protected Position direction; // unit-ish vector, e.g. (1, 0) rolling toward the zombies
    protected boolean alive = true;

    protected Nut(Position position, Position direction) {
        this.position = position;
        this.direction = direction;
    }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    public String getKindName() {
        return getClass().getSimpleName();
    }

    public void move(double deltaSeconds) {
        if (!alive) return;
        position = position.add(direction.scale(SPEED * deltaSeconds));
    }

    public void bounceVertical() {
        direction = new Position(direction.x(), -direction.y());
    }

    public abstract boolean onHitZombie(Zombie zombie, GameSession session);
}
