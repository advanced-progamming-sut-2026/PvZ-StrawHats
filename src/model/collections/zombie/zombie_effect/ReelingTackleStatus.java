package model.collections.zombie.zombie_effect;

import model.collections.Faction;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import service.GameClock;
import util.GameSession;

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
            int lastGridCol = session.getLawn().getCols() - 1;
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
            Cell activeCell = session.getLawn().getCell(r, colIter);
            if (activeCell != null && activeCell.getPlant() != null && activeCell.getPlant().isAlive()) {
                Plant targetPlant = activeCell.getPlant();
                double nextX = targetPlant.getLocation().x() + 1;

                if (nextX >= c) {
                    targetPlant.takeDamage(targetPlant.getHP(), caster);
                    return true;
                }

                Cell targetCell = session.getLawn().getCell(r, nextX);
                if (targetCell != null && targetCell.getPlant() == null && targetCell.getInteractableStructure() == null) {
                    activeCell.setPlant(null);
                    targetCell.setPlant(targetPlant);
                    targetPlant.setPosition(new Position(nextX, r));
                    targetPlant.setPosition(new Position(nextX, r));
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