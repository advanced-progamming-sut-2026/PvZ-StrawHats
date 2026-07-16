package model.collections.zombie.zombie_effect;

import model.collections.Faction;
import model.collections.zombie.Zombie;
import model.pitches.Cell;
import service.GameClock;
import model.utils.GameSession;

public class ThermiteExplosion implements ZombieEffectStatus {
    private static final int PLANT_DAMAGE = 99999;

    private final double combustionDelay;
    private double fuseElapsed = 0.0;
    private boolean detonated = false;

    public ThermiteExplosion() {
        this(10.0);
    }

    public ThermiteExplosion(double combustionDelay) {
        this.combustionDelay = combustionDelay;
    }

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (detonated || !target.isAlive() || target.getPosition() == null) return;

        fuseElapsed += GameClock.SECONDS_PER_TICK;

        if (fuseElapsed >= combustionDelay) {
            detonated = true;
            detonate(target, session);
        }
    }

    private void detonate(Zombie target, GameSession session) {
        int rowIdx = (int) target.getPosition().y();

        if (target.getFaction() == Faction.ZOMBIES) {
            int totalCols = session.getEnvironment().getCols();
            for (int c = 0; c < totalCols; c++) {
                Cell gridCell = session.getEnvironment().getCell(rowIdx, c);
                if (gridCell != null && gridCell.getPlant() != null && gridCell.getPlant().isAlive()) {
                    gridCell.getPlant().takeDamage(PLANT_DAMAGE, target);
                }
            }
        } else {
            session.getZombies().stream()
                    .filter(z -> z.isAlive() && z.getFaction() == Faction.ZOMBIES && (int) z.getPosition().y() == rowIdx)
                    .forEach(z -> z.takeDamage(z.getHp()));
        }

        int armorHp = target.getArmor() != null ? target.getArmor().getHP() : 0;
        int overkillDamage = target.getHp() + armorHp + 1;
        target.takeDamage(overkillDamage, (Object) null);
    }
}
