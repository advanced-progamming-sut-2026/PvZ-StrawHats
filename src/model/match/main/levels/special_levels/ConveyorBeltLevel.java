package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.collections.plant.Plant;
import java.util.List;

public class ConveyorBeltLevel extends Level {
    private List<Plant> conveyorPlants;
    private int maxConveyorSize = 8;


    public List<Plant> getConveyorPlants() { return conveyorPlants; }
    public void setConveyorPlants(List<Plant> conveyorPlants) { this.conveyorPlants = conveyorPlants; }
    public int getMaxConveyorSize() { return maxConveyorSize; }
    public void setMaxConveyorSize(int maxConveyorSize) { this.maxConveyorSize = maxConveyorSize; }
}