package model.collections.zombie.zombie_defense;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.projectile.Projectile;
import model.utils.GameSession;

public class JesterDeflection implements DefenseBehavior {
    public static final double SPIN_PERIOD = 1.0;

    @Override
    public int handleDamage(Zombie zombie, int incomingDamage, Object damageSource, GameSession session) {
        if (damageSource instanceof Projectile projectile) {
            if (isDeflectable(projectile)) {
                activateSpinning(zombie);
                reflectTowardsPlant(zombie, projectile, session);
                return 0;
            }
        }
        return incomingDamage;
    }

    private boolean isDeflectable(Projectile projectile) {
        String typeName = projectile.getClass().getSimpleName().toLowerCase();
        return !typeName.contains("laser") && !typeName.contains("plasma");
    }

    public void activateSpinning(Zombie zombie) {
        if (zombie.getEffectStatus() instanceof SpinEffect spinEffect) {
            spinEffect.startSpin(SPIN_PERIOD);
        }
    }

    private void reflectTowardsPlant(Zombie zombie, Projectile projectile, GameSession session) {
        Plant targetPlant = searchClosestPlantInRow(zombie, session);
        if (targetPlant != null && projectile != null) {
            Position speed = projectile.getSpeed();
            if (speed != null) {
                projectile.setSpeed(new Position(Math.abs(speed.x()), speed.y()));
            }
        }
    }

    public Plant searchClosestPlantInRow(Zombie zombie, GameSession session) {
        Position zombiePos = zombie.getPosition();
        if (zombiePos == null || session == null || session.getPlants() == null) {
            return null;
        }

        double zombieRow = zombiePos.y();
        double zombieCol = zombiePos.x();

        Plant closestPlant = null;
        double minSeparation = Double.MAX_VALUE;

        for (Plant plant : session.getPlants()) {
            if (plant == null || !plant.isAlive() || plant.getPosition() == null) continue;

            if (Math.abs(plant.getPosition().y() - zombieRow) >= 0.5) continue;

            if (plant.getPosition().x() >= zombieCol) continue;

            double separation = zombieCol - plant.getPosition().x();
            if (separation < minSeparation) {
                minSeparation = separation;
                closestPlant = plant;
            }
        }

        return closestPlant;
    }
}