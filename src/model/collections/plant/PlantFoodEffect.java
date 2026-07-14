package model.collections.plant;

import util.GameSession;

public interface PlantFoodEffect {
    void triggerSuperpower(Plant plant, GameSession session);
    void tickDurationEffect(Plant plant, double deltaTimeSeconds);
    void applyStatusModifiers(Plant plant);
}
