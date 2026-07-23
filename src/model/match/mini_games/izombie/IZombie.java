package model.match.mini_games.izombie;

import model.collections.item.GroundItem;
import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match.mini_games.MiniGameMode;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.Environment;
import model.utils.GameSession;
import view.GeneralPrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IZombie extends MiniGameMode {
    private static final int STARTING_SUN = 150;
    private static final String SUN_ZOMBIE_ALIAS = "ZombieArmor2"; // bucket-tier toughness
    private static final int SUN_PER_PRODUCTION = 25;
    private static final double INITIAL_PRODUCTION_INTERVAL = 10.0;
    private static final double MIN_PRODUCTION_INTERVAL = 2.0;
    private static final double PRODUCTION_ACCELERATION = 0.08;
    private static final int REDLINE_COLUMN = 6; // red line is shown after 1-based column 7
    private static final Random RAND = new Random();
    private static final int[] DEFENDER_PLANTS = {1, 6, 44, 23, 30, 25};

    // Exactly five purchasable zombies per level.  The three selections are
    // intentionally different and contain more than ten unique zombies.
    private static final Map<Integer, Map<String, Integer>> ROSTERS = buildRosters();

    private final GameSession session;
    private final Brain[] brains;
    private final Map<String, Integer> roster;
    private final List<Zombie> sunZombies;
    private final Set<Zombie> playerZombies =
            Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<Zombie, Double> sunProductionTimers =
            new IdentityHashMap<>();
    private final List<String> eventLog = new ArrayList<>();
    private double elapsedSeconds;

    public IZombie(int difficulty) {
        setDifficulty(difficulty);
        this.session = new GameSession(5, 9);
        configureSession(session);
        session.setSkySunEnabled(false);
        session.setZombieBreachesEnabled(false);
        session.addSun(STARTING_SUN);
        this.roster = ROSTERS.get(getDifficulty());
        this.brains = new Brain[session.getEnvironment().getRows()];
        for (int row = 0; row < brains.length; row++) {
            brains[row] = new Brain(new Position(0, row));
        }
        seedDefendingPlants();
        this.sunZombies = spawnSunZombies();
        log("I, Zombie level " + getDifficulty()
                + " started with " + STARTING_SUN + " sun.");
    }

    /**
     * Place two or three real plants in the first three columns of every
     * lane.  The pattern is random but always leaves several left-side
     * columns represented, making the red-line deployment rule visible.
     */
    private void seedDefendingPlants() {
        for (int row = 0; row < session.getEnvironment().getRows(); row++) {
            List<Integer> columns = new ArrayList<>(List.of(0, 1, 2));
            Collections.shuffle(columns, RAND);
            int count = 2 + RAND.nextInt(2);
            for (int i = 0; i < count; i++) {
                int col = columns.get(i);
                int plantId = DEFENDER_PLANTS[RAND.nextInt(DEFENDER_PLANTS.length)];
                Plant plant = PlantFactory.createPlant(plantId, 1, new Position(col, row));
                session.plantAt(row, col, plant);
            }
        }
        log("Defending plants were generated in columns 1-3, left of the red line.");
    }

    private List<Zombie> spawnSunZombies() {
        return IntStream.range(0, session.getEnvironment().getRows())
                .mapToObj(row -> {
                    Zombie zombie = ZombieFactory.create(SUN_ZOMBIE_ALIAS, row, REDLINE_COLUMN + 1);
                    zombie.setPosition(new Position(REDLINE_COLUMN + 1, row));
                    zombie.setSpeed(Position.ShowZero());
                    session.spawnZombie(zombie);
                    sunProductionTimers.put(zombie, INITIAL_PRODUCTION_INTERVAL);
                    return zombie;
                })
                .collect(Collectors.toList());
    }

    public boolean placeZombie(String alias, int row) {
        return placeZombie(alias, row, REDLINE_COLUMN + 1);
    }

    public boolean placeZombie(String alias, int row, int col) {
        if (row < 0 || row >= brains.length || alias == null) return false;
        if (col <= REDLINE_COLUMN || col >= session.getCols()) return false;
        String selectedAlias = findRosterAlias(alias);
        if (selectedAlias == null) return false;

        int cost = roster.get(selectedAlias);
        if (!session.spendSun(cost)) return false;

        Zombie zombie = ZombieFactory.create(selectedAlias, row, col);
        zombie.setPosition(new Position(col, row));
        session.spawnZombie(zombie);
        playerZombies.add(zombie);
        log("Placed " + selectedAlias + " in lane " + (row + 1)
                + " at column " + (col + 1) + " for " + cost
                + " sun. Sun left: " + session.getSunCount() + ".");
        return true;
    }

    private String findRosterAlias(String alias) {
        if (SUN_ZOMBIE_ALIAS.equalsIgnoreCase(alias.trim())) return null;
        return roster.keySet().stream()
                .filter(candidate -> candidate.equalsIgnoreCase(alias.trim()))
                .findFirst().orElse(null);
    }

    public void tick(double deltaSeconds) {
        if (isWon() || isLost()) return;
        double safeDelta = Math.max(0, deltaSeconds);
        elapsedSeconds += safeDelta;
        session.tick();
        detectSunZombieDeaths();
        generateSunFromSunZombies(safeDelta);
        checkBrainsEaten();
    }

    private void detectSunZombieDeaths() {
        for (Zombie zombie : sunZombies) {
            if (!zombie.isAlive() && sunProductionTimers.containsKey(zombie)) {
                sunProductionTimers.remove(zombie);
                log("The Sun Producer Zombie in lane "
                        + (zombie.getPosition() == null
                        ? "unknown" : ((int) zombie.getPosition().y() + 1))
                        + " was killed. It will never return.");
            }
        }
    }

    private void generateSunFromSunZombies(double deltaSeconds) {
        double interval = currentProductionInterval();
        for (Zombie zombie : sunZombies) {
            if (!zombie.isAlive()) continue;
            double remaining = sunProductionTimers.getOrDefault(zombie, interval) - deltaSeconds;
            if (remaining <= 1e-9) {
                session.addSun(SUN_PER_PRODUCTION);
                log("Sun Producer Zombie in lane "
                        + ((int) zombie.getPosition().y() + 1)
                        + " generated " + SUN_PER_PRODUCTION + " sun. Total: "
                        + session.getSunCount() + " (rate interval "
                        + String.format("%.1fs", interval) + ").");
                remaining += interval;
            }
            sunProductionTimers.put(zombie, remaining);
        }
    }

    private double currentProductionInterval() {
        return Math.max(MIN_PRODUCTION_INTERVAL,
                INITIAL_PRODUCTION_INTERVAL - PRODUCTION_ACCELERATION * elapsedSeconds);
    }

    private void checkBrainsEaten() {
        for (Zombie zombie : new ArrayList<>(session.getZombies())) {
            if (!zombie.isAlive() || zombie.getPosition() == null
                    || zombie.getPosition().x() > 0) continue;

            int row = (int) Math.round(zombie.getPosition().y());
            if (row >= 0 && row < brains.length) {
                if (!brains[row].isEaten()) {
                    brains[row].markEaten();
                    log(zombie.getName() + " ate the brain in lane " + (row + 1) + ".");
                }
                // A second zombie reaching an already-eaten brain must not
                // remain stranded outside the lawn.
                zombie.setHp(0);
            }
        }
        if (isWon()) {
            log("All five brains were eaten. I, Zombie is won.");
        }
    }

    public boolean isWon() {
        for (Brain brain : brains) {
            if (!brain.isEaten()) return false;
        }
        return true;
    }

    public boolean isLost() {
        if (isWon()) return false;
        boolean anyPlayerZombieAlive = playerZombies.stream().anyMatch(Zombie::isAlive);
        boolean canAffordAnything = roster.values().stream()
                .anyMatch(cost -> cost <= session.getSunCount());
        return !anyPlayerZombieAlive && !canAffordAnything;
    }

    public Map<String, Integer> getRoster() { return roster; }
    public Brain[] getBrains() { return brains; }
    public GameSession getSession() { return session; }
    public List<Zombie> getSunZombies() { return List.copyOf(sunZombies); }
    public int getRedLineColumn() { return REDLINE_COLUMN; }
    public double getSunProductionInterval() { return currentProductionInterval(); }

    public boolean isSunProducer(Zombie zombie) {
        return sunZombies.stream().anyMatch(candidate -> candidate == zombie);
    }

    public List<GroundItem> collectItemsAt(int x, int y) {
        return session.collectItemsNear(new Position(x, y));
    }

    public void addSunCheat(int amount) {
        if (amount <= 0) return;
        session.addSun(amount);
        log("Cheat added " + amount + " sun. Total: " + session.getSunCount() + ".");
    }

    public String renderPlantAt(int row, int col) {
        return session.renderTileStatus(row, col);
    }

    public String renderDefendingPlants() {
        if (session.getPlants().isEmpty()) return "no defending plants";
        return renderPlants();
    }

    public String renderZombiesInfo() {
        return renderZombies();
    }

    public String renderState() {
        StringBuilder result = new StringBuilder(getStageDetails())
                .append(" | Sun: ").append(session.getSunCount())
                .append(" | Red line after column ").append(REDLINE_COLUMN + 1)
                .append("\nAvailable zombies (cost): ").append(roster)
                .append("\nBrains:");
        for (int row = 0; row < brains.length; row++) {
            result.append(" lane ").append(row + 1).append("=")
                    .append(brains[row].isEaten() ? "eaten" : "safe");
        }
        result.append("\nLawn (| = red line):\n");
        Environment env = session.getEnvironment();
        for (int row = 0; row < env.getRows(); row++) {
            char brainSymbol = brains[row].isEaten() ? '-' : 'B';
            result.append(row + 1).append(" ").append(brainSymbol).append(" ");

            for (int col = 0; col < env.getCols(); col++) {
                if (col == REDLINE_COLUMN + 1) result.append("| ");
                result.append(symbolAt(row, col)).append(" ");
            }
            result.append("\n");
        }
        result.append("Legend: B=brain, P=plant, Z=playable zombie, S=Sun Producer Zombie\n");
        result.append("Plants:\n  ").append(renderPlants().replace("\n", "\n  "));
        result.append("\nZombies:\n  ").append(renderZombies().replace("\n", "\n  "));
        String groundItems = renderGroundItems();
        if (!groundItems.isBlank()) {
            result.append("\nGround items:\n  ").append(groundItems.replace("\n", "\n  "));
        }
        result.append("\nSun Producer rate interval: ")
                .append(String.format("%.1fs", currentProductionInterval()));
        if (!eventLog.isEmpty()) {
            result.append("\nRecent events:\n  ")
                    .append(String.join("\n  ", eventLog));
        }
        return result.toString();
    }

    private String renderGroundItems() {
        return session.getItems().stream()
                .filter(GroundItem.class::isInstance)
                .map(GroundItem.class::cast)
                .filter(item -> item.isAlive() && item.getPosition() != null)
                .map(item -> item.getItemType().name().toLowerCase() + " at ("
                        + ((int) Math.round(item.getPosition().x()) + 1) + ", "
                        + ((int) Math.round(item.getPosition().y()) + 1) + ")")
                .collect(Collectors.joining("\n"));
    }

    private char symbolAt(int row, int col) {
        Cell cell = session.getEnvironment().getCell(row, col);
        if (cell == null) return '?';
        boolean playerZombie = session.getZombies().stream()
                .anyMatch(z -> z.isAlive() && z.getPosition() != null
                        && Math.round(z.getPosition().x()) == col
                        && Math.round(z.getPosition().y()) == row);
        boolean sunZombie = session.getZombies().stream()
                .anyMatch(z -> playerZombie && isSunProducer(z)
                        && z.isAlive() && Math.round(z.getPosition().x()) == col
                        && Math.round(z.getPosition().y()) == row);
        if (sunZombie) return 'S';
        if (playerZombie) return 'Z';
        if (cell.getPlant() != null && cell.getPlant().isAlive()) return 'P';
        return '.';
    }

    private String renderPlants() {
        if (session.getPlants().isEmpty()) return "none";
        return session.getPlants().stream()
                .filter(plant -> plant.isAlive() && plant.getPosition() != null)
                .map(plant -> plant.getName() + " | hp: " + plant.getHP()
                        + " | position: (" + ((int) plant.getPosition().x() + 1)
                        + ", " + ((int) plant.getPosition().y() + 1) + ")")
                .collect(Collectors.joining("\n"));
    }

    private String renderZombies() {
        if (session.getZombies().isEmpty()) return "none";
        return session.getZombies().stream()
                .filter(zombie -> zombie.isAlive() && zombie.getPosition() != null)
                .map(zombie -> (isSunProducer(zombie) ? "Sun Producer Zombie" : zombie.getName())
                        + " | hp: " + zombie.getHp() + "/" + zombie.getMaxHp()
                        + " | position: (" + String.format("%.2f", zombie.getPosition().x() + 1)
                        + ", " + ((int) Math.round(zombie.getPosition().y()) + 1) + ")"
                        + " | state: " + zombie.getZombieState())
                .collect(Collectors.joining("\n"));
    }

    private void log(String message) {
        eventLog.add(message);
        if (eventLog.size() > 14) eventLog.remove(0);
        GeneralPrinter.print(message);
    }

    private static Map<Integer, Map<String, Integer>> buildRosters() {
        Map<Integer, Map<String, Integer>> rosters = new LinkedHashMap<>();

        Map<String, Integer> level1 = new LinkedHashMap<>();
        level1.put("ZombieDefault", 50);
        level1.put("ZombieImp", 75);
        level1.put("ZombieRa", 100);
        level1.put("ZombieArmor1", 125);
        level1.put("ZombieExplorer", 150);
        rosters.put(1, Map.copyOf(level1));

        Map<String, Integer> level2 = new LinkedHashMap<>();
        level2.put("ZombieArmor1", 75);
        level2.put("ZombieTombRaiser", 125);
        level2.put("ZombieProspector", 100);
        level2.put("ZombieLostCityJane", 150);
        level2.put("ZombieNewspaper", 125);
        rosters.put(2, Map.copyOf(level2));

        Map<String, Integer> level3 = new LinkedHashMap<>();
        level3.put("ZombieImp", 60);
        level3.put("ZombieDarkArmor3", 125);
        level3.put("ZombieModernAllStar", 150);
        level3.put("ZombieGargantuar", 150);
        level3.put("ZombieDarkJuggler", 125);
        rosters.put(3, Map.copyOf(level3));

        return rosters;
    }
}
