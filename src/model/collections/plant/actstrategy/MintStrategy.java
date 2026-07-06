package model.collections.plant.actstrategy;

import model.collections.plant.Plant;
import util.GameSession;

public class MintStrategy implements ActStrategy {
    @Override
    public void act(Plant user, GameSession session) {
        session.getPlants().stream()
                .filter(plant -> plant != null && plant.isAlive() && plant.getType() == user.getType())
                .forEach(plant -> {
                    if (plant.getPlantFoodEffect() != null) {
                        plant.getPlantFoodEffect().applyStatusModifiers(plant);
                        //todo check and update the triggers
                        plant.getPlantFoodEffect().triggerSuperpower(plant, session);
                    }
                });

        user.setAlive(false);
    }
}
