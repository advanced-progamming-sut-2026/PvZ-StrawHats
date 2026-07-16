package model.projectile.zombie_projectile;

import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.utils.GameSession;

public class GargantuarImpProjectile extends ZombieProjectile {

    private final double apex;
    private final int targetRow;
    private final String impAlias;

    public GargantuarImpProjectile(Position startPosition, Position targetPosition, double flightTime,
                                   double apex, int targetRow, String impAlias, GameSession session) {
        super(startPosition, targetPosition, flightTime, "Gargantuar", session);
        this.apex = apex;
        this.targetRow = targetRow;
        this.impAlias = impAlias;
    }

    @Override
    protected void updateFlightPath(double progress) {
        double currentX = startPosition.x() + (targetPosition.x() - startPosition.x()) * progress;
        double currentY = startPosition.y() + (targetPosition.y() - startPosition.y()) * progress;

        double visualY = currentY - (apex / 100.0) * Math.sin(progress * Math.PI);

        this.setPosition(new Position(currentX, visualY));
    }

    @Override
    protected void onDestinationReached(GameSession session) {
        if (session == null || session.getEnvironment() == null) return;

        int targetCol = (int) (targetPosition.x());

        Cell targetCell = session.getEnvironment().getCell(targetRow, targetCol);
        if (targetCell == null) return;

        Zombie imp = ZombieFactory.create(impAlias, targetRow, targetCol);
        session.spawnZombie(imp);
    }
}