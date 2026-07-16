package model.collections.plant.plantfood;

import model.collections.plant.Plant;
import model.collections.plant.PlantFoodEffect;
import model.utils.GameSession;

public class SpawnSun implements PlantFoodEffect {
    private final int amount;

    public SpawnSun(int amount) {
        this.amount = amount;
    }

    @Override
    public void triggerSuperpower(Plant plant, GameSession session) {
        session.addSun(amount);
    }

    @Override
    public void tickDurationEffect(Plant plant, double deltaTimeSeconds) {
    }

    @Override
    public void applyStatusModifiers(Plant plant) {
    }
}
