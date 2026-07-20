package model.collections.zombie.zombie_effect;

import model.collections.Faction;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.utils.GameSession;
import service.GameClock;

public class ReelingTackleStatus implements ZombieEffectStatus {
    private final double reelCooldown;
    private double cooldownTimer;

    public ReelingTackleStatus(double reelCooldown) {
        this.reelCooldown = reelCooldown;
        this.cooldownTimer = reelCooldown;
    }

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (!target.isAlive() || target.getPosition() == null) return;

        if (target.getFaction() == Faction.ZOMBIES) {
            int lastGridCol = session.getEnvironment().getCols() - 1;
            if (target.getPosition().x() < lastGridCol) {
                target.setPosition(new Position(lastGridCol, target.getPosition().y()));
            }
        }

        cooldownTimer += GameClock.SECONDS_PER_TICK;
        if (cooldownTimer >= reelCooldown) {
            if (launchHookLine(target, session)) {
                cooldownTimer = 0;
            }
        }
    }

    private boolean launchHookLine(Zombie caster, GameSession session) {
        int r = (int) caster.getPosition().y();
        int c = (int) caster.getPosition().x();

        if (caster.getFaction() == Faction.ZOMBIES) {
            return dragPlantTowardZombie(session, r, c, caster);
        } else {
            return pullHostileZombie(session, r, c);
        }
    }

    private boolean dragPlantTowardZombie(GameSession session, int r, int c, Zombie caster) {
        for (int colIter = c - 1; colIter >= 0; colIter--) {
            Cell activeCell = session.getEnvironment().getCell(r, colIter);
            if (activeCell != null && activeCell.getPlant() != null && activeCell.getPlant().isAlive()) {
                Plant targetPlant = activeCell.getPlant();
                int currentCol = (int) targetPlant.getLocation().x();
                int nextCol = currentCol + 1;

                if (nextCol >= c) {
                    targetPlant.takeDamage(targetPlant.getHP(), caster);
                    return true;
                }

                Cell targetCell = session.getEnvironment().getCell(r, nextCol);
                if (targetCell != null && targetCell.getPlant() == null && targetCell.getStructure() == null) {
                    activeCell.setPlant(null);
                    targetCell.setPlant(targetPlant);
                    targetPlant.setPosition(new Position(nextCol, r));
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    private boolean pullHostileZombie(GameSession session, int r, int c) {
        for (Zombie enemy : session.getZombies()) {
            if (enemy.isAlive() && enemy.getFaction() == Faction.ZOMBIES
                    && (int) enemy.getPosition().y() == r && enemy.getPosition().x() > c) {
                enemy.takeDamage(enemy.getHp());
                return true;
            }
        }
        return false;
    }
}
