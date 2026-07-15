package model.collections.zombie.zombie_effect;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import service.GameClock;
import util.GameSession;

public class PianistMusicEffect implements ZombieEffectStatus {
    private final double tempoDelay;
    private double rhythmTimer = 0.0;

    public PianistMusicEffect(double tempoDelay) {
        this.tempoDelay = tempoDelay;
    }

    @Override
    public void applyTickEffect(Zombie pianist, GameSession session) {
        if (!pianist.isAlive() || pianist.getPosition() == null) return;

        rhythmTimer += GameClock.SECONDS_PER_TICK;

        if (rhythmTimer >= tempoDelay) {
            rhythmTimer = 0;
            triggerZombieRowShift(pianist, session);
        }
    }

    private void triggerZombieRowShift(Zombie conductor, GameSession session) {
        int limitRows = session.getLawn().getRows();

        for (Zombie dancer : session.getZombies()) {
            if (dancer == null || !dancer.isAlive() || dancer == conductor || dancer.getFaction() != conductor.getFaction()) {
                continue;
            }

            int activeRow = (int) dancer.getPosition().y();
            int directionModifier = Math.random() < 0.5 ? -1 : 1;
            int targetRow = activeRow + directionModifier;

            if (targetRow >= 0 && targetRow < limitRows) {
                dancer.setPosition(new Position(dancer.getPosition().x(), targetRow));
            } else {
                targetRow = activeRow - directionModifier;
                if (targetRow >= 0 && targetRow < limitRows) {
                    dancer.setPosition(new Position(dancer.getPosition().x(), targetRow));
                }
            }
        }
    }
}
