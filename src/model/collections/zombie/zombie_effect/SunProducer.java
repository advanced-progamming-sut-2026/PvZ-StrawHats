package model.collections.zombie.zombie_effect;

import model.collections.item.GroundSun;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;
import service.GameClock;

public class SunProducer implements ZombieEffectStatus {
    private static final int SUN_VALUE = GroundSun.SunDropType.REGULAR.getValue();

    private double activeGenerationRate = 10.0;
    private final double floorIntervalRate = 2.0;
    private final double speedMultiplierStep = 0.5;
    private double productionTimer = activeGenerationRate;

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (target == null || !target.isAlive() || target.getPosition() == null) return;

        productionTimer -= GameClock.SECONDS_PER_TICK;
        if (productionTimer <= 0) {
            Position dropPosition = target.getPosition();
            session.getItems().add(new GroundSun(dropPosition, SUN_VALUE));

            if (activeGenerationRate > floorIntervalRate) {
                activeGenerationRate -= speedMultiplierStep;
                if (activeGenerationRate < floorIntervalRate) {
                    activeGenerationRate = floorIntervalRate;
                }
            }
            productionTimer = activeGenerationRate;
        }
    }
}
