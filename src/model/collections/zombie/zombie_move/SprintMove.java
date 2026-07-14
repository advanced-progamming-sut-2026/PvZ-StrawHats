package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import service.GameClock;
import util.GameSession;

public class SprintMove implements MoveBehavior {
    private final double baseSprintMultiplier;
    private final double enrageMultiplier;
    private final boolean enragesOnArmorLoss;

    public SprintMove() {
        this(1.0, 1.0, false);
    }

    public SprintMove(double baseSprintMultiplier) {
        this(baseSprintMultiplier, 1.0, false);
    }

    public SprintMove(double baseSprintMultiplier, double enrageMultiplier, boolean enragesOnArmorLoss) {
        this.baseSprintMultiplier = baseSprintMultiplier;
        this.enrageMultiplier = enrageMultiplier;
        this.enragesOnArmorLoss = enragesOnArmorLoss;
    }

    @Override
    public void move(Zombie zombie, GameSession session) {
        Position pos = zombie.getPosition();
        if (pos == null) return;

        double deltaX = getActiveSpeedX(zombie) * GameClock.SECONDS_PER_TICK;
        zombie.setPosition(Position.of(pos.x() + deltaX, pos.y()));

        if (zombie.getPosition().x() < 0) {
            session.onZombieReachedEnd();
        }
    }

    protected double getActiveSpeedX(Zombie zombie) {
        if (enragesOnArmorLoss && (zombie.getArmor() == null || zombie.getArmor().isDestroyed())) {
            return zombie.getSpeed().x() * enrageMultiplier;
        }
        return zombie.getSpeed().x() * baseSprintMultiplier;
    }
}
