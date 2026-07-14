package model.collections.zombie.zombie_move;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.pitches.Square;
import service.GameClock;
import util.GameSession;

public class JumpMove implements MoveBehavior {

    @Override
    public void move(Zombie zombie, GameSession session) {
        Position pos = zombie.getPosition();
        if (pos == null) return;

        Position vel = zombie.getSpeed();
        int currentRow = (int) pos.y();

        int lookAheadCol = (int) (pos.x() - 0.5);

        Square aheadCell = session.getLawn().getCell(currentRow, lookAheadCol);
        Plant obstaclePlant = (aheadCell != null) ? aheadCell.getPlant() : null;

        if (obstaclePlant != null && obstaclePlant.isAlive()) {
            double leapX = pos.x() - 1.2;
            zombie.setPosition(Position.of(leapX, pos.y()));
        } else {
            Position newPos = pos.add(vel.scale(GameClock.SECONDS_PER_TICK));
            zombie.setPosition(newPos);
        }

        if (zombie.getPosition().x() < 0) {
            session.onZombieReachedEnd();
        }
    }
}
