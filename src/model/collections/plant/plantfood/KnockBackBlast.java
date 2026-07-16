package model.collections.plant.plantfood;

import model.collections.plant.Plant;
import model.collections.plant.PlantFoodEffect;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

public class KnockBackBlast implements PlantFoodEffect {
    private final int damage;
    private final double pushDistance;

    public KnockBackBlast(int damage, double pushDistance) {
        this.damage = damage;
        this.pushDistance = pushDistance;
    }

    @Override
    public void triggerSuperpower(Plant plant, GameSession session) {
        Position center = plant.getPosition();
        if (center == null) return;
        for (Zombie zombie : session.getZombies()) {
            if (zombie == null || !zombie.isAlive() || zombie.getPosition() == null) continue;
            if (Math.abs(zombie.getPosition().y() - center.y()) < 0.5) {
                zombie.takeDamage(damage, plant);
                Position pos = zombie.getPosition();
                zombie.setPosition(new Position(pos.x() + pushDistance, pos.y()));
            }
        }
    }

    @Override
    public void tickDurationEffect(Plant plant, double deltaTimeSeconds) {
    }

    @Override
    public void applyStatusModifiers(Plant plant) {
    }
}
