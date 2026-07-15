package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.match_mechanisms.vector.Position;
import java.util.Map;

public class SaveOurSeedsLevel extends Level {
    private Map<Position, String> seedPositions; // (row, col) → plant type

    public Map<Position, String> getSeedPositions() { return seedPositions; }
    public void setSeedPositions(Map<Position, String> seedPositions) { this.seedPositions = seedPositions; }
}