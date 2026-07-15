package model.collections.zombie.zombie_effect;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

public class GigantorImpChucker implements ZombieEffectStatus {
    private final double launchThresholdPercent;
    private final double projectileArcApex;
    private final double airTravelDuration;
    private final int landingGridCol;
    private final double thresholdSpawnX;
    private final String impCharacterAlias;

    private boolean impLaunched = false;

    public GigantorImpChucker(double launchThreshold, double arcApex, double flightDuration,
                              int targetCol, double limitMinX, String impName) {
        this.launchThresholdPercent = launchThreshold;
        this.projectileArcApex = arcApex;
        this.airTravelDuration = flightDuration;
        this.landingGridCol = targetCol;
        this.thresholdSpawnX = limitMinX;
        this.impCharacterAlias = impName;
    }

    @Override
    public void applyTickEffect(Zombie host, GameSession session) {
        if (impLaunched || !host.isAlive() || host.getMaxHp() <= 0 || host.getPosition() == null) {
            return;
        }

        double healthRatio = (double) host.getHp() / host.getMaxHp();
        if (healthRatio > launchThresholdPercent) return;

        if (thresholdSpawnX > 0 && host.getPosition().x() * 100.0 < thresholdSpawnX) {
            return;
        }

        executeImpThrow(host, session);
        impLaunched = true;
    }

    private void executeImpThrow(Zombie launcher, GameSession session) {
        int trackRow = (int) launcher.getPosition().y();
        if (session.getLawn() == null) return;

        int totalGridCols = session.getLawn().getCols();
        int correctedCol = Math.min(landingGridCol, totalGridCols - 1);

        Position origin = launcher.getPosition();
        Position destination = new Position(correctedCol, trackRow);

        session.addZombieProjectile(new GargantuarImpProjectile(
                origin, destination, airTravelDuration, projectileArcApex, trackRow, impCharacterAlias
        ));
    }
}