package model.projectile.zombie_projectile;

import model.collections.plant.Plant;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.utils.GameSession;

public class OctopusProjectile extends ZombieProjectile {

    private final double arcHeight = 2.5;

    public OctopusProjectile(Position startPosition, Position targetPosition, double flightTime, GameSession session) {
        super(startPosition, targetPosition, flightTime, "OctopusZombie", session);
    }

    @Override
    protected void updateFlightPath(double progress) {
        double currentX = startPosition.x() + (targetPosition.x() - startPosition.x()) * progress;
        double currentY = startPosition.y() + (targetPosition.y() - startPosition.y()) * progress;

        double visualY = currentY - (arcHeight * Math.sin(progress * Math.PI));
        this.setPosition(new Position(currentX, visualY));
    }

    @Override
    protected void onDestinationReached(GameSession session) {
        if (session == null || session.getEnvironment() == null) return;

        int targetRow = (int) targetPosition.y();
        int targetCol = (int) targetPosition.x();

        Cell targetCell = session.getEnvironment().getCell(targetRow, targetCol);

        if (targetCell != null && targetCell.getPlant() != null && targetCell.getPlant().isAlive()) {
            Plant targetPlant = targetCell.getPlant();

            if (!isPlantIncapacitated(targetPlant)) {
                targetCell.setObstacle(new OctopusWrap(targetPlant, 800));
                targetPlant.setState(Plant.PlantState.INCAPACITATED);
            }
        }
    }
}