package model.collections.plant.plantfood;

import model.collections.plant.Plant;
import model.collections.plant.PlantFoodEffect;
import model.collections.zombie.Zombie;
import model.utils.GameSession;

public class PullUnderWater implements PlantFoodEffect {
    private static final int LETHAL_DAMAGE = 9999;
    private final int maxTargets;

    public PullUnderWater(int maxTargets) {
        this.maxTargets = Math.max(1, maxTargets);
    }

    @Override
    public void triggerSuperpower(Plant plant, GameSession session) {
        double row = plant.getPosition() == null ? -1 : plant.getPosition().y();
        int killed = 0;
        for (Zombie zombie : session.getZombies()) {
            if (killed >= maxTargets) break;
            if (zombie == null || !zombie.isAlive() || zombie.getPosition() == null) continue;
            if (Math.abs(zombie.getPosition().y() - row) < 0.5) {
                zombie.takeDamage(LETHAL_DAMAGE, plant);
                killed++;
            }
        }
    }

    @Override
    public void tickDurationEffect(Plant plant, double deltaTimeSeconds) {
    }

    @Override
    public void applyStatusModifiers(Plant plant) {
    }
}
