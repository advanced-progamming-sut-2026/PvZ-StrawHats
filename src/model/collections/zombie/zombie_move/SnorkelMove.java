package model.collections.zombie.zombie_move;

import model.collections.zombie.VulnerabilityType;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieState;
import model.match_mechanisms.vector.Position;
import service.GameClock;
import util.GameSession;

public class SnorkelMove implements MoveBehavior {
    private static final double WATER_START_X = 2.0;
    private static final double WATER_END_X = 7.0;

    @Override
    public void move(Zombie zombie, GameSession session) {
        Position pos = zombie.getPosition();
        if (pos == null) return;

        double deltaX = zombie.getSpeed().x() * GameClock.SECONDS_PER_TICK;
        double targetX = pos.x() + deltaX;
        zombie.setPosition(Position.of(targetX, pos.y()));

        boolean inWaterSection = (targetX >= WATER_START_X && targetX <= WATER_END_X);
        boolean isEating = zombie.getZombieState().equals(ZombieState.EATING);

        if (inWaterSection && !isEating) {
            // Set tactical rule state to handle Lobber check bypass
            zombie.setVulnerabilityState(VulnerabilityType.SUBMERGED);
        } else {
            // Surface unit when outside pool lanes or actively chewing a plant
            zombie.setVulnerabilityState(VulnerabilityType.FULLY_VULNERABLE);
        }

        if (zombie.getPosition().x() < 0) {
            session.onZombieReachedEnd();
        }
    }
}
