package model.match_mechanisms.plant_food;

import model.match_mechanisms.vector.Position;

public class PlantFoodFactory {
    public PlantFood createPlantFood(Position position) {
        return new PlantFood(position);
    }
}
