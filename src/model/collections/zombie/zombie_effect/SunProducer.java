package model.collections.zombie.zombie_effect;

import model.collections.zombie.Zombie;
import model.match_mechanisms.sun.Sun;
import model.match_mechanisms.sun.SunFactory;
import service.GameClock;
import util.GameSession;

public class SunProducer implements ZombieEffectStatus {
    private double activeGenerationRate = 10.0;
    private final double floorIntervalRate = 2.0;
    private final double speedMultiplierStep = 0.5;
    private double productionTimer = activeGenerationRate;

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (target == null || !target.isAlive() || target.getPosition() == null) return;

        productionTimer -= GameClock.SECONDS_PER_TICK;
        if (productionTimer <= 0) {
            int targetRow = (int) target.getPosition().y();
            int targetCol = (int) target.getPosition().x();

            SunFactory factory = new SunFactory();
            Sun newSun = factory.createPlantSun(targetCol, targetRow);

            session.addSun(1);
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