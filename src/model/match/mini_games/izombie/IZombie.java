package model.match.mini_games.izombie;

import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match.mini_games.MiniGameMode;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * The player controls the zombies: spend sun to place them right of the red
 * line and fight through a lawn of real, normally-behaving defending plants
 * to eat every brain (one per row, replacing the lawn mowers).
 */
public class IZombie extends MiniGameMode {
    private static final int STARTING_SUN = 150;
    private static final String SUN_ZOMBIE_ALIAS = "ZombieArmor2"; // bucket-tier toughness
    private static final double SUN_BASE_RATE = 2.0;     // sun per second at level start
    private static final double SUN_GROWTH_RATE = 0.1;   // extra sun/sec, per second elapsed
    private static final int REDLINE_COLUMN = 6;
    private static final int DEFENDER_PLANT_ID = 6; // Peashooter — a normal, active defender
    private static final Random RAND = new Random();

    // Ten distinct zombies total across the three levels, five per level, no two levels identical.
    private static final Map<Integer, Map<String, Integer>> ROSTERS = buildRosters();

    private final GameSession session;
    private final Brain[] brains;
    private final Map<String, Integer> roster;
    private final List<Zombie> sunZombies;
    private double elapsedSeconds = 0;

    public IZombie(int difficulty) {
        setDifficulty(difficulty);
        this.session = new GameSession(5, 9);
        this.session.addSun(STARTING_SUN);
        this.roster = ROSTERS.get(getDifficulty());
        this.brains = new Brain[session.getEnvironment().getRows()];
        for (int row = 0; row < brains.length; row++) {
            brains[row] = new Brain(new Position(0, row));
        }
        seedDefendingPlants();
        this.sunZombies = spawnSunZombies();
    }

    /**
     * A handful of real, normally-behaving plants pre-placed on the left
     * columns, defending the brains just like a normal lawn.
     */
    private void seedDefendingPlants() {
        int leftColumns = 3;
        for (int row = 0; row < session.getEnvironment().getRows(); row++) {
            int col = RAND.nextInt(leftColumns);
            Plant plant = PlantFactory.createPlant(DEFENDER_PLANT_ID, 1, new Position(col, row));
            session.plantAt(row, col, plant);
        }
    }

    private List<Zombie> spawnSunZombies() {
        return java.util.stream.IntStream.range(0, session.getEnvironment().getRows())
                .mapToObj(row -> {
                    Zombie zombie = ZombieFactory.create(SUN_ZOMBIE_ALIAS, row, REDLINE_COLUMN + 1);
                    zombie.setPosition(new Position(REDLINE_COLUMN + 1, row));
                    session.spawnZombie(zombie);
                    return zombie;
                })
                .collect(Collectors.toList());
    }

    public boolean placeZombie(String alias, int row) {
        Integer cost = roster.get(alias);
        if (cost == null || !session.spendSun(cost)) return false;

        Zombie zombie = ZombieFactory.create(alias, row, REDLINE_COLUMN + 1);
        zombie.setPosition(new Position(REDLINE_COLUMN + 1, row));
        session.spawnZombie(zombie);
        return true;
    }

    public void tick(double deltaSeconds) {
        elapsedSeconds += deltaSeconds;
        session.tick();
        generateSunFromSunZombies(deltaSeconds);
        checkBrainsEaten();
    }

    private void generateSunFromSunZombies(double deltaSeconds) {
        double rate = SUN_BASE_RATE + SUN_GROWTH_RATE * elapsedSeconds;
        long aliveSunZombies = sunZombies.stream().filter(Zombie::isAlive).count();
        if (aliveSunZombies == 0) return;
        session.addSun((int) Math.round(rate * aliveSunZombies * deltaSeconds));
    }

    private void checkBrainsEaten() {
        for (Zombie zombie : session.getZombies()) {
            if (!zombie.isAlive() || zombie.getPosition() == null) continue;
            if (zombie.getPosition().x() > 0) continue;

            int row = (int) zombie.getPosition().y();
            if (row >= 0 && row < brains.length && !brains[row].isEaten()) {
                brains[row].markEaten();
            }
        }
    }

    public boolean isWon() {
        for (Brain brain : brains) {
            if (!brain.isEaten()) return false;
        }
        return true;
    }

    public boolean isLost() {
        boolean cheapestAffordable = roster.values().stream().anyMatch(cost -> cost <= session.getSunCount());
        boolean anyZombiesAlive = session.getZombies().stream().anyMatch(Zombie::isAlive);
        return !cheapestAffordable && !anyZombiesAlive && !isWon();
    }

    public Map<String, Integer> getRoster() { return roster; }
    public Brain[] getBrains() { return brains; }
    public GameSession getSession() { return session; }

    private static Map<Integer, Map<String, Integer>> buildRosters() {
        Map<Integer, Map<String, Integer>> rosters = new LinkedHashMap<>();

        Map<String, Integer> level1 = new LinkedHashMap<>();
        level1.put("ZombieDefault", 100);
        level1.put("ZombieImp", 100);
        level1.put("ZombieRa", 100);
        level1.put("ZombieArmor1", 200);
        level1.put("ZombieExplorer", 250);
        rosters.put(1, level1);

        Map<String, Integer> level2 = new LinkedHashMap<>();
        level2.put("ZombieDefault", 100);
        level2.put("ZombieImp", 100);
        level2.put("ZombieTombRaiser", 300);
        level2.put("ZombiePeashooter", 200);
        level2.put("ZombieJalapeno", 300);
        rosters.put(2, level2);

        Map<String, Integer> level3 = new LinkedHashMap<>();
        level3.put("ZombieSquash", 350);
        level3.put("ZombieGargantuar", 1500);
        level3.put("ZombieRa", 100);
        level3.put("ZombieExplorer", 250);
        level3.put("ZombieTombRaiser", 300);
        rosters.put(3, level3);

        return rosters;
    }
}
