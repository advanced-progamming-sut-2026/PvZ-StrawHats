package model.match.mini_games;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

import java.util.*;

/**
 * A normal level (same plant selection, sun, waves, win/loss as usual)
 * except some zombies borrow a plant's ability. Those zombies are otherwise
 * ordinary; their special behavior is driven entirely from here rather than
 * from Zombie itself.
 *
 * Note: the "peashooter zombie" shot is applied as an instant, cooldown-gated
 * hit on the nearest plant in its row rather than a separately animated
 * Projectile, for simplicity — the targeting rule (nearest plant ahead of it,
 * same row) matches a normal Peashooter's.
 */
public class Zombotany extends MiniGameMode {
    private static final Random RAND = new Random();

    private static final String PEASHOOTER_ZOMBIE = "ZombiePeashooter";
    private static final String WALLNUT_ZOMBIE = "ZombieWallnut";
    private static final String JALAPENO_ZOMBIE = "ZombieJalapeno";
    private static final String SQUASH_ZOMBIE = "ZombieSquash";

    private static final double PEASHOOTER_COOLDOWN_SECONDS = 1.5;
    private static final int PEASHOOTER_DAMAGE = 20;
    private static final double JALAPENO_FUSE_SECONDS = 10.0;
    private static final double SQUASH_TOUCH_DISTANCE = 0.4;
    private static final int SQUASH_SPEED_MULTIPLIER = 3;

    private final GameSession session;
    private final Map<Zombie, Double> peashooterCooldowns = new HashMap<>();
    private final Map<Zombie, Double> jalapenoFuses = new HashMap<>();

    public Zombotany(int difficulty) {
        setDifficulty(difficulty);
        this.session = new GameSession(5, 9);
        this.session.addSun(150);
    }

    /**
     * Call once per wave/spawn batch to decide which zombies in it should be
     * Zombotany specials instead of their normal counterparts.
     */
    public Zombie spawnZombieForWave(int row, boolean forceSpecial) {
        String alias = forceSpecial ? pickSpecialAlias() : "ZombieDefault";
        Zombie zombie = ZombieFactory.create(alias, row, session.getEnvironment().getCols() - 1);
        zombie.setPosition(new Position(session.getEnvironment().getCols() - 1, row));

        if (alias.equals(SQUASH_ZOMBIE) && zombie.getSpeed() != null) {
            zombie.setSpeed(zombie.getSpeed().scale(SQUASH_SPEED_MULTIPLIER));
        }
        if (alias.equals(JALAPENO_ZOMBIE)) {
            jalapenoFuses.put(zombie, JALAPENO_FUSE_SECONDS);
        }

        session.spawnZombie(zombie);
        return zombie;
    }

    private String pickSpecialAlias() {
        String[] specials = { PEASHOOTER_ZOMBIE, WALLNUT_ZOMBIE, JALAPENO_ZOMBIE, SQUASH_ZOMBIE };
        return specials[RAND.nextInt(specials.length)];
    }

    public void tick(double deltaSeconds) {
        session.tick();
        tickPeashooterZombies(deltaSeconds);
        tickJalapenoZombies(deltaSeconds);
        tickSquashZombies();
        forgetDeadZombies();
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
}
