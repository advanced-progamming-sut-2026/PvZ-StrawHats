package model.match.main.levels;

import model.match.main.season.Season;
import model.match_mechanisms.ZombieWave;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class Level {
    protected int id;
    protected String name;
    protected Season season;
    protected int rows = 5;
    protected int cols = 9;
    protected int initialSun = 150;
    protected String gameMode = "Adventure - Normal";
    protected List<ZombieWave> waves;
    protected List<String> availablePlants;
    protected List<String> forcedPlants;
    protected List<String> zombiePool = new ArrayList<>();

    // Tide support (used for Big Wave Beach)
    protected int currentTideColumn = 0;      // 0 = no water, >0 = water covers that many columns from right
    protected double tideTimer = 0;
    protected int maxTideColumn = 3;          // default, can be overridden per level

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public int getCols() { return cols; }
    public void setCols(int cols) { this.cols = cols; }
    public int getInitialSun() { return initialSun; }
    public void setInitialSun(int initialSun) { this.initialSun = initialSun; }
    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }
    public List<ZombieWave> getWaves() { return waves; }
    public void setWaves(List<ZombieWave> waves) { this.waves = waves; }
    public List<String> getAvailablePlants() { return availablePlants; }
    public void setAvailablePlants(List<String> availablePlants) { this.availablePlants = availablePlants; }
    public List<String> getForcedPlants() { return forcedPlants; }
    public void setForcedPlants(List<String> forcedPlants) { this.forcedPlants = forcedPlants; }
    public List<String> getZombiePool() { return zombiePool; }
    public void setZombiePool(List<String> zombiePool) {
        this.zombiePool = zombiePool == null ? new ArrayList<>() : new ArrayList<>(new LinkedHashSet<>(zombiePool));
    }

    // ---- Tide ----
    public int getCurrentTideColumn() { return currentTideColumn; }
    public void setCurrentTideColumn(int currentTideColumn) { this.currentTideColumn = currentTideColumn; }
    public int getMaxTideColumn() { return maxTideColumn; }
    public void setMaxTideColumn(int maxTideColumn) { this.maxTideColumn = maxTideColumn; }

    /**
     * Update tide level based on elapsed time.
     * Override in Beach levels to implement dynamic tide changes.
     * Default does nothing (no tide).
     */
    public void updateTide(double deltaSeconds, GameSession session) {
        // Default: no tide
    }

    /**
     * Called when the level is loaded into GameSession.
     * Override to place forced plants, set initial tide, etc.
     */
    public void initSpecial(GameSession session) {
        // default no‑op
    }

    /**
     * Per-level loss condition, checked every tick alongside the normal
     * "zombie reached the house" rule. Override in special levels that can
     * end the match on their own (Dead Line, Save Our Seeds, Love Your Plants...).
     */
    public boolean checkLossCondition(GameSession session) {
        return false;
    }

    /** Default victory: every configured wave spawned and no live zombies remain. */
    public boolean checkWinCondition(GameSession session) {
        return session != null
                && session.isWavesStarted()
                && session.allWavesSpawned()
                && session.getZombies().isEmpty();
    }

    /**
     * Whether sun should keep falling from the sky on this level.
     * Night Ops and Plant What You Get turn this off.
     */
    public boolean isSkySunEnabled() {
        return season == null || !season.isNight();
    }
}
