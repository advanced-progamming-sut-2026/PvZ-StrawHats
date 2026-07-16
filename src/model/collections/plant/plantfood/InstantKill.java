package model.collections.plant.plantfood;

import model.collections.plant.Plant;
import model.collections.plant.PlantFoodEffect;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

public class InstantKill implements PlantFoodEffect {
    private static final int LETHAL_DAMAGE = 9999;

    public InstantKill() {
    }

    @Override
    public void triggerSuperpower(Plant plant, GameSession session) {
        Position center = plant.getPosition();
        if (center == null) return;

        Zombie nearest = null;
        double bestDistance = Double.MAX_VALUE;
        for (Zombie zombie : session.getZombies()) {
            if (zombie == null || !zombie.isAlive() || zombie.getPosition() == null) continue;
            double distance = zombie.getPosition().distanceTo(center);
            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = zombie;
            }
        }
        if (nearest != null) {
            nearest.takeDamage(LETHAL_DAMAGE, plant);
        }
    }

    @Override
    public void tickDurationEffect(Plant plant, double deltaTimeSeconds) {
    }

    @Override
    public void applyStatusModifiers(Plant plant) {
    }
}
