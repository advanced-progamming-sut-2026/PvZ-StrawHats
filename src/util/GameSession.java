package util;

import model.collections.Item;
import model.collections.item.GroundPlantFood;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match.main.levels.Level;
import model.match_mechanisms.ZombieWave;
import model.match_mechanisms.vector.Position;
import model.projectile.Projectile;
import service.GameClock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameSession {

    private final GameClock clock = new GameClock();

    private List<Plant> plants = new ArrayList<>();
    private List<Zombie> zombies = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    private Level level;
    private List<ZombieWave> waves = new ArrayList<>();
    private int nextWaveIndex = 0;
    private double waveTimer = 0;
    private boolean wavesStarted = false;

    private int sunCount;
    private int plantFoodCount;

    private int rows = 5;
    private int cols = 9;
    private boolean[] lawnMowerAvailable = new boolean[rows];

    private boolean gameOver = false;
    private boolean gameWon = false;

    public GameSession() {
        Arrays.fill(lawnMowerAvailable, true);
    }

    public void tick() {
        clock.tick();
        double deltaTimeSeconds = GameClock.SECONDS_PER_TICK;

        for (Plant plant : plants) {
            if (plant.isAlive()) plant.tick(deltaTimeSeconds, this);
        }
        for (Zombie zombie : zombies) {
            if (zombie.isAlive()) zombie.tick(deltaTimeSeconds, this);
        }
        for (Projectile projectile : projectiles) {
            if (projectile.isAlive()) projectile.tick();
        }
        for (Item item : items) {
            if (item.isAlive()) item.tick();
        }

        if (wavesStarted) tickWaveScheduler(deltaTimeSeconds);

        for (Zombie zombie : zombies) {
            if (!zombie.isAlive() && zombie.isPlantFoodPending()) {
                items.add(new GroundPlantFood(zombie.getPosition()));
                zombie.clearPlantFoodPending();
            }
        }

        plants.removeIf(p -> !p.isAlive());
        zombies.removeIf(z -> !z.isAlive());
        items.removeIf(i -> !i.isAlive());
        projectiles.removeIf(p -> !p.isAlive());

        checkZombieBreaches();

        if (wavesStarted && allWavesSpawned() && zombies.isEmpty() && !gameOver) {
            gameWon = true;
        }
    }

    private void tickWaveScheduler(double deltaTimeSeconds) {
        if (nextWaveIndex >= waves.size()) return;

        waveTimer += deltaTimeSeconds;
        ZombieWave nextWave = waves.get(nextWaveIndex);

        if (waveTimer >= nextWave.getDelay()) {
            spawnWave(nextWave);
            nextWaveIndex++;
            waveTimer = 0;
        }
    }

    private void spawnWave(ZombieWave wave) {
        if (wave.getWaveZombies() == null) return;
        for (Zombie zombie : wave.getWaveZombies()) {
            spawnZombie(zombie);
        }
    }

    public boolean allWavesSpawned() {
        return nextWaveIndex >= waves.size();
    }

    private void checkZombieBreaches() {
        for (Zombie zombie : zombies) {
            if (!zombie.isAlive() || zombie.getPosition() == null) continue;

            if (zombie.getPosition().x() < 0.0) {
                int row = (int) zombie.getPosition().y();

                if (row >= 0 && row < lawnMowerAvailable.length && lawnMowerAvailable[row]) {
                    lawnMowerAvailable[row] = false;
                    triggerLawnMower(row);
                } else {
                    onZombieReachedEnd();
                    break;
                }
            }
        }
    }

    private void triggerLawnMower(int row) {
        for (Zombie zombie : zombies) {
            if (zombie.isAlive() && zombie.getPosition() != null && (int) zombie.getPosition().y() == row) {
                zombie.setHp(0);
            }
        }
    }

    public void spawnZombie(Zombie zombie) {
        if (zombie == null) return;
        zombies.add(zombie);
    }

    public void onZombieReachedEnd() {
        gameOver = true;
    }

    public void startWaves() {
        ZombieFactory.init();
        nextWaveIndex = 0;
        waveTimer = 0;
        wavesStarted = true;
    }

    public boolean isWavesStarted() {
        return wavesStarted;
    }

    public boolean areWavesDone() {
        return wavesStarted && allWavesSpawned() && zombies.isEmpty();
    }

    public int getSunCount() {
        return sunCount;
    }

    public void addSun(int amount) {
        sunCount += amount;
    }

    public boolean spendSun(int amount) {
        if (sunCount < amount) return false;
        sunCount -= amount;
        return true;
    }

    public int getPlantFoodCount() {
        return plantFoodCount;
    }

    public void addPlantFood() {
        plantFoodCount++;
    }

    public boolean spendPlantFood() {
        if (plantFoodCount <= 0) return false;
        plantFoodCount--;
        return true;
    }

    public void killAllZombies() {
        zombies.forEach(z -> z.setHp(0));
        zombies.clear();
    }

    public void removeAllCooldowns() {
        plants.forEach(p -> p.setInternalTimer(0));
    }

    private Plant findPlantAt(double x, double y) {
        for (Plant plant : plants) {
            Position loc = plant.getLocation();
            if (plant.isAlive() && loc != null && loc.x() == x && loc.y() == y) {
                return plant;
            }
        }
        return null;
    }

    public boolean removePlantAt(int x, int y) {
        Plant plant = findPlantAt(x, y);
        if (plant == null) return false;

        plant.setAlive(false);
        plants.remove(plant);
        return true;
    }

    public String renderMap() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                sb.append(findPlantAt(c, r) != null ? "P" : ".");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    public String renderPlantsStatus() {
        if (plants.isEmpty()) return "no plants on the field";
        StringBuilder sb = new StringBuilder();
        for (Plant plant : plants) {
            sb.append(plant.getName())
                    .append(" | hp: ").append(plant.getHP())
                    .append(" | level: ").append(plant.getLevel())
                    .append("\n");
        }
        return sb.toString().trim();
    }

    public String renderTileStatus(int row, int col) {
        Plant plant = findPlantAt(col, row);
        StringBuilder sb = new StringBuilder();
        sb.append("tile (").append(row).append(", ").append(col).append("): ");

        if (plant != null) {
            sb.append("plant=").append(plant.getName()).append(" hp=").append(plant.getHP());
        } else {
            sb.append("empty");
        }
        return sb.toString();
    }

    public String renderZombiesInfo() {
        if (zombies.isEmpty()) return "no zombies on the field";
        StringBuilder sb = new StringBuilder();
        for (Zombie zombie : zombies) {
            sb.append(zombie.getName())
                    .append(" | hp: ").append(zombie.getHp())
                    .append(" | row: ").append((int) zombie.getPosition().y())
                    .append("\n");
        }
        return sb.toString().trim();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public void setGridSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.lawnMowerAvailable = new boolean[rows];
        Arrays.fill(lawnMowerAvailable, true);
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public List<Zombie> getZombies() {
        return zombies;
    }

    public void setZombies(List<Zombie> zombies) {
        this.zombies = zombies;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setWaves(List<ZombieWave> waves) {
        this.waves = waves != null ? waves : new ArrayList<>();
        this.nextWaveIndex = 0;
        this.waveTimer = 0;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public double getElapsedSeconds() {
        return clock.getElapsedSeconds();
    }
}
