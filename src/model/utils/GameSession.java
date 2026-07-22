package model.utils;

import model.collections.Item;
import model.collections.item.*;
import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.collections.plant.PlantTag;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.collections.zombie.zombie_pushing_item.PushableStructure;
import model.match.main.levels.Level;
import model.match.main.season.travellog.egypt.Egypt;
import model.match.main.season.travellog.egypt.SandStorm;
import model.match_mechanisms.ZombieWave;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.Environment;
import model.pitches.LawnMower;
import model.pitches.Tile;
import model.pitches.TileType;
import model.projectile.Projectile;
import model.projectile.zombie_projectile.ZombieProjectile;
import model.user_data.User;
import model.user_data.UserState;
import service.GameClock;
import controller.QuestManager;
import view.GeneralPrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import java.util.function.ToIntFunction;

public class GameSession {

    public static ToIntFunction<? super Zombie> difficulty = Zombie::getMaxHp;
    private static GameSession instance;
    private static final Random ITEM_RANDOM = new Random();
    private static final double MIN_SKY_SUN_INTERVAL = 12.0;
    private static final double SKY_SUN_INTERVAL_START = 6.0;
    private static final double SKY_SUN_INTERVAL_GROWTH = 0.05;

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
    private double wavesStartedAtSeconds = 0;

    private List<Zombie> currentWaveZombies = new ArrayList<>();
    private int currentWaveStartingHp = 0;
    private double previousWaveMultiplier = 1.0;
    private static final double HUGE_WAVE_ALERT_LEAD_SECONDS = 5.0;
    private boolean hugeWaveAlertShown = false;

    private int sunCount;
    private int plantFoodCount;

    private Environment environment;
    private LawnMower[] lawnMowers;

    private boolean gameOver = false;
    private boolean gameWon = false;
    private int difficultyLevel;
    private int plantsLostThisMatch = 0;
    private final Set<String> plantFamiliesUsedThisMatch = new HashSet<>();
    private boolean plantedAnyPlantThisMatch = false;
    private boolean usedNonNightPlantThisMatch = false;
    private final Map<Integer, Double> plantCooldowns = new HashMap<>();

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

    /** Returns the active session without creating one as a side effect. */
    public static GameSession peekInstance() {
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
        if (gameOver || gameWon) return;

        clock.tick();
        double deltaTimeSeconds = GameClock.SECONDS_PER_TICK;

        plantCooldowns.replaceAll((plantId, remaining) -> GameClock.countDown(remaining, deltaTimeSeconds));
        plantCooldowns.entrySet().removeIf(entry -> GameClock.isZero(entry.getValue()));

        if (level != null) {
            level.updateTide(deltaTimeSeconds, this);
            if (level.getSeason() != null) {
                level.getSeason().applyPerTickEffect(this, deltaTimeSeconds);
            }
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

        if (wavesStarted && (level == null || level.isSkySunEnabled())) {
            skySunTimer += deltaTimeSeconds;
            if (GameClock.hasReached(skySunTimer, getEffectiveSkySunInterval())) {
                skySunTimer = 0;
                int col = ITEM_RANDOM.nextInt(environment.getCols());
                int row = ITEM_RANDOM.nextInt(environment.getRows());
                GroundSun sun = GroundSun.fallFromSky(new Position(col, row));
                items.add(sun);
                GeneralPrinter.print("New " + sun.getDropType().name().toLowerCase()
                        + " sun is dropping at position (" + (col + 1) + ", " + (row + 1) + ").");
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

        boolean levelWon = level != null
                ? level.checkWinCondition(this)
                : wavesStarted && allWavesSpawned() && zombies.isEmpty();
        if (levelWon && !gameOver) {
            if (!gameWon) {
                gameWon = true;
                QuestManager.notifyLevelWon(this);
            }
        }
    }

    private double getEffectiveSkySunInterval() {
        double elapsed = getElapsedSecondsSinceWavesStarted();
        double baseInterval = Math.max(SKY_SUN_INTERVAL_START
                + SKY_SUN_INTERVAL_GROWTH * elapsed, MIN_SKY_SUN_INTERVAL);
        if (difficultyLevel <= 0) return baseInterval;
        return baseInterval * (difficultyLevel / 3.0);
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

        if (nextWave.isFinalWave() && !hugeWaveAlertShown
                && GameClock.hasReached(waveTimer,
                Math.max(0, nextWave.getDelay() - HUGE_WAVE_ALERT_LEAD_SECONDS))) {
            hugeWaveAlertShown = true;
            GeneralPrinter.print("A huge wave of zombies is approaching!");
        }

        if (!GameClock.hasReached(waveTimer, nextWave.getDelay())) return;
        if (!previousWaveMostlyCleared()) return;

        spawnWave(nextWave);
        nextWaveIndex++;
        waveTimer = 0;
    }

    private boolean previousWaveMostlyCleared() {
        if (currentWaveZombies.isEmpty()) return true;
        int remainingHp = currentWaveZombies.stream()
                .filter(Zombie::isAlive)
                .mapToInt(Zombie::getHp)
                .sum();
        return remainingHp <= currentWaveStartingHp * 0.25;
    }

    private void spawnWave(ZombieWave wave) {
        if (wave.getWaveZombies() == null) return;

        int waveNumber = nextWaveIndex + 1;
        double multiplier;
        if (wave.isFinalWave()) {
            multiplier = previousWaveMultiplier * 2.0;
            GeneralPrinter.print("The final wave has come.");
        } else {
            multiplier = (waveNumber == 1) ? 1.0 : previousWaveMultiplier * 1.25;
            GeneralPrinter.print("Wave " + waveNumber + " started.");
        }
        previousWaveMultiplier = multiplier;

        if (level != null && level.getSeason() != null) {
            level.getSeason().onWaveStart(this, nextWaveIndex);
        }

        currentWaveZombies = new ArrayList<>();
        int totalHp = 0;

        for (Zombie template : wave.getWaveZombies()) {
            int lane = ITEM_RANDOM.nextInt(getRows());
            int spawnColumn = getCols() - 1;
            Zombie zombie = ZombieFactory.create(template.getAlias(), lane, spawnColumn);
            if (wave.isFinalWave() && level != null && level.getSeason() instanceof Egypt) {
                spawnColumn = Math.max(0, spawnColumn - SandStorm.sandstorm());
            }
            zombie.setPosition(new Position(spawnColumn, lane));

            Position speed = zombie.getSpeed();
            if (speed != null) {
                zombie.setSpeed(new Position(-Math.abs(speed.x()), 0));
            }

            if (multiplier != 1.0) {
                zombie.setMaxHp((int) Math.round(zombie.getMaxHp() * multiplier));
                zombie.setHp(zombie.getMaxHp());
                zombie.setEatDps(zombie.getEatDps() * multiplier);
            }

            int cost = ZombieFactory.getZombieCost(zombie.getAlias());
            GeneralPrinter.print("Zombie " + zombie.getName() + " spawned at wave " + waveNumber
                    + " in lane " + (lane + 1) + " which cost " + cost + ".");

            spawnZombie(zombie);
            currentWaveZombies.add(zombie);
            totalHp += zombie.getHp();
        }

        currentWaveStartingHp = totalHp;
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

        int displayX = (int) Math.round(dropPosition.x()) + 1;
        int displayY = (int) Math.round(dropPosition.y()) + 1;
        GeneralPrinter.print("Zombie of type " + zombie.getName() + " is dead at ("
                + displayX + ", " + displayY + ").");

        GroundCoin coin = new GroundCoin(dropPosition, GroundCoin.CoinTier.rollRandom());
        items.add(coin);
        GeneralPrinter.print("A " + coin.getTier().name().toLowerCase()
                + " coin dropped at (" + displayX + ", " + displayY + ").");

        if (ITEM_RANDOM.nextInt(100) < 10) {
            items.add(new GroundDiamond(dropPosition, 1));
            GeneralPrinter.print("A diamond dropped at (" + displayX + ", " + displayY + ").");
        }

        if (User.currentUser != null) {
            UserState state = User.currentUser.userState;
            if (!state.unlockedPlantIds.isEmpty() && ITEM_RANDOM.nextInt(100) < 5) {
                List<Integer> unlocked = new ArrayList<>(state.unlockedPlantIds);
                int plantId = unlocked.get(ITEM_RANDOM.nextInt(unlocked.size()));
                items.add(new GroundSeedPack(dropPosition, plantId, 1));
                GeneralPrinter.print("A seed pack dropped at (" + displayX + ", " + displayY + ").");
            }
        }

        QuestManager.notifyZombieKilled(this, zombie, killerName);
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
                announceCollection(groundItem, state);
            }
        }
        return collectedItems;
    }

    private void announceCollection(GroundItem item, UserState state) {
        switch (item.getItemType()) {
            case SUN -> GeneralPrinter.print("You collected a sun; you have " + getSunCount() + " sun now.");
            case PLANT_FOOD -> GeneralPrinter.print("The glowing zombie dropped a plant food; you have " + getPlantFoodCount() + " plant foods now.");
            case COIN -> GeneralPrinter.print("A zombie dropped a coin; you have " + state.coins + " coins now.");
            case DIAMOND -> GeneralPrinter.print("A zombie dropped a diamond; you have " + state.diamonds + " diamonds now.");
            case SEED_PACK -> {
                int totalPots = state.seedPacketInventory.values().stream().mapToInt(Integer::intValue).sum();
                GeneralPrinter.print("A zombie dropped a pot; you have " + totalPots + " pots now.");
            }
            default -> {
            }
        }
    }

    public void startWaves() {
        if (wavesStarted) return;
        ZombieFactory.init();
        nextWaveIndex = 0;
        waveTimer = 0;
        skySunTimer = 0;
        wavesStartedAtSeconds = clock.getElapsedSeconds();
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

    private static final int MAX_PLANT_FOOD = 3;

    public int getPlantFoodCount() {
        return plantFoodCount;
    }

    public boolean addPlantFood() {
        if (plantFoodCount >= MAX_PLANT_FOOD) return false;
        plantFoodCount++;
        return true;
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
        plantCooldowns.clear();
    }

    public boolean isPlantReady(int plantId) {
        return GameClock.isZero(plantCooldowns.getOrDefault(plantId, 0.0));
    }

    public double getPlantCooldown(int plantId) {
        return plantCooldowns.getOrDefault(plantId, 0.0);
    }

    public void startPlantCooldown(int plantId, double seconds) {
        if (seconds > 0) plantCooldowns.put(plantId, seconds);
    }

    public boolean plantAt(int row, int col, Plant plant) {
        Cell cell = environment.getCell(row, col);
        if (cell == null || cell.hasPlant() || cell.getObstacle() != null || plant == null) return false;

        cell.setPlant(plant);
        plant.setPosition(new Position(col, row));
        plants.add(plant);

        plantedAnyPlantThisMatch = true;
        boolean nightPlant = false;
        if (plant.getTags() != null) {
            for (PlantTag tag : plant.getTags()) {
                plantFamiliesUsedThisMatch.add(tag.getName().toLowerCase());
                plantFamiliesUsedThisMatch.add(tag.name().toLowerCase());
            }
            nightPlant = plant.getTags().contains(PlantTag.NIGHT)
                    || plant.getTags().contains(PlantTag.SHROOM);
        }
        if (!nightPlant) usedNonNightPlantThisMatch = true;

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

    public Plant digPlantAt(int row, int col) {
        Cell cell = environment.getCell(row, col);
        if (cell == null || !cell.hasPlant()) return null;

        Plant plant = cell.getPlant();
        cell.setPlant(null);
        plants.remove(plant);
        return plant;
    }

    private Plant findPlantAt(int row, int col) {
        Cell cell = environment.getCell(row, col);
        return (cell != null && cell.hasPlant()) ? cell.getPlant() : null;
    }

    public String renderMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wave: ").append(getWavesSpawnedCount()).append("/").append(getTotalWaveCount())
                .append(" | Sun: ").append(sunCount)
                .append(" | Plant food: ").append(plantFoodCount);
        if (!allWavesSpawned()) {
            sb.append(" | Next wave: ")
                    .append(String.format("%.1fs", getSecondsUntilNextWave()));
        }
        sb.append("\n");
        for (int r = 0; r < environment.getRows(); r++) {
            sb.append(r + 1).append(lawnMowers[r].isUsed() ? " [ ] " : " [M] ");
            for (int c = 0; c < environment.getCols(); c++) {
                sb.append(mapSymbolFor(environment.getCell(r, c)));
            }
            sb.append("\n");
        }
        sb.append("(M=lawn mower, P=plant, Z=zombie, E=zombie eating a plant, X=obstacle, ~=ice, .=empty)");
        List<GroundItem> visibleItems = items.stream()
                .filter(GroundItem.class::isInstance)
                .map(GroundItem.class::cast)
                .filter(Item::isAlive)
                .toList();
        if (!visibleItems.isEmpty()) {
            sb.append("\nGround items:");
            for (GroundItem item : visibleItems) {
                Position position = item.getPosition();
                if (position == null) continue;
                sb.append("\n  ").append(item.getItemType().name().toLowerCase())
                        .append(" at (").append((int) Math.round(position.x()) + 1)
                        .append(", ").append((int) Math.round(position.y()) + 1).append(")");
                if (item instanceof GroundSun sun && sun.isFalling()) sb.append(" [falling]");
            }
        }
        return sb.toString().trim();
    }

    private char mapSymbolFor(Cell cell) {
        if (cell == null) return '?';
        boolean hasZombie = !cell.getZombies().isEmpty();
        boolean hasPlant = cell.hasPlant();
        if (hasZombie && hasPlant) return 'E';
        if (hasZombie) return 'Z';
        if (hasPlant) return 'P';
        if (cell.getObstacle() != null) return 'X';
        if (cell.getTile() != null && cell.getTile().getType() == TileType.Slippery) return '~';
        return '.';
    }

    public String renderPlantsStatus() {
        StringBuilder sb = new StringBuilder("Loadout planting status:");
        List<String> selected = controller.menus.match.BeforeMenu.selectedPlants;
        if (selected.isEmpty()) {
            sb.append("\n  no plants selected");
        }
        for (String selectedName : selected) {
            PlantJsonParser.PlantConfig config = PlantFactory.getBlueprints().values().stream()
                    .filter(candidate -> candidate.name.equalsIgnoreCase(selectedName))
                    .findFirst().orElse(null);
            if (config == null) continue;
            double cooldown = getPlantCooldown(config.id);
            sb.append("\n  ").append(config.name)
                    .append(" | cost: ").append(config.cost)
                    .append(" | ").append(GameClock.isZero(cooldown)
                            ? "ready" : String.format("recharging: %.1fs", cooldown));
        }
        sb.append("\nPlants on the field:");
        if (plants.isEmpty()) {
            sb.append("\n  none");
            return sb.toString();
        }
        for (Plant plant : plants) {
            Position position = plant.getPosition();
            sb.append("\n  ").append(plant.getName())
                    .append(" | hp: ").append(plant.getHP())
                    .append(" | level: ").append(plant.getLevel());
            if (position != null) {
                sb.append(" | position: (").append((int) position.x() + 1)
                        .append(", ").append((int) position.y() + 1).append(")");
            }
        }
        return sb.toString();
    }

    public String renderTileStatus(int row, int col) {
        Cell cell = environment.getCell(row, col);
        StringBuilder sb = new StringBuilder();
        sb.append("tile (").append(col + 1).append(", ").append(row + 1).append("): ");

        if (cell == null) {
            sb.append("out of bounds");
            return sb.toString();
        }

        List<String> parts = new ArrayList<>();

        if (cell.hasPlant()) {
            Plant plant = cell.getPlant();
            parts.add("plant=" + plant.getName() + " hp=" + plant.getHP() + " level=" + plant.getLevel());
        } else {
            parts.add("no plant");
        }

        if (cell.getObstacle() != null) {
            parts.add("obstacle=" + cell.getObstacle().getName());
        }

        if (cell.getTile() != null) {
            Tile tile = cell.getTile();
            String terrain = tile.getType().toString();
            if (tile.getSlipperyDirection() != null) {
                terrain += " (" + tile.getSlipperyDirection() + ")";
            }
            parts.add("terrain=" + terrain);
        }

        if (cell.getStructure() != null) {
            parts.add("structure=" + cell.getStructure().getClass().getSimpleName());
        }

        if (level != null && level.getSeason() != null && level.getSeason().hasTide()
                && col >= environment.getCols() - level.getCurrentTideColumn()) {
            parts.add("flooded (tide)");
        }

        if (!cell.getZombies().isEmpty()) {
            parts.add("zombies=" + cell.getZombies().size());
        }

        sb.append(String.join(", ", parts));
        return sb.toString();
    }

    public String renderZombiesInfo() {
        if (zombies.isEmpty()) return "no zombies on the field";
        StringBuilder sb = new StringBuilder();
        for (Zombie zombie : zombies) {
            Position position = zombie.getPosition();
            sb.append(zombie.getName())
                    .append(" | hp: ").append(zombie.getHp())
                    .append("/").append(zombie.getMaxHp());
            if (position != null) {
                sb.append(" | position: (").append(String.format("%.2f", position.x() + 1))
                        .append(", ").append((int) Math.round(position.y()) + 1).append(")");
            }
            if (zombie.getArmor() != null && zombie.getArmor().getHP() > 0) {
                sb.append(" | armor: ").append(zombie.getArmor().getHP());
            }
            sb.append(" | state: ").append(zombie.getZombieState())
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
            setGridSize(level.getRows(), level.getCols());
            plants.clear();
            zombies.clear();
            items.clear();
            groundItems.clear();
            projectiles.clear();
            zombieProjectiles.clear();
            plantCooldowns.clear();
            clock.reset();
            gameOver = false;
            gameWon = false;
            wavesStarted = false;
            wavesStartedAtSeconds = 0;
            plantsLostThisMatch = 0;
            plantFamiliesUsedThisMatch.clear();
            plantedAnyPlantThisMatch = false;
            usedNonNightPlantThisMatch = false;
            plantFoodCount = 0;
            sunCount = level.getInitialSun();
            setWaves(level.getWaves());
            QuestManager.notifyLevelStarted(this);
            level.initSpecial(this);
            if (level.getSeason() != null) {
                level.getSeason().placeSeasonObstacles(this);
            }
        }
    }

    public void setWaves(List<ZombieWave> waves) {
        this.waves = waves != null ? waves : new ArrayList<>();
        this.nextWaveIndex = 0;
        this.waveTimer = 0;
        this.previousWaveMultiplier = 1.0;
        this.currentWaveZombies = new ArrayList<>();
        this.currentWaveStartingHp = 0;
        this.hugeWaveAlertShown = false;
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

    public double getElapsedSecondsSinceWavesStarted() {
        return wavesStarted ? Math.max(0, clock.getElapsedSeconds() - wavesStartedAtSeconds) : 0;
    }

    public boolean hasUsedPlantFamily(String family) {
        return family != null && plantFamiliesUsedThisMatch.contains(family.trim().toLowerCase());
    }

    public boolean usedOnlyNightPlants() {
        return plantedAnyPlantThisMatch && !usedNonNightPlantThisMatch;
    }

    public boolean isLawnMowerUsed(int row) {
        return row >= 0 && row < lawnMowers.length && lawnMowers[row].isUsed();
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
