package model.collections.plant.plantfood;

import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantFoodEffect;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

public class SpawnClones implements PlantFoodEffect {
    private final int count;

    public SpawnClones(int count) {
        this.count = Math.max(1, count);
    }

    @Override
    public void triggerSuperpower(Plant plant, GameSession session) {
        Position position = plant.getPosition();
        if (position == null) return;

        int row = (int) position.y();
        int col = (int) position.x();
        int spawned = 0;

        for (int offset = 1; offset <= session.getCols() && spawned < count; offset++) {
            spawned += trySpawnAt(session, plant, row, col + offset);
            if (spawned >= count) break;
            spawned += trySpawnAt(session, plant, row, col - offset);
        }
    }

    private int trySpawnAt(GameSession session, Plant plant, int row, int col) {
        if (row < 0 || row >= session.getRows() || col < 0 || col >= session.getCols()) return 0;
        if (session.getEnvironment().getCell(row, col) == null
                || session.getEnvironment().getCell(row, col).hasPlant()) return 0;

        Plant clone = PlantFactory.createPlant(plant.getId(), plant.getLevel(), new Position(col, row));
        return session.plantAt(row, col, clone) ? 1 : 0;
    }

    @Override
    public void tickDurationEffect(Plant plant, double deltaTimeSeconds) {
    }

    @Override
    public void applyStatusModifiers(Plant plant) {
    }
}
