package model.match.mini_games;

import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.collections.item.GroundItem;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;
import view.GeneralPrinter;

import java.util.*;

public class Zombotany extends MiniGameMode {
    private static final Random RAND = new Random();

    private static final String PEASHOOTER_ZOMBIE = "ZombiePeashooter";
    private static final String WALLNUT_ZOMBIE = "ZombieWallnut";
    private static final String JALAPENO_ZOMBIE = "ZombieJalapeno";
    private static final String SQUASH_ZOMBIE = "ZombieSquash";

    private static final double PEASHOOTER_COOLDOWN_SECONDS = 1.5;
    private static final int PEASHOOTER_DAMAGE = 20;
    private static final double JALAPENO_FUSE_SECONDS = 10.0;
    private static final double JALAPENO_SPEED_MULTIPLIER = 1.8;
    private static final double SQUASH_TOUCH_DISTANCE = 0.4;
    private static final int SQUASH_SPEED_MULTIPLIER = 3;
    private static final int INITIAL_SUN = 500;

    private final GameSession session;
    private final List<String> availablePlants;
    private final List<String> zombiePool = List.of(
            PEASHOOTER_ZOMBIE, WALLNUT_ZOMBIE, JALAPENO_ZOMBIE, SQUASH_ZOMBIE
    );
    private final Map<Zombie, Double> peashooterCooldowns = new HashMap<>();
    private final Map<Zombie, Double> jalapenoFuses = new HashMap<>();
    private final Set<Zombie> announcedZombies =
            Collections.newSetFromMap(new IdentityHashMap<>());
    private final List<String> eventLog = new ArrayList<>();

    public Zombotany(int difficulty) {
        setDifficulty(difficulty);
        this.session = new GameSession(5, 9);
        configureSession(session);
        this.availablePlants = availablePlantsFor(getDifficulty());
        this.session.addSun(INITIAL_SUN);
        this.session.setWaves(wavesFor(getDifficulty()));
        this.session.startWaves();
        log("Zombotany level " + getDifficulty() + " started with "
                + INITIAL_SUN + " sun.");
    }

    public Zombie spawnZombieForWave(int row, boolean forceSpecial) {
        String alias = forceSpecial ? pickSpecialAlias() : "ZombieDefault";
        Zombie zombie = ZombieFactory.create(alias, row, session.getEnvironment().getCols() - 1);
        zombie.setPosition(new Position(session.getEnvironment().getCols() - 1, row));

        if (alias.equals(SQUASH_ZOMBIE) && zombie.getSpeed() != null) {
            zombie.setSpeed(zombie.getSpeed().scale(SQUASH_SPEED_MULTIPLIER));
        }
        if (alias.equals(JALAPENO_ZOMBIE)) {
            increaseJalapenoSpeed(zombie);
            jalapenoFuses.put(zombie, JALAPENO_FUSE_SECONDS);
        }

        session.spawnZombie(zombie);
        announcedZombies.add(zombie);
        log(alias + " spawned in lane " + (row + 1) + ".");
        return zombie;
    }

    private String pickSpecialAlias() {
        String[] specials = { PEASHOOTER_ZOMBIE, WALLNUT_ZOMBIE, JALAPENO_ZOMBIE, SQUASH_ZOMBIE };
        return specials[RAND.nextInt(specials.length)];
    }

    public void tick(double deltaSeconds) {
        Set<Plant> alivePlantsBefore = Collections.newSetFromMap(new IdentityHashMap<>());
        session.getPlants().stream().filter(Plant::isAlive).forEach(alivePlantsBefore::add);
        session.tick();
        configureSpecialZombies();
        for (Plant plant : alivePlantsBefore) {
            if (!plant.isAlive()) {
                log(plant.getName() + " was destroyed by a Zombotany zombie.");
            }
        }
        forgetDeadZombies();
    }

    private void configureSpecialZombies() {
        for (Zombie zombie : session.getZombies()) {
            if (zombie.getPosition() == null) continue;
            if (isAlias(zombie, JALAPENO_ZOMBIE)) {
                increaseJalapenoSpeed(zombie);
                jalapenoFuses.putIfAbsent(zombie, JALAPENO_FUSE_SECONDS);
            }
            if (announcedZombies.add(zombie)) {
                log(zombie.getName() + " spawned in lane "
                        + ((int) Math.round(zombie.getPosition().y()) + 1) + ".");
            }
        }
    }

    private void increaseJalapenoSpeed(Zombie zombie) {
        Position speed = zombie.getSpeed();
        if (speed == null) return;
        double direction = speed.x() < 0 ? -1 : 1;
        double absoluteSpeed = Math.abs(speed.x());
        if (absoluteSpeed < 0.185 * JALAPENO_SPEED_MULTIPLIER - 0.0001) {
            zombie.setSpeed(new Position(direction * absoluteSpeed * JALAPENO_SPEED_MULTIPLIER,
                    speed.y()));
        }
    }

    private void tickPeashooterZombies(double deltaSeconds) {
        for (Zombie zombie : session.getZombies()) {
            if (!zombie.isAlive() || !isAlias(zombie, PEASHOOTER_ZOMBIE)) continue;

            double cooldown = peashooterCooldowns.getOrDefault(zombie, 0.0) - deltaSeconds;
            if (cooldown > 0) {
                peashooterCooldowns.put(zombie, cooldown);
                continue;
            }
            shootNearestPlantInRow(zombie);
            peashooterCooldowns.put(zombie, PEASHOOTER_COOLDOWN_SECONDS);
        }
    }

    private void shootNearestPlantInRow(Zombie zombie) {
        int row = (int) zombie.getPosition().y();
        double zombieX = zombie.getPosition().x();

        session.getPlants().stream()
                .filter(plant -> plant.isAlive() && (int) plant.getLocation().y() == row
                        && plant.getLocation().x() < zombieX)
                .max((a, b) -> Double.compare(a.getLocation().x(), b.getLocation().x()))
                .ifPresent(plant -> plant.takeDamage(PEASHOOTER_DAMAGE, zombie));
    }

    private void tickJalapenoZombies(double deltaSeconds) {
        for (Map.Entry<Zombie, Double> entry : jalapenoFuses.entrySet()) {
            Zombie zombie = entry.getKey();
            if (!zombie.isAlive()) continue;

            double remaining = entry.getValue() - deltaSeconds;
            entry.setValue(remaining);
            if (remaining <= 0) {
                burnRow((int) zombie.getPosition().y());
                zombie.setHp(0);
                log("Jalapeno Zombie exploded in lane "
                        + ((int) zombie.getPosition().y() + 1) + " after its fuse.");
            }
        }
    }

    private void burnRow(int row) {
        session.getPlants().stream()
                .filter(plant -> plant.isAlive() && (int) plant.getLocation().y() == row)
                .forEach(plant -> plant.takeDamage(plant.getHP(), null));
    }

    private void tickSquashZombies() {
        List<Zombie> squashZombies = new ArrayList<>();
        for (Zombie zombie : session.getZombies()) {
            if (zombie.isAlive() && isAlias(zombie, SQUASH_ZOMBIE)) squashZombies.add(zombie);
        }

        for (Zombie zombie : squashZombies) {
            session.getPlants().stream()
                    .filter(plant -> plant.isAlive() && isTouching(zombie, plant))
                    .findFirst()
                    .ifPresent(plant -> {
                        plant.takeDamage(plant.getHP(), zombie);
                        zombie.setHp(0);
                        log("Squash Zombie crushed " + plant.getName() + " in lane "
                                + ((int) plant.getPosition().y() + 1) + ".");
                    });
        }
    }

    private boolean isTouching(Zombie zombie, Plant plant) {
        return zombie.getPosition() != null && plant.getLocation() != null
                && zombie.getPosition().distanceTo(plant.getLocation()) <= SQUASH_TOUCH_DISTANCE;
    }

    private boolean isAlias(Zombie zombie, String alias) {
        return alias.equals(zombie.getAlias());
    }

    private void forgetDeadZombies() {
        peashooterCooldowns.keySet().removeIf(zombie -> !zombie.isAlive());
        jalapenoFuses.keySet().removeIf(zombie -> !zombie.isAlive());
    }

    public boolean isWon() {
        return session.isWavesStarted() && session.areWavesDone() && !session.isGameOver();
    }

    public boolean isLost() {
        return session.isGameOver();
    }

    public GameSession getSession() { return session; }

    public List<String> getAvailablePlants() { return List.copyOf(availablePlants); }
    public List<String> getZombiePool() { return List.copyOf(zombiePool); }
    public double getJalapenoSpeedMultiplier() { return JALAPENO_SPEED_MULTIPLIER; }

    public List<GroundItem> collectItemsAt(int x, int y) {
        return session.collectItemsNear(new Position(x, y));
    }

    public void addSunCheat(int amount) {
        if (amount <= 0) return;
        session.addSun(amount);
        log("Cheat added " + amount + " sun. Total: " + session.getSunCount() + ".");
    }

    public String renderZombiesInfo() {
        return session.renderZombiesInfo();
    }

    public String renderPlantsInfo() {
        if (session.getPlants().isEmpty()) return "no plants on the field";
        return session.getPlants().stream()
                .filter(Plant::isAlive)
                .map(plant -> plant.getName() + " | hp: " + plant.getHP()
                        + " | position: (" + ((int) plant.getPosition().x() + 1)
                        + ", " + ((int) plant.getPosition().y() + 1) + ")")
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    public boolean plantAt(String plantName, int row, int col) {
        if (row < 0 || row >= session.getRows() || col < 0 || col >= session.getCols()) return false;
        if (availablePlants.stream().noneMatch(name -> name.equalsIgnoreCase(plantName))) return false;

        PlantJsonParser.PlantConfig config = PlantFactory.getBlueprints().values().stream()
                .filter(candidate -> candidate.name.equalsIgnoreCase(plantName))
                .findFirst().orElse(null);
        if (config == null || !session.isPlantReady(config.id)) return false;

        Plant plant = PlantFactory.createPlant(config.id, 1, new Position(col, row));
        if (session.getSunCount() < plant.getCost() || !session.plantAt(row, col, plant)) return false;

        session.spendSun(plant.getCost());
        session.startPlantCooldown(config.id, plant.getRecharge());
        log(plant.getName() + " planted at (" + (col + 1) + ", " + (row + 1)
                + "). Sun: " + session.getSunCount() + ".");
        return true;
    }

    public String renderState() {
        String plantsOnField = session.getPlants().stream()
                .filter(Plant::isAlive)
                .map(plant -> plant.getName() + " at ("
                        + ((int) plant.getLocation().x() + 1) + ", "
                        + ((int) plant.getLocation().y() + 1) + ")"
                        + " hp=" + plant.getHP())
                .collect(java.util.stream.Collectors.joining("\n  "));
        if (plantsOnField.isEmpty()) plantsOnField = "none";

        StringBuilder result = new StringBuilder(getStageDetails()
                + " | Available plants: " + availablePlants
                + " | Zombie pool: " + zombiePool
                + "\n" + session.renderMap()
                + "\nPlants on the field:\n  " + plantsOnField
                + "\nZombies:\n  " + renderZombiesInfo().replace("\n", "\n  "));
        if (!eventLog.isEmpty()) {
            result.append("\nRecent events:\n  ")
                    .append(String.join("\n  ", eventLog));
        }
        return result.toString();
    }

    private void log(String message) {
        eventLog.add(message);
        if (eventLog.size() > 14) eventLog.remove(0);
        GeneralPrinter.print(message);
    }

    private List<String> availablePlantsFor(int level) {
        return switch (level) {
            case 2 -> List.of("Sunflower", "Peashooter", "Repeater", "Wall-nut", "Potato Mine");
            case 3 -> List.of("Sunflower", "Peashooter", "Repeater", "Snow Pea", "Wall-nut", "Tall-nut", "Chomper");
            default -> List.of("Sunflower", "Peashooter", "Wall-nut");
        };
    }

    private List<model.match_mechanisms.ZombieWave> wavesFor(int level) {
        return switch (level) {
            case 2 -> MiniGameWaves.create(session,
                    new double[] {8, 18, 22, 26},
                    new String[][] {
                            {PEASHOOTER_ZOMBIE, SQUASH_ZOMBIE},
                            {JALAPENO_ZOMBIE, WALLNUT_ZOMBIE},
                            {PEASHOOTER_ZOMBIE, PEASHOOTER_ZOMBIE, JALAPENO_ZOMBIE, SQUASH_ZOMBIE},
                            {WALLNUT_ZOMBIE, WALLNUT_ZOMBIE, JALAPENO_ZOMBIE, JALAPENO_ZOMBIE,
                                    PEASHOOTER_ZOMBIE, PEASHOOTER_ZOMBIE, SQUASH_ZOMBIE}
                    });
            case 3 -> MiniGameWaves.create(session,
                    new double[] {7, 16, 20, 24},
                    new String[][] {
                            {WALLNUT_ZOMBIE, JALAPENO_ZOMBIE},
                            {WALLNUT_ZOMBIE, SQUASH_ZOMBIE, PEASHOOTER_ZOMBIE},
                            {WALLNUT_ZOMBIE, JALAPENO_ZOMBIE, SQUASH_ZOMBIE, PEASHOOTER_ZOMBIE},
                            {WALLNUT_ZOMBIE, WALLNUT_ZOMBIE, JALAPENO_ZOMBIE, JALAPENO_ZOMBIE,
                                    SQUASH_ZOMBIE, SQUASH_ZOMBIE, PEASHOOTER_ZOMBIE, PEASHOOTER_ZOMBIE}
                    });
            default -> MiniGameWaves.create(session,
                    new double[] {10, 22, 28},
                    new String[][] {
                            {PEASHOOTER_ZOMBIE},
                            {PEASHOOTER_ZOMBIE, SQUASH_ZOMBIE},
                            {PEASHOOTER_ZOMBIE, PEASHOOTER_ZOMBIE, PEASHOOTER_ZOMBIE,
                                    SQUASH_ZOMBIE, JALAPENO_ZOMBIE}
                    });
        };
    }
}
