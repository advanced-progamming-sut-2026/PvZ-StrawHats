package model.projectile;

import model.match_mechanisms.vector.Position;
import service.GameClock;

public class ArcMove implements MoveStrategy {
    private final double gravity;


    public ArcMove(double gravity) {
        this.gravity = gravity;
    }


    @Override
    public void move(Projectile projectile) {
        Position pos = projectile.getPosition();
        Position speed = projectile.getSpeed();

        if (pos != null && speed != null) {
            // Apply gravity to vertical speed
            double newSpeedY = speed.y() + (gravity * GameClock.SECONDS_PER_TICK);
            Position newSpeed = Position.of(speed.x(), newSpeedY);

            // Update position based on the new speed
            Position newPos = pos.add(newSpeed.scale(GameClock.SECONDS_PER_TICK));

            projectile.setSpeed(newSpeed);
            projectile.setPosition(newPos);
        }
    }
}
