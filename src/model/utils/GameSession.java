package model.utils;

import controller.QuestManager;
import model.collections.Item;
import model.collections.item.*;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.collections.zombie.zombie_pushing_item.PushableStructure;
import model.match.main.levels.Level;
import model.match_mechanisms.ZombieWave;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.Environment;
import model.pitches.LawnMower;
import model.projectile.Projectile;
import model.projectile.zombie_projectile.ZombieProjectile;
import model.user_data.User;
import model.user_data.UserState;
import service.GameClock;

import java.util.*;
import java.util.function.ToIntFunction;

public class GameSession {

    public static ToIntFunction<? super Zombie> difficulty = Zombie::getMaxHp;
    private static GameSession instance;
    private static final Random ITEM_RANDOM = new Random();
    private static final double SKY_SUN_INTERVAL = 10.0;

    private final GameClock clock = new GameClock();

    private List<Plant> plants = new ArrayList<>();
    private List<Zombie> zombies = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
    private List<GroundItem> groundItems = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<ZombieProjectile> zombieProjectiles = new ArrayList<>();

    private Level level;
    private List<ZombieWave> waves = new ArrayList<>();
    private int nextWaveIndex = 0;
    private double waveTimer = 0;
    private boolean wavesStarted = false;

    private int sunCount;
    private int plantFoodCount;

    private Environment environment;
    private LawnMower[] lawnMowers;

    private boolean gameOver = false;
    private boolean gameWon = false;
    private int difficultyLevel;
    private int plantsLostThisMatch = 0;

    private double skySunTimer = 0;

    public GameSession() {
        this(5, 9);
    }

    public GameSession(int rows, int cols) {
        setGridSize(rows, cols);
        instance = this;
    }

    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }

    public void setGridSize(int rows, int cols) {
        this.environment = new Environment(rows, cols);
        this.lawnMowers = new LawnMower[rows];
        for (int r = 0; r < rows; r++) {
            lawnMowers[r] = new LawnMower(r);
            lawnMowers[r].setRow(environment.getRowCells(r));
        }
    }

    public void tick() {
        clock.tick();
        double deltaTimeSeconds = GameClock.SECONDS_PER_TICK;

        if (level != null) {
            level.updateTide(deltaTimeSeconds, this);
        }

        for (Plant plant : plants) {
            if (plant.isAlive()) plant.tick(deltaTimeSeconds, this);
        }
        for (Zombie zombie : zombies) {
            if (zombie.isAlive()) zombie.tick(deltaTimeSeconds, this);
        }
        for (Projectile projectile : projectiles) {
            if (projectile.isAlive()) projectile.tick();
        }
        for (ZombieProjectile zombieProjectile : zombieProjectiles) {
            if (zombieProjectile.isAlive()) zombieProjectile.tick();
        }
        for (Item item : items) {
            if (item.isAlive()) item.tick();
        }

        if (wavesStarted) tickWaveScheduler(deltaTimeSeconds);

        if (wavesStarted) {
            skySunTimer += deltaTimeSeconds;
            if (skySunTimer >= SKY_SUN_INTERVAL) {
                skySunTimer = 0;
                int col = ITEM_RANDOM.nextInt(environment.getCols());
                int row = ITEM_RANDOM.nextInt(environment.getRows());
                items.add(GroundSun.fallFromSky(new Position(col, row)));
            }
        }

        for (Zombie zombie : zombies) {
            if (!zombie.isAlive() && zombie.isPlantFoodPending()) {
                items.add(new GroundPlantFood(zombie.getPosition()));
                zombie.clearPlantFoodPending();
            }
        }

        clearDeadPlantsFromGrid();
        refreshZombieOccupancy();

        recordLevelSpecificDeaths();
        tickLevelSpecificLogic(deltaTimeSeconds);

        for (Plant plant : plants) {
            if (!plant.isAlive()) plantsLostThisMatch++;
        }
        plants.removeIf(p -> !p.isAlive());
        zombies.removeIf(z -> !z.isAlive());
        items.removeIf(i -> !i.isAlive());
        projectiles.removeIf(p -> !p.isAlive());
        zombieProjectiles.removeIf(p -> !p.isAlive());

        checkZombieBreaches();

        if (level != null && level.checkLossCondition(this)) {
            gameOver = true;
        }

        if (wavesStarted && allWavesSpawned() && zombies.isEmpty() && !gameOver) {
            if (!gameWon) {
                gameWon = true;
                QuestManager.notifyLevelWon(this);
            }
        }
    }

    private void recordLevelSpecificDeaths() {
        if (level instanceof model.match.main.levels.special_levels.LoveYourPlantsLevel loveLevel) {
            plants.stream().filter(p -> !p.isAlive()).forEach(p -> loveLevel.recordPlantLoss());
        }
        if (level instanceof model.match.main.levels.special_levels.TimedWarLevel timedWarLevel) {
            zombies.stream().filter(z -> !z.isAlive()).forEach(z -> timedWarLevel.recordZombieKill());
            timedWarLevel.tickTimer(GameClock.SECONDS_PER_TICK);
        }
    }

    private void tickLevelSpecificLogic(double deltaTimeSeconds) {
        if (level instanceof model.match.main.levels.special_levels.ConveyorBeltLevel conveyorLevel) {
            conveyorLevel.tickConveyor(deltaTimeSeconds);
        }
    }

    private void clearDeadPlantsFromGrid() {
        for (int r = 0; r < environment.getRows(); r++) {
            for (int c = 0; c < environment.getCols(); c++) {
                Cell cell = environment.getCell(r, c);
                if (cell.getPlant() != null && !cell.getPlant().isAlive()) {
                    cell.setPlant(null);
                }
            }
        }
    }

    private void refreshZombieOccupancy() {
        for (int r = 0; r < environment.getRows(); r++) {
            for (int c = 0; c < environment.getCols(); c++) {
                environment.getCell(r, c).clearZombies();
            }
        }
        for (Zombie zombie : zombies) {
            if (!zombie.isAlive() || zombie.getPosition() == null) continue;
            int col = (int) Math.round(zombie.getPosition().x());
            int row = (int) Math.round(zombie.getPosition().y());
            Cell cell = environment.getCell(row, col);
            if (cell != null) cell.addZombie(zombie);
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
            int row = ITEM_RANDOM.nextInt(environment.getRows());
            zombie.setPosition(new Position(environment.getCols(), row));
            spawnZombie(zombie);
        }
    }

    public boolean allWavesSpawned() {
        return nextWaveIndex >= waves.size();
    }

    public int getTotalWaveCount() {
        return waves.size();
    }

    public int getWavesSpawnedCount() {
        return nextWaveIndex;
    }

    public double getSecondsUntilNextWave() {
        if (allWavesSpawned()) return -1;
        return Math.max(0, waves.get(nextWaveIndex).getDelay() - waveTimer);
    }

    private void checkZombieBreaches() {
        for (Zombie zombie : zombies) {
            if (!zombie.isAlive() || zombie.getPosition() == null) continue;

            if (zombie.getPosition().x() < 0.0) {
                int row = (int) Math.round(zombie.getPosition().y());
                if (row < 0 || row >= lawnMowers.length) continue;

                boolean survived = lawnMowers[row].killZombiesInRow(zombiesInRow(row));
                if (!survived) {
                    onZombieReachedEnd();
                    return;
                }
            }
        }
    }

    private List<Zombie> zombiesInRow(int row) {
        List<Zombie> result = new ArrayList<>();
        for (Zombie zombie : zombies) {
            if (zombie.isAlive() && zombie.getPosition() != null
                    && (int) Math.round(zombie.getPosition().y()) == row) {
                result.add(zombie);
            }
        }
        return result;
    }

    public void spawnZombie(Zombie zombie) {
        if (zombie == null) return;
        zombies.add(zombie);
    }

    public void onZombieReachedEnd() {
        gameOver = true;
    }

    public void notifyZombieDied(Zombie zombie, String killerName) {
        if (zombie == null) return;
        Position dropPosition = zombie.getPosition();
        if (dropPosition == null) return;

        items.add(new GroundCoin(dropPosition, GroundCoin.CoinTier.rollRandom()));

        if (ITEM_RANDOM.nextInt(100) < 10) {
            items.add(new GroundDiamond(dropPosition, 1));
        }

        if (User.currentUser != null) {
            UserState state = User.currentUser.userState;
            if (!state.unlockedPlantIds.isEmpty() && ITEM_RANDOM.nextInt(100) < 5) {
                List<Integer> unlocked = new ArrayList<>(state.unlockedPlantIds);
                int plantId = unlocked.get(ITEM_RANDOM.nextInt(unlocked.size()));
                items.add(new GroundSeedPack(dropPosition, plantId, 1));
            }
        }

        reportKillToQuests(killerName);
    }

    private void reportKillToQuests(String killerName) {
        boolean killedByPlant = killerName != null && !killerName.equals("Unknown");

        Map<String, Object> plantContext = new HashMap<>();
        plantContext.put("plantType", killedByPlant ? killerName.toLowerCase().replace(" ", "").replace("-", "") : "none");
        plantContext.put("isOffensive", killedByPlant);
        QuestManager.updateProgress("KILL_ZOMBIES_WITH_SPECIFIC_PLANT", 1, plantContext);

        Map<String, Object> chapterContext = new HashMap<>();
        chapterContext.put("chapter", level != null ? level.getId() : "unknown");
        QuestManager.updateProgress("KILL_ZOMBIES_IN_CHAPTER", 1, chapterContext);
    }

    public List<GroundItem> collectItemsNear(Position target) {
        List<GroundItem> collectedItems = new ArrayList<>();
        if (User.currentUser == null || target == null) return collectedItems;

        UserState state = User.currentUser.userState;
        for (Item item : items) {
            if (item instanceof GroundItem groundItem
                    && groundItem.isAlive()
                    && !groundItem.isCollected()
                    && groundItem.isNear(target)) {
                groundItem.collect(this, state);
                collectedItems.add(groundItem);
            }
        }
        return collectedItems;
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
        if (amount > 0) {
            QuestManager.updateProgress("COLLECT_SUN", amount, Collections.emptyMap());
        }
    }

    public int getPlantsLostThisMatch() {
        return plantsLostThisMatch;
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

    public boolean plantAt(int row, int col, Plant plant) {
        Cell cell = environment.getCell(row, col);
        if (cell == null || cell.hasPlant() || plant == null) return false;

        cell.setPlant(plant);
        plant.setPosition(new Position(col, row));
        plants.add(plant);

        if (plant.getTags() != null && plant.getTags().contains(model.collections.plant.PlantTag.EXPLOSIVE)) {
            QuestManager.updateProgress("USE_EXPLOSIVE_PLANTS", 1, Collections.emptyMap());
        }

        return true;
    }

    public boolean removePlantAt(int row, int col) {
        Cell cell = environment.getCell(row, col);
        if (cell == null || !cell.hasPlant()) return false;

        Plant plant = cell.getPlant();
        plant.setAlive(false);
        cell.setPlant(null);
        plants.remove(plant);
        return true;
    }

    private Plant findPlantAt(int row, int col) {
        Cell cell = environment.getCell(row, col);
        return (cell != null && cell.hasPlant()) ? cell.getPlant() : null;
    }

    public String renderMap() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < environment.getRows(); r++) {
            for (int c = 0; c < environment.getCols(); c++) {
                sb.append(findPlantAt(r, c) != null ? "P" : ".");
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
        Plant plant = findPlantAt(row, col);
        StringBuilder sb = new StringBuilder();
        sb.append("tile (").append(col).append(", ").append(row).append("): ");

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

    public int getRows() { return environment.getRows(); }
    public int getCols() { return environment.getCols(); }
    public Environment getEnvironment() { return environment; }

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
        if (level != null) {
            level.initSpecial(this);
        }
    }

    public void setWaves(List<ZombieWave> waves) {
        this.waves = waves != null ? waves : new ArrayList<>();
        this.nextWaveIndex = 0;
        this.waveTimer = 0;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<GroundItem> getGroundItems() {
        return groundItems;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setGroundItems(List<GroundItem> groundItems) {
        this.groundItems = groundItems;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public void addZombieProjectile(ZombieProjectile projectile) {
        if (projectile != null) zombieProjectiles.add(projectile);
    }

    public List<ZombieProjectile> getZombieProjectiles() {
        return zombieProjectiles;
    }

    public double getElapsedSeconds() {
        return clock.getElapsedSeconds();
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }


    public void registerStructure(PushableStructure structure) {
        if (structure == null || environment == null) return;
        int row = (int) structure.getPosition().y();
        int col = (int) structure.getPosition().x();
        Cell cell = environment.getCell(row, col);
        if (cell != null) {
            cell.setStructure(structure);
        }
    }

    public Environment getLawn() {
        return environment;
    }
}