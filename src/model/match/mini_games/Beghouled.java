package model.match.mini_games;

import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.Environment;
import model.pitches.ObstacleFactory;
import model.pitches.ObstacleInformation;
import model.utils.GameSession;

import java.util.*;

/**
 * A three-in-a-row puzzle laid over a normal (never-ending) zombie lawn.
 * Swapping two adjacent plants is only allowed if it creates a match;
 * matches pay out sun, which can be spent upgrading every plant of a given
 * type on the board. A zombie eating a plant leaves a permanent crater.
 */
public class Beghouled extends MiniGameMode {
    private static final Random RAND = new Random();
    private static final int SUN_PER_UNIT = 50;
    private static final double RESPAWN_WAVE_INTERVAL = 20.0;

    private final GameSession session;
    private final int[] boardPlantIds; // the five plant types seeded on this level's board
    private final Map<String, UpgradePath> upgradePaths;
    private final int matchesNeeded;

    private int matchesMade = 0;
    private double timeSinceLastWave = 0;

    public Beghouled(int difficulty) {
        setDifficulty(difficulty);
        this.session = new GameSession(5, 9);
        this.boardPlantIds = plantPoolFor(difficulty);
        this.upgradePaths = buildUpgradePaths();
        this.matchesNeeded = 5 + difficulty * 3;
        seedBoard();
        session.startWaves();
    }

    private int[] plantPoolFor(int difficulty) {
        return switch (difficulty) {
            case 2 -> new int[] { 1, 6, 44, 25, 27 };  // Sunflower, Peashooter, Wall-nut, Cabbage-pult, Melon-pult
            case 3 -> new int[] { 1, 6, 44, 23, 25 };  // Sunflower, Peashooter, Wall-nut, Puff-shroom, Cabbage-pult
            default -> new int[] { 1, 6, 44, 23, 30 }; // Sunflower, Peashooter, Wall-nut, Puff-shroom, Potato Mine
        };
    }

    private Map<String, UpgradePath> buildUpgradePaths() {
        Map<String, UpgradePath> map = new LinkedHashMap<>();
        map.put("Peashooter", new UpgradePath(6, 7, 500));
        map.put("Repeater", new UpgradePath(7, 21, 1500));
        map.put("Wall-nut", new UpgradePath(44, 45, 500));
        map.put("Puff-shroom", new UpgradePath(23, 24, 250));
        map.put("Cabbage-pult", new UpgradePath(25, 27, 1000));
        map.put("Melon-pult", new UpgradePath(27, 28, 750));
        return map;
    }

    private void seedBoard() {
        Environment env = session.getEnvironment();
        for (int row = 0; row < env.getRows(); row++) {
            for (int col = 0; col < env.getCols(); col++) {
                placeRandomPlant(row, col);
            }
        }
    }

    private void placeRandomPlant(int row, int col) {
        int plantId = boardPlantIds[RAND.nextInt(boardPlantIds.length)];
        Plant plant = PlantFactory.createPlant(plantId, 1, new Position(col, row));
        session.getEnvironment().getCell(row, col).setPlant(plant);
    }

    private boolean isCrater(int row, int col) {
        Cell cell = session.getEnvironment().getCell(row, col);
        return cell != null && cell.getObstacle() != null;
    }

    public boolean trySwap(int row1, int col1, int row2, int col2) {
        if (!areAdjacent(row1, col1, row2, col2)) return false;
        if (isCrater(row1, col1) || isCrater(row2, col2)) return false;

        swapCells(row1, col1, row2, col2);

        if (!hasMatchThrough(row1, col1) && !hasMatchThrough(row2, col2)) {
            swapCells(row1, col1, row2, col2); // no match created, revert
            return false;
        }

        resolveMatches(false);
        return true;
    }

    private boolean areAdjacent(int row1, int col1, int row2, int col2) {
        int rowDiff = Math.abs(row1 - row2);
        int colDiff = Math.abs(col1 - col2);
        return (rowDiff + colDiff) == 1;
    }

    private void swapCells(int row1, int col1, int row2, int col2) {
        Cell a = session.getEnvironment().getCell(row1, col1);
        Cell b = session.getEnvironment().getCell(row2, col2);
        Plant plantA = a.getPlant();
        Plant plantB = b.getPlant();
        a.setPlant(plantB);
        b.setPlant(plantA);
        if (plantB != null) plantB.setPosition(new Position(col1, row1));
        if (plantA != null) plantA.setPosition(new Position(col2, row2));
    }

    private boolean hasMatchThrough(int row, int col) {
        return matchLength(row, col, 0, 1) + matchLength(row, col, 0, -1) >= 2
                || matchLength(row, col, 1, 0) + matchLength(row, col, -1, 0) >= 2;
    }

    private int matchLength(int row, int col, int rowStep, int colStep) {
        Integer id = plantIdAt(row, col);
        if (id == null) return 0;
        int length = 0;
        int r = row + rowStep;
        int c = col + colStep;
        while (id.equals(plantIdAt(r, c))) {
            length++;
            r += rowStep;
            c += colStep;
        }
        return length;
    }

    private Integer plantIdAt(int row, int col) {
        Cell cell = session.getEnvironment().getCell(row, col);
        if (cell == null || cell.getPlant() == null || !cell.getPlant().isAlive()) return null;
        return cell.getPlant().getId();
    }

    /**
     * Finds every match currently on the board, removes it, applies gravity
     * and refill, and pays out sun per matched group. Repeats for any
     * cascade matches created by the refill (which pay one extra sun unit
     * per group than a player-triggered match would).
     */
    private void resolveMatches(boolean isCascadePass) {
        List<List<int[]>> groups = findMatchedGroups();
        if (groups.isEmpty()) {
            finishResolution();
            return;
        }

        int bonusUnits = isCascadePass ? 1 : 0;
        for (List<int[]> group : groups) {
            awardSun(group.size(), bonusUnits);
            matchesMade++;
            for (int[] cell : group) {
                session.getEnvironment().getCell(cell[0], cell[1]).setPlant(null);
            }
        }

        applyGravityAndRefill();
        resolveMatches(true); // any further matches this triggers are cascades
    }

    private void finishResolution() {
        if (!anyMoveWouldMatch()) {
            resetBoard();
        }
        if (matchesMade >= matchesNeeded) {
            session.killAllZombies();
        }
    }

    private void awardSun(int matchSize, int bonusUnits) {
        int units = Math.max(1, matchSize - 2) + bonusUnits;
        session.addSun(units * SUN_PER_UNIT);
    }

    /**
     * Returns every matched run on the board, grouped so that runs sharing
     * a cell (an L/T-shaped match) are merged into a single group.
     */
    private List<List<int[]>> findMatchedGroups() {
        java.util.Set<Long> matched = new java.util.LinkedHashSet<>();
        Environment env = session.getEnvironment();
        for (int row = 0; row < env.getRows(); row++) {
            scanLineForMatches(matched, row, 0, 0, 1, env.getCols());
        }
        for (int col = 0; col < env.getCols(); col++) {
            scanLineForMatches(matched, 0, col, 1, 0, env.getRows());
        }
        return groupConnectedCells(matched);
    }

    private List<List<int[]>> groupConnectedCells(java.util.Set<Long> matched) {
        List<List<int[]>> groups = new ArrayList<>();
        java.util.Set<Long> visited = new java.util.HashSet<>();

        for (long key : matched) {
            if (visited.contains(key)) continue;
            List<int[]> group = new ArrayList<>();
            java.util.Deque<Long> queue = new java.util.ArrayDeque<>();
            queue.add(key);
            visited.add(key);

            while (!queue.isEmpty()) {
                long current = queue.poll();
                int r = (int) (current >> 32);
                int c = (int) (current & 0xFFFFFFFFL);
                group.add(new int[] { r, c });
                for (long neighbor : neighborsOf(r, c)) {
                    if (matched.contains(neighbor) && visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
            groups.add(group);
        }
        return groups;
    }

    private long[] neighborsOf(int row, int col) {
        return new long[] {
                key(row - 1, col), key(row + 1, col), key(row, col - 1), key(row, col + 1)
        };
    }

    private long key(int row, int col) {
        return ((long) row << 32) | (col & 0xFFFFFFFFL);
    }

    private void scanLineForMatches(java.util.Set<Long> matched, int startRow, int startCol,
                                     int rowStep, int colStep, int length) {
        int runStart = 0;
        for (int i = 1; i <= length; i++) {
            Integer prevId = i - 1 < length ? plantIdAt(startRow + rowStep * (i - 1), startCol + colStep * (i - 1)) : null;
            Integer curId = i < length ? plantIdAt(startRow + rowStep * i, startCol + colStep * i) : null;
            if (curId == null || !curId.equals(prevId)) {
                if (i - runStart >= 3) {
                    for (int j = runStart; j < i; j++) {
                        int r = startRow + rowStep * j;
                        int c = startCol + colStep * j;
                        matched.add(((long) r << 32) | (c & 0xFFFFFFFFL));
                    }
                }
                runStart = i;
            }
        }
    }

    private void applyGravityAndRefill() {
        Environment env = session.getEnvironment();
        for (int col = 0; col < env.getCols(); col++) {
            applyGravityToColumn(col, env.getRows());
        }
    }

    private void applyGravityToColumn(int col, int rows) {
        int writeRow = rows - 1;
        for (int row = rows - 1; row >= 0; row--) {
            if (isCrater(row, col)) {
                writeRow = row - 1;
                continue;
            }
            Plant plant = session.getEnvironment().getCell(row, col).getPlant();
            if (plant == null) continue;
            moveDown(row, col, writeRow, plant);
            writeRow--;
        }
        fillRemainingGaps(col, rows);
    }

    private void moveDown(int fromRow, int col, int toRow, Plant plant) {
        if (fromRow == toRow) return;
        session.getEnvironment().getCell(fromRow, col).setPlant(null);
        session.getEnvironment().getCell(toRow, col).setPlant(plant);
        plant.setPosition(new Position(col, toRow));
    }

    private void fillRemainingGaps(int col, int rows) {
        for (int row = rows - 1; row >= 0; row--) {
            if (isCrater(row, col)) continue;
            if (session.getEnvironment().getCell(row, col).getPlant() == null) {
                placeRandomPlant(row, col);
            }
        }
    }

    private boolean anyMoveWouldMatch() {
        Environment env = session.getEnvironment();
        for (int row = 0; row < env.getRows(); row++) {
            for (int col = 0; col < env.getCols(); col++) {
                if (isCrater(row, col)) continue;
                if (col + 1 < env.getCols() && !isCrater(row, col + 1) && wouldMatchIfSwapped(row, col, row, col + 1)) return true;
                if (row + 1 < env.getRows() && !isCrater(row + 1, col) && wouldMatchIfSwapped(row, col, row + 1, col)) return true;
            }
        }
        return false;
    }

    private boolean wouldMatchIfSwapped(int row1, int col1, int row2, int col2) {
        swapCells(row1, col1, row2, col2);
        boolean result = hasMatchThrough(row1, col1) || hasMatchThrough(row2, col2);
        swapCells(row1, col1, row2, col2);
        return result;
    }

    private void resetBoard() {
        Environment env = session.getEnvironment();
        for (int row = 0; row < env.getRows(); row++) {
            for (int col = 0; col < env.getCols(); col++) {
                if (!isCrater(row, col)) placeRandomPlant(row, col);
            }
        }
    }

    public boolean upgrade(String plantName) {
        UpgradePath path = upgradePaths.get(plantName);
        if (path == null || !session.spendSun(path.cost)) return false;

        Environment env = session.getEnvironment();
        for (int row = 0; row < env.getRows(); row++) {
            for (int col = 0; col < env.getCols(); col++) {
                Integer id = plantIdAt(row, col);
                if (id != null && id == path.fromId) {
                    Plant upgraded = PlantFactory.createPlant(path.toId, 1, new Position(col, row));
                    session.getEnvironment().getCell(row, col).setPlant(upgraded);
                }
            }
        }
        return true;
    }

    public void tick(double deltaSeconds) {
        markCratersFromDeadPlants();
        session.tick();
        timeSinceLastWave += deltaSeconds;
        if (timeSinceLastWave >= RESPAWN_WAVE_INTERVAL) {
            timeSinceLastWave = 0;
            spawnEndlessWave();
        }
    }

    private void markCratersFromDeadPlants() {
        Environment env = session.getEnvironment();
        for (int row = 0; row < env.getRows(); row++) {
            for (int col = 0; col < env.getCols(); col++) {
                Cell cell = env.getCell(row, col);
                if (cell.getPlant() != null && !cell.getPlant().isAlive()) {
                    cell.setObstacle(ObstacleFactory.create(ObstacleInformation.CRATER));
                    cell.setPlant(null);
                }
            }
        }
    }

    private void spawnEndlessWave() {
        int lane = RAND.nextInt(session.getEnvironment().getRows());
        Zombie zombie = ZombieFactory.create("ZombieDefault", lane, session.getEnvironment().getCols() - 1);
        zombie.setPosition(new Position(session.getEnvironment().getCols() - 1, lane));
        session.spawnZombie(zombie);
    }

    public boolean isWon() {
        return matchesMade >= matchesNeeded;
    }

    public boolean isLost() {
        return session.isGameOver();
    }

    public int getMatchesMade() { return matchesMade; }
    public int getMatchesNeeded() { return matchesNeeded; }
    public GameSession getSession() { return session; }

    private static class UpgradePath {
        final int fromId;
        final int toId;
        final int cost;

        UpgradePath(int fromId, int toId, int cost) {
            this.fromId = fromId;
            this.toId = toId;
            this.cost = cost;
        }
    }
}
