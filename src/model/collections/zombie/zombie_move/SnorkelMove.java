package model.collections.zombie.zombie_move;

import model.collections.zombie.VulnerabilityType;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieState;
import model.match_mechanisms.vector.Position;
import util.GameSession;

public class SnorkelMove implements MoveBehavior {

    @Override
    public void move(Zombie zombie, double deltaTime, GameSession session) {
        Position pos = zombie.getPosition();
        if (pos == null || zombie.getSpeed() == null) return;

        double deltaX = zombie.getSpeed().x() * deltaTime;
        double nextX = pos.x() + deltaX;
        zombie.setPosition(new Position(nextX, pos.y()));

        var level = session.getLevel();
        boolean inWaterSection = (level != null) && nextX >= level.getCurrentTideColumn();
        boolean isEating = zombie.getZombieState() == ZombieState.EATING;

        if (inWaterSection && !isEating) {
            zombie.setVulnerabilityState(VulnerabilityType.SUBMERGED);
        } else {
            zombie.setVulnerabilityState(VulnerabilityType.FULLY_VULNERABLE);
        }

        if (zombie.getPosition().x() < 0) {
            session.onZombieReachedEnd();
        }
    }
}