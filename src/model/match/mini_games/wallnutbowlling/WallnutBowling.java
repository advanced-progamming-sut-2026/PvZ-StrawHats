package model.match.mini_games.wallnutbowlling;

import model.collections.zombie.Zombie;
import model.match.mini_games.MiniGameMode;
import model.match.mini_games.wallnutbowlling.nut.BigNut;
import model.match.mini_games.wallnutbowlling.nut.BowlingWallnut;
import model.match.mini_games.wallnutbowlling.nut.ExplodeONut;
import model.match.mini_games.wallnutbowlling.nut.Nut;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * No sun falls here and the player never picks plants directly: nuts arrive
 * one at a time off a conveyor belt and can only be planted up to the red
 * line. Once planted they roll like bowling balls into the zombies.
 */
public class WallnutBowling extends MiniGameMode {
    private static final Random RAND = new Random();
    private static final double HIT_DISTANCE = 0.4;

    public enum NutKind { BOWLING, EXPLODE, BIG }

    private final GameSession session;
    private final int redLineColumn;
    private final List<Nut> activeNuts = new ArrayList<>();
    private NutKind nextNutKind;

    public WallnutBowling(int difficulty) {
        setDifficulty(difficulty);
        this.session = new GameSession(5, 9);
        this.redLineColumn = 2 + difficulty; // further back on easier levels
        offerNextNut();
    }

    private void offerNextNut() {
        NutKind[] kinds = NutKind.values();
        nextNutKind = kinds[RAND.nextInt(kinds.length)];
    }

    public NutKind getNextNutKind() { return nextNutKind; }

    public boolean plantNut(int row, int col) {
        if (col > redLineColumn) return false;

        Position position = new Position(col, row);
        Position direction = new Position(-1, 0);
        Nut nut = switch (nextNutKind) {
            case BOWLING -> new BowlingWallnut(position, direction);
            case EXPLODE -> new ExplodeONut(position, direction);
            case BIG -> new BigNut(position, direction);
        };

        activeNuts.add(nut);
        offerNextNut();
        return true;
    }

    public void tick(double deltaSeconds) {
        session.tick();
        moveNuts(deltaSeconds);
        resolveCollisions();
        activeNuts.removeIf(nut -> !nut.isAlive());
    }

    private void moveNuts(double deltaSeconds) {
        for (Nut nut : activeNuts) {
            nut.move(deltaSeconds);
            Position pos = nut.getPosition();
            if (pos.y() <= 0 || pos.y() >= session.getEnvironment().getRows() - 1) {
                nut.bounceVertical();
            }
        }
    }

    private void resolveCollisions() {
        for (Nut nut : activeNuts) {
            if (!nut.isAlive()) continue;
            session.getZombies().stream()
                    .filter(zombie -> zombie.isAlive() && isTouching(nut, zombie))
                    .findFirst()
                    .ifPresent(zombie -> nut.onHitZombie(zombie, session));
        }
    }

    private boolean isTouching(Nut nut, Zombie zombie) {
        return zombie.getPosition() != null && nut.getPosition().distanceTo(zombie.getPosition()) <= HIT_DISTANCE;
    }

    public boolean isWon() {
        return session.isWavesStarted() && session.areWavesDone() && !session.isGameOver();
    }

    public boolean isLost() {
        return session.isGameOver();
    }

    public GameSession getSession() { return session; }
    public int getRedLineColumn() { return redLineColumn; }
    public List<Nut> getActiveNuts() { return activeNuts; }
}
