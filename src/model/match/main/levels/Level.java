package model.match.main.levels;

import model.match.main.season.Season;
import model.match_mechanisms.ZombieWave;
import model.utils.GameSession;
import java.util.List;

public abstract class Level {
    protected int id;
    protected String name;
    protected Season season;
    protected int rows = 5;
    protected int cols = 9;
    protected int initialSun = 150;
    protected List<ZombieWave> waves;
    protected List<String> availablePlants;
    protected List<String> forcedPlants;

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
    public List<ZombieWave> getWaves() { return waves; }
    public void setWaves(List<ZombieWave> waves) { this.waves = waves; }
    public List<String> getAvailablePlants() { return availablePlants; }
    public void setAvailablePlants(List<String> availablePlants) { this.availablePlants = availablePlants; }
    public List<String> getForcedPlants() { return forcedPlants; }
    public void setForcedPlants(List<String> forcedPlants) { this.forcedPlants = forcedPlants; }

    public void initSpecial(GameSession session) {}
}