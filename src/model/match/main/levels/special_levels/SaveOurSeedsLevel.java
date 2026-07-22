package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.match_mechanisms.vector.Position;
import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.pitches.Cell;
import model.utils.GameSession;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SaveOurSeedsLevel extends Level {
    private Map<Position, String> seedPositions; // (row, col) → plant type
    private final Set<Position> confirmedPlanted = new HashSet<>();

    @Override
    public void initSpecial(GameSession session) {
        confirmedPlanted.clear();
        if (session == null || seedPositions == null) return;

        for (Map.Entry<Position, String> entry : seedPositions.entrySet()) {
            Position position = entry.getKey();
            Integer plantId = PlantFactory.getBlueprints().values().stream()
                    .filter(config -> config.name.equalsIgnoreCase(entry.getValue()))
                    .map(config -> config.id)
                    .findFirst()
                    .orElse(null);
            if (plantId == null) {
                throw new IllegalStateException("Unknown guarded plant: " + entry.getValue());
            }

            Plant plant = PlantFactory.createPlant(plantId, 1, position);
            if (!session.plantAt((int) position.y(), (int) position.x(), plant)) {
                throw new IllegalStateException("Could not place guarded plant at " + position);
            }
            confirmedPlanted.add(position);
        }
    }

    /**
     * Instant loss the moment a zombie manages to eat one of the guarded plants.
     * note that a seed position only counts once we've actually seen a plant sitting there
     * (so this doesn't fire before the level's initial plants have been placed).
     */
    @Override
    public boolean checkLossCondition(GameSession session) {
        if (seedPositions == null) return false;

        for (Position pos : seedPositions.keySet()) {
            Cell cell = session.getEnvironment().getCell((int) pos.y(), (int) pos.x());
            boolean hasPlant = cell != null && cell.hasPlant();

            if (hasPlant)
                confirmedPlanted.add(pos);
            else if (confirmedPlanted.contains(pos))
                return true;

        }
        return false;
    }

    public Map<Position, String> getSeedPositions() { return seedPositions; }
    public void setSeedPositions(Map<Position, String> seedPositions) { this.seedPositions = seedPositions; }
}
