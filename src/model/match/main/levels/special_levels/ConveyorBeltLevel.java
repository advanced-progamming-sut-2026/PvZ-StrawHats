package model.match.main.levels.special_levels;

import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.match.main.levels.Level;
import model.utils.GameSession;
import service.GameClock;

import java.util.List;
import java.util.Random;

public class ConveyorBeltLevel extends Level {
    private static final double CONVEYOR_INTERVAL_SECONDS = 12.0;
    private static final Random RAND = new Random();

    private List<Plant> conveyorPlants;
    private int maxConveyorSize = 8;

    private double conveyorTimer = 0;
    private Plant currentPlant;

    @Override
    public void initSpecial(GameSession session) {
        conveyorTimer = 0;
        offerNextPlant();
    }

    /**
     * Advances the conveyor clock; call once per tick while the player is in this level.
     */
    public void tickConveyor(double deltaSeconds) {
        if (currentPlant != null) return;

        conveyorTimer += deltaSeconds;
        if (GameClock.hasReached(conveyorTimer, CONVEYOR_INTERVAL_SECONDS)) {
            conveyorTimer = 0;
            offerNextPlant();
        }
    }

    private void offerNextPlant() {
        if (conveyorPlants == null || conveyorPlants.isEmpty()) return;
        Plant template = conveyorPlants.get(RAND.nextInt(conveyorPlants.size()));
        currentPlant = PlantFactory.createPlant(template.getId(), template.getLevel(), template.getPosition());
    }

    public Plant getCurrentPlant() {
        return currentPlant;
    }

    /**
     * Player takes the currently offered plant off the belt to plant it;
     * the belt starts counting down toward the next one.
     */
    public Plant takeCurrentPlant() {
        Plant taken = currentPlant;
        currentPlant = null;
        conveyorTimer = 0;
        return taken;
    }

    public List<Plant> getConveyorPlants() { return conveyorPlants; }
    public void setConveyorPlants(List<Plant> conveyorPlants) { this.conveyorPlants = conveyorPlants; }
    public int getMaxConveyorSize() { return maxConveyorSize; }
    public void setMaxConveyorSize(int maxConveyorSize) { this.maxConveyorSize = maxConveyorSize; }
}
