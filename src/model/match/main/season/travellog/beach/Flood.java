package model.match.main.season.travellog.beach;

import model.collections.plant.Plant;
import model.collections.plant.PlantTag;
import model.collections.zombie.VulnerabilityType;
import model.collections.zombie.Zombie;
import model.collections.zombie.zombie_move.SnorkelMove;
import model.match.main.levels.Level;
import model.pitches.Cell;
import model.pitches.Tile;
import model.pitches.TileType;
import model.utils.GameSession;

public class Flood {
    public static void initialize(Level level, GameSession session) {
        if (level == null || session == null) return;
        if (level.getCurrentTideColumn() <= 0) level.setCurrentTideColumn(level.getMaxTideColumn());
        apply(level, session);
    }

    public static void riselevel(Level level, GameSession session) {
        if (level == null) return;
        level.setCurrentTideColumn(Math.min(level.getCurrentTideColumn() + 1, level.getMaxTideColumn()));
        apply(level, session);
    }

    public static void falllevel(Level level, GameSession session) {
        if (level == null) return;
        level.setCurrentTideColumn(Math.max(level.getCurrentTideColumn() - 1, 0));
        apply(level, session);
    }

    public static void riselevel(Level level) { riselevel(level, GameSession.peekInstance()); }
    public static void falllevel(Level level) { falllevel(level, GameSession.peekInstance()); }

    public static void apply(Level level, GameSession session) {
        if (level == null || session == null || session.getEnvironment() == null) return;
        int waterStart = session.getCols() - level.getCurrentTideColumn();

        for (int row = 0; row < session.getRows(); row++) {
            for (int col = 0; col < session.getCols(); col++) {
                Cell cell = session.getEnvironment().getCell(row, col);
                if (cell == null) continue;
                boolean flooded = col >= waterStart;
                if (flooded) {
                    if (cell.getTile() == null || cell.getTile().type() != TileType.Slippery) {
                        cell.setTile(new Tile(TileType.Water));
                    }
                    Plant top = cell.getPlant();
                    if (top != null && top.isAlive() && !isWaterSafe(top)) top.setAlive(false);
                } else if (cell.getTile() != null && cell.getTile().type() == TileType.Water) {
                    cell.setTile(new Tile(TileType.Normal));
                }
            }
        }

        for (Zombie zombie : session.getZombies()) {
            if (zombie == null || !zombie.isAlive() || zombie.getPosition() == null) continue;
            boolean flooded = zombie.getPosition().x() >= waterStart;
            if (zombie.getMoveBehavior() instanceof SnorkelMove && flooded
                    && zombie.getZombieState() != model.collections.zombie.ZombieState.EATING) {
                zombie.setVulnerabilityState(VulnerabilityType.SUBMERGED);
            } else if (zombie.getVulnerabilityState() == VulnerabilityType.SUBMERGED) {
                zombie.setVulnerabilityState(VulnerabilityType.FULLY_VULNERABLE);
            }
        }
    }

    private static boolean isWaterSafe(Plant plant) {
        if (plant.getTags().contains(PlantTag.WATER)) return true;
        Plant bottom = plant.getBottom();
        return bottom != null && bottom.isAlive() && bottom.getTags().contains(PlantTag.WATER);
    }
}
