package model.projectile;

import model.match_mechanisms.vector.Position;
import service.GameClock;

public class ArcMove implements MoveStrategy {
    private final double gravity;

    // e.g., gravity = 9.8 (You may need to tweak this to match your grid scale)
    public ArcMove(double gravity) {
        this.gravity = gravity;
    }


    @Override
    public void move(Projectile projectile) {
        Position pos = projectile.getPosition();
        Position speed = projectile.getSpeed();

        if (pos != null && speed != null) {
            // Apply gravity to vertical speed (assuming +Y goes down your screen)
            double newSpeedY = speed.y() + (gravity * GameClock.SECONDS_PER_TICK);
            Position newSpeed = Position.of(speed.x(), newSpeedY);

            // Update position based on the new speed
            Position newPos = pos.add(newSpeed.scale(GameClock.SECONDS_PER_TICK));

            projectile.setSpeed(newSpeed);
            projectile.setPosition(newPos);
        }
    }
}
