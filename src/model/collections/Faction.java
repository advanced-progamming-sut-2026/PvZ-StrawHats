package model.collections;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.utils.GameSession;

public enum Faction {
    ZOMBIES,
    PLANTS;

    public Item findTarget(Zombie self, GameSession session) {
        return switch (this) {
            case ZOMBIES -> findNearestPlantAhead(self, session);
            case PLANTS -> findNearestEnemyZombieAhead(self, session);
        };
    }

    private Item findNearestPlantAhead(Zombie self, GameSession session) {
        if (session.getPlants() == null) return null;
        double row = self.getPosition().y();
        double col = self.getPosition().x();
        Plant nearest = null;
        double bestDist = Double.MAX_VALUE;
        for (Plant plant : session.getPlants()) {
            if (plant == null || !plant.isAlive()) continue;
            if (Math.abs(plant.getLocation().y() - row) > 0.5) continue;
            double dist = col - plant.getLocation().x();
            if (dist >= 0 && dist < bestDist) {
                bestDist = dist;
                nearest = plant;
            }
        }
        return nearest;
    }

    private Item findNearestEnemyZombieAhead(Zombie self, GameSession session) {
        if (session.getZombies() == null) return null;
        double row = self.getPosition().y();
        double col = self.getPosition().x();
        Zombie nearest = null;
        double bestDist = Double.MAX_VALUE;
        for (Zombie zombie : session.getZombies()) {
            if (zombie == null || zombie == self || !zombie.isAlive()) continue;
            if (zombie.getFaction() != ZOMBIES) continue;
            if (Math.abs(zombie.getPosition().y() - row) > 0.5) continue;
            double dist = Math.abs(zombie.getPosition().x() - col);
            if (dist < bestDist) {
                bestDist = dist;
                nearest = zombie;
            }
        }
        return nearest;
    }
}
