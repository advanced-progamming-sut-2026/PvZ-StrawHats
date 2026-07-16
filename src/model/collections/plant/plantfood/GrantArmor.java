package model.collections.plant.plantfood;

import model.collections.armour.ArmourFactory;
import model.collections.armour.ArmourType;
import model.collections.armour.PlantArmour;
import model.collections.plant.Plant;
import model.collections.plant.PlantFoodEffect;
import model.utils.GameSession;

public class GrantArmor implements PlantFoodEffect {
    private final int hp;

    public GrantArmor(int hp) {
        this.hp = hp;
    }

    @Override
    public void triggerSuperpower(Plant plant, GameSession session) {
    }

    @Override
    public void tickDurationEffect(Plant plant, double deltaTimeSeconds) {
    }

    @Override
    public void applyStatusModifiers(Plant plant) {
        if (plant.getArmor() == null) {
            plant.setArmor((PlantArmour) ArmourFactory.createArmour(ArmourType.PLANT_SHIELD, hp, 0, false));
        }
    }
}
