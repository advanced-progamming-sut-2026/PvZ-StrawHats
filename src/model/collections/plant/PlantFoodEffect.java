package model.collections.plant;

import model.utils.GameSession;

public interface PlantFoodEffect {
    void triggerSuperpower(Plant plant, GameSession session);
    void tickDurationEffect(Plant plant, double deltaTimeSeconds);
    void applyStatusModifiers(Plant plant);
    default double getDurationSeconds() { return 0.0; }
    default void reset() {}
}
