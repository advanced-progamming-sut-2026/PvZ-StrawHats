package model.match.mini_games.wallnutbowlling;

import model.collections.zombie.Zombie;
import model.match.mini_games.MiniGameMode;
import model.match.mini_games.MiniGameWaves;
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
    private final List<String> zombiePool;
    private NutKind nextNutKind;

    public WallnutBowling(int difficulty) {
        setDifficulty(difficulty);
        this.session = new GameSession(5, 9);
        configureSession(session);
        session.setSkySunEnabled(false);
        this.redLineColumn = 5 - getDifficulty(); // level 1: column 5, level 3: column 3
        this.zombiePool = zombiePoolFor(getDifficulty());
        session.setWaves(wavesFor(getDifficulty()));
        session.startWaves();
        offerNextNut();
    }

    private void offerNextNut() {
        NutKind[] kinds = NutKind.values();
        nextNutKind = kinds[RAND.nextInt(kinds.length)];
    }

    public NutKind getNextNutKind() { return nextNutKind; }

    public boolean plantNut(int row, int col) {
        if (row < 0 || row >= session.getRows() || col < 0 || col > redLineColumn) return false;

        Position position = new Position(col, row);
        Position direction = new Position(1, 0);
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
            if (pos.x() > session.getCols()) {
                nut.kill();
                continue;
            }
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
    public List<String> getZombiePool() { return List.copyOf(zombiePool); }

    public String renderState() {
        StringBuilder result = new StringBuilder(getStageDetails())
                .append(" | Red line: column ").append(redLineColumn + 1)
                .append(" | Next nut: ").append(nextNutKind)
                .append(" | Active nuts: ").append(activeNuts.size())
                .append("\n").append(session.renderMap());
        if (!activeNuts.isEmpty()) {
            result.append("\nNuts:");
            for (Nut nut : activeNuts) {
                result.append(" (").append(String.format("%.1f", nut.getPosition().x() + 1))
                        .append(", ").append(String.format("%.1f", nut.getPosition().y() + 1)).append(")");
            }
        }
        return result.toString();
    }

    private List<String> zombiePoolFor(int level) {
        return switch (level) {
            case 2 -> List.of("ZombieDefault", "ZombieArmor1", "ZombieArmor2");
            case 3 -> List.of("ZombieDefault", "ZombieImp", "ZombieArmor1", "ZombieArmor2",
                    "ZombieNewspaper", "ZombieModernAllStar", "ZombieGargantuar");
            default -> List.of("ZombieDefault", "ZombieImp", "ZombieArmor1");
        };
    }

    private List<model.match_mechanisms.ZombieWave> wavesFor(int level) {
        return switch (level) {
            case 2 -> MiniGameWaves.create(session,
                    new double[] {7, 18, 22, 26},
                    new String[][] {
                            {"ZombieDefault", "ZombieArmor1"},
                            {"ZombieArmor2"},
                            {"ZombieArmor2", "ZombieArmor1"},
                            {"ZombieArmor2", "ZombieArmor2", "ZombieArmor1", "ZombieDefault", "ZombieDefault"}
                    });
            case 3 -> MiniGameWaves.create(session,
                    new double[] {6, 16, 20, 24},
                    new String[][] {
                            {"ZombieArmor1", "ZombieArmor2"},
                            {"ZombieNewspaper", "ZombieImp"},
                            {"ZombieModernAllStar", "ZombieDefault"},
                            {"ZombieGargantuar", "ZombieArmor2", "ZombieArmor1", "ZombieDefault"}
                    });
            default -> MiniGameWaves.create(session,
                    new double[] {8, 20, 25},
                    new String[][] {
                            {"ZombieDefault"},
                            {"ZombieDefault", "ZombieImp"},
                            {"ZombieArmor1", "ZombieDefault", "ZombieDefault"}
                    });
        };
    }
}
