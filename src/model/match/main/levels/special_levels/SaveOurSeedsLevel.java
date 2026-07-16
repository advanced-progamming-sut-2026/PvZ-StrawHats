package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.utils.GameSession;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SaveOurSeedsLevel extends Level {
    private Map<Position, String> seedPositions; // (row, col) → plant type
    private final Set<Position> confirmedPlanted = new HashSet<>();

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
