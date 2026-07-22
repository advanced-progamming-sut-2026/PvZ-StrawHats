package model.collections.zombie.zombie_attack;

import model.collections.Faction;
import model.collections.Item;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.pitches.Cell;
import model.pitches.obstacles.Obstacle;
import model.utils.GameSession;

import java.util.Comparator;

public class ZombieTargeting {

    private static final double EATING_RANGE = 0.5;

    public static Item findTarget(Zombie self, GameSession session) {
        if (self == null || !self.isAlive() || session == null) {
            return null;
        }

        if (self.getFaction() == Faction.ZOMBIES) {
            return findPlantSideTarget(self, session);
        } else {
            return findZombieSideTarget(self, session);
        }
    }

    private static Cell getZombieCell(Zombie zombie, GameSession session) {
        if (zombie == null || zombie.getPosition() == null || session == null || session.getEnvironment() == null) {
            return null;
        }
        int col = (int) Math.round(zombie.getPosition().x());
        int row = (int) Math.round(zombie.getPosition().y());
        return session.getEnvironment().getCell(row, col);
    }

    private static boolean isPlantIncapacitated(Plant plant) {
        return plant != null && plant.getPlantState() == Plant.PlantState.INCAPACITATED;
    }

    private static Item findPlantSideTarget(Zombie self, GameSession session) {
        Cell cell = getZombieCell(self, session);
        if (cell == null) return null;

        Obstacle obstacle = cell.getObstacle();
        if (obstacle instanceof Item item && item.isAlive()) {
            return item;
        }

        Plant plant = cell.getPlant();
        if (plant != null && plant.isAlive() && !isPlantIncapacitated(plant)) {
            return plant;
        }

        return null;
    }

    private static Item findZombieSideTarget(Zombie self, GameSession session) {
        Cell cell = getZombieCell(self, session);
        if (cell != null) {
            Obstacle obstacle = cell.getObstacle();
            if (obstacle instanceof Item item && item.isAlive()) {
                return item;
            }
        }

        if (self.getPosition() == null || session.getZombies() == null) {
            return null;
        }

        int myRow = (int) self.getPosition().y();
        double myX = self.getPosition().x();

        return session.getZombies().stream()
                .filter(other -> other != self && other.isAlive())
                .filter(other -> other.getFaction() == Faction.ZOMBIES)
                .filter(other -> other.getPosition() != null)
                .filter(other -> (int) other.getPosition().y() == myRow)
                .filter(other -> isInEatingRange(myX, other.getPosition().x()))
                .min(Comparator.comparingDouble(other -> other.getPosition().x() - myX))
                .orElse(null);
    }

    private static boolean isInEatingRange(double myX, double otherX) {
        double dist = otherX - myX;
        return dist >= 0 && dist <= EATING_RANGE;
    }
}