package model.match.main.season.travellog.cave;

import model.collections.plant.Plant;
import model.collections.plant.PlantTag;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.Environment;
import model.pitches.obstacles.IceBlock;
import model.utils.GameSession;

public final class FrostbiteFreezing {
    public static final int MAX_CHILL_LEVEL = 3;
    public static final int ICE_BLOCK_HP = 600;
    public static final double ADJACENT_FIRE_MELT_DPS = 60.0;

    private FrostbiteFreezing() { }

    public static boolean canAddChill(Plant plant) {
        return plant != null
                && plant.isAlive()
                && !plant.getTags().contains(PlantTag.FIRE)
                && plant.getChillLevel() < MAX_CHILL_LEVEL;
    }

    public static boolean addChillLevel(GameSession session, Plant plant) {
        if (session == null || session.getEnvironment() == null || !canAddChill(plant)) return false;

        Cell cell = findPlantCell(session.getEnvironment(), plant);
        if (cell == null) return false;

        int newLevel = Math.min(MAX_CHILL_LEVEL, plant.getChillLevel() + 1);
        plant.setChillLevel(newLevel);

        if (newLevel == MAX_CHILL_LEVEL) {
            if (!(cell.getObstacle() instanceof IceBlock iceBlock)
                    || iceBlock.getFrozenPlant() != plant) {
                cell.setObstacle(new IceBlock(plant, ICE_BLOCK_HP));
            }
            plant.setState(Plant.PlantState.INCAPACITATED);
        }
        return true;
    }

    public static boolean damageIce(Cell cell, int damage, boolean fireDamage) {
        if (cell == null || !(cell.getObstacle() instanceof IceBlock iceBlock)) return false;

        boolean destroyed = fireDamage
                ? iceBlock.takeDamage(Integer.MAX_VALUE)
                : iceBlock.takeDamage(Math.max(0, damage));
        if (destroyed) cell.setObstacle(null);
        return destroyed;
    }

    public static void meltFromAdjacentFirePlants(GameSession session, double deltaSeconds) {
        if (session == null || session.getEnvironment() == null || deltaSeconds <= 0) return;
        Environment environment = session.getEnvironment();
        int damage = Math.max(1, (int) Math.round(ADJACENT_FIRE_MELT_DPS * deltaSeconds));

        for (int row = 0; row < environment.getRows(); row++) {
            for (int col = 0; col < environment.getCols(); col++) {
                Cell cell = environment.getCell(row, col);
                if (!(cell.getObstacle() instanceof IceBlock iceBlock)) continue;

                Plant frozenPlant = iceBlock.getFrozenPlant();
                if (frozenPlant == null || !frozenPlant.isAlive()) {
                    cell.setObstacle(null);
                    continue;
                }

                if (hasAdjacentFirePlant(environment, row, col)) {
                    damageIce(cell, damage, false);
                }
            }
        }
    }

    private static boolean hasAdjacentFirePlant(Environment environment, int row, int col) {
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                if (rowOffset == 0 && colOffset == 0) continue;
                Cell neighbour = environment.getCell(row + rowOffset, col + colOffset);
                Plant plant = neighbour == null ? null : neighbour.getPlant();
                if (plant != null && plant.isAlive() && plant.getTags().contains(PlantTag.FIRE)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Cell findPlantCell(Environment environment, Plant plant) {
        Position position = plant.getLocation();
        if (position == null) return null;
        Cell cell = environment.getCell((int) Math.round(position.y()), (int) Math.round(position.x()));
        return cell != null && cell.getPlant() == plant ? cell : null;
    }
}
