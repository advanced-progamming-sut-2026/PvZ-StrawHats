package model.collections.zombie.zombie_effect;

import model.collections.Faction;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.pitches.Cell;
import model.utils.GameSession;

public class FireEffect implements ZombieEffectStatus {
    private boolean activeFlame = true;
    private final double blastRadius;

    public FireEffect(double blastRadius) {
        this.blastRadius = blastRadius;
    }

    public boolean isActiveFlame() { return activeFlame; }
    public void setActiveFlame(boolean activeFlame) { this.activeFlame = activeFlame; }

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (target == null || !target.isAlive() || !activeFlame || target.getPosition() == null) {
            return;
        }

        int targetRow = (int) target.getPosition().y();
        double currentX = target.getPosition().x();
        int baseGridCol = (int) Math.floor(currentX);
        int frontGridCol = baseGridCol - 1;

        if (target.getFaction() == Faction.ZOMBIES) {
            int totalCols = session.getLawn().getCols();
            if (frontGridCol >= 0 && frontGridCol < totalCols) {
                applySearingDamage(target, session, targetRow, frontGridCol, currentX);
            }
            if (baseGridCol >= 0 && baseGridCol < totalCols) {
                applySearingDamage(target, session, targetRow, baseGridCol, currentX);
            }
        } else {
            session.getZombies().stream()
                    .filter(z -> z.isAlive() && z.getFaction() == Faction.ZOMBIES && (int) z.getPosition().y() == targetRow)
                    .forEach(z -> {
                        double separation = z.getPosition().x() - currentX;
                        if (separation >= 0 && separation <= blastRadius) {
                            z.takeDamage(z.getHp());
                        }
                    });
        }
    }

    private void applySearingDamage(Zombie source, GameSession session, int r, int c, double originX) {
        Cell cell = session.getLawn().getCell(r, c);
        if (cell != null && cell.getPlant() != null && cell.getPlant().isAlive()) {
            Plant vegetation = cell.getPlant();
            if (vegetation.getLocation() != null) {
                double gap = originX - vegetation.getLocation().x();
                if (gap >= 0 && gap <= blastRadius) {
                    vegetation.takeDamage(vegetation.getHP(), source);
                }
            }
        }
    }
}