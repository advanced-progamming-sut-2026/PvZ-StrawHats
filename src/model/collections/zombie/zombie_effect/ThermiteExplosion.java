package model.collections.zombie.zombie_effect;

import model.collections.Faction;
import model.collections.zombie.Zombie;
import model.pitches.Cell;
import service.GameClock;
import model.utils.GameSession;

public class ThermiteExplosion implements ZombieEffectStatus {
    private double fuseElapsed = 0.0;
    private final double combustionDelay = 10.0;

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (!target.isAlive() || target.getPosition() == null) return;

        fuseElapsed += GameClock.SECONDS_PER_TICK;

        if (fuseElapsed >= combustionDelay) {
            int rowIdx = (int) target.getPosition().y();

            if (target.getFaction() == Faction.ZOMBIES) {
                int totalCols = session.getLawn().getCols();
                for (int c = 0; c < totalCols; c++) {
                    Cell gridCell = session.getLawn().getCell(rowIdx, c);
                    if (gridCell != null && gridCell.getPlant() != null && gridCell.getPlant().isAlive()) {
                        gridCell.getPlant().takeDamage(99999, target);
                    }
                }
            } else {
                session.getZombies().stream()
                        .filter(z -> z.isAlive() && z.getFaction() == Faction.ZOMBIES && (int) z.getPosition().y() == rowIdx)
                        .forEach(z -> z.takeDamage(z.getHp()));
            }

            target.takeDamage(target.getHp(), false);
        }
    }
}
