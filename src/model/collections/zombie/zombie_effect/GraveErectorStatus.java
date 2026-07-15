package model.collections.zombie.zombie_effect;

import model.collections.Faction;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import service.GameClock;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraveErectorStatus implements ZombieEffectStatus {
    private final double tombGenerationDelay;
    private final int maxTombsPerAction;
    private double actionClock;

    public GraveErectorStatus(double cooldown, int spawnCount) {
        this.tombGenerationDelay = cooldown;
        this.maxTombsPerAction = spawnCount;
        this.actionClock = cooldown;
    }

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (!target.isAlive() || target.getFaction() == Faction.PLANTS || target.getPosition() == null) {
            return;
        }

        actionClock += GameClock.SECONDS_PER_TICK;
        if (actionClock >= tombGenerationDelay) {
            actionClock = 0;
            launchNecroticSpire(target, session);
        }
    }

    private void launchNecroticSpire(Zombie spellcaster, GameSession session) {
        List<Cell> emptyGridSpots = new ArrayList<>();
        int gridRows = session.getLawn().getRows();
        int gridCols = session.getLawn().getCols();
        int currentX = (int) spellcaster.getPosition().x();

        for (int r = 0; r < gridRows; r++) {
            for (int c = 0; c < gridCols; c++) {
                if (c <= currentX) {
                    Cell checkedCell = session.getLawn().getCell(r, c);
                    if (checkedCell != null && checkedCell.getPlant() == null && checkedCell.getInteractableStructure() == null) {
                        emptyGridSpots.add(checkedCell);
                    }
                }
            }
        }

        if (emptyGridSpots.isEmpty()) return;
        Collections.shuffle(emptyGridSpots);

        int spawnedSpires = 0;
        for (Cell chosenCell : emptyGridSpots) {
            if (spawnedSpires >= maxTombsPerAction) break;

            Position origin = spellcaster.getPosition();
            Position targetLocation = new Position(chosenCell.getCol(), chosenCell.getRow());

            session.addZombieProjectile(new BoneProjectile(origin, targetLocation, 1.5));
            spawnedSpires++;
        }
    }
}