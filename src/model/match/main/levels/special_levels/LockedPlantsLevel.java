package model.match.main.levels.special_levels;

import model.match.main.levels.Level;

import java.util.List;

public class LockedPlantsLevel extends Level {
    private List<String> lockedPlants;
    private List<String> alwaysAvailable;

    /**
     * True if this plant is not selectable in this level: either it is
     * explicitly locked, or it simply isn't in the available/forced lists.
     */
    public boolean isPlantLocked(String plantAlias) {
        if (lockedPlants != null && lockedPlants.contains(plantAlias)) return true;
        if (alwaysAvailable != null && alwaysAvailable.contains(plantAlias)) return false;
        boolean inAvailable = getAvailablePlants() != null && getAvailablePlants().contains(plantAlias);
        boolean inForced = getForcedPlants() != null && getForcedPlants().contains(plantAlias);
        return !inAvailable && !inForced;
    }

    public List<String> getLockedPlants() { return lockedPlants; }
    public void setLockedPlants(List<String> lockedPlants) { this.lockedPlants = lockedPlants; }
    public List<String> getAlwaysAvailable() { return alwaysAvailable; }
    public void setAlwaysAvailable(List<String> alwaysAvailable) { this.alwaysAvailable = alwaysAvailable; }
}