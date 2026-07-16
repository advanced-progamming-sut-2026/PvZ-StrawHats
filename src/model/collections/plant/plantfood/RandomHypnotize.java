package model.collections.plant.plantfood;

import model.collections.plant.Plant;
import model.collections.plant.PlantFoodEffect;
import model.collections.zombie.Zombie;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomHypnotize implements PlantFoodEffect {
    private final int targetCount;

    public RandomHypnotize(int targetCount) {
        this.targetCount = Math.max(1, targetCount);
    }

    @Override
    public void triggerSuperpower(Plant plant, GameSession session) {
        List<Zombie> candidates = new ArrayList<>();
        for (Zombie zombie : session.getZombies()) {
            if (zombie != null && zombie.isAlive() && !zombie.isHypnotized()) {
                candidates.add(zombie);
            }
        }
        Collections.shuffle(candidates);
        for (int i = 0; i < Math.min(targetCount, candidates.size()); i++) {
            candidates.get(i).hypnotize();
        }
    }

    @Override
    public void tickDurationEffect(Plant plant, double deltaTimeSeconds) {
    }

    @Override
    public void applyStatusModifiers(Plant plant) {
    }
}
