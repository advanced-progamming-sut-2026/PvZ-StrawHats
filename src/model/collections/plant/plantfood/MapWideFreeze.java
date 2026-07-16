package model.collections.plant.plantfood;

import model.collections.plant.Plant;
import model.collections.plant.PlantFoodEffect;
import model.collections.zombie.Zombie;
import model.utils.GameSession;

public class MapWideFreeze implements PlantFoodEffect {
    @Override
    public void triggerSuperpower(Plant plant, GameSession session) {
        for (Zombie zombie : session.getZombies()) {
            if (zombie != null && zombie.isAlive()) {
                zombie.setStatus(Zombie.Status.FREEZE);
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
