package model.match_mechanisms.plant_food;

import model.match_mechanisms.vector.Position;

public class PlantFoodFactory {
    public static PlantFood createPlantFood(Position position) {
        return new PlantFood(position);
    }
}
