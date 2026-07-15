package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import java.util.List;

public class LockedPlantsLevel extends Level {
    private List<String> lockedPlants;
    private List<String> alwaysAvailable;

    public List<String> getLockedPlants() { return lockedPlants; }
    public void setLockedPlants(List<String> lockedPlants) { this.lockedPlants = lockedPlants; }
    public List<String> getAlwaysAvailable() { return alwaysAvailable; }
    public void setAlwaysAvailable(List<String> alwaysAvailable) { this.alwaysAvailable = alwaysAvailable; }
}