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
        if (containsIgnoreCase(lockedPlants, plantAlias)) return true;
        if (containsIgnoreCase(alwaysAvailable, plantAlias)) return false;
        boolean inAvailable = containsIgnoreCase(getAvailablePlants(), plantAlias);
        boolean inForced = containsIgnoreCase(getForcedPlants(), plantAlias);
        return !inAvailable && !inForced;
    }

    private boolean containsIgnoreCase(List<String> values, String target) {
        return values != null && values.stream().anyMatch(value -> value.equalsIgnoreCase(target));
    }

    public List<String> getLockedPlants() { return lockedPlants; }
    public void setLockedPlants(List<String> lockedPlants) { this.lockedPlants = lockedPlants; }
    public List<String> getAlwaysAvailable() { return alwaysAvailable; }
    public void setAlwaysAvailable(List<String> alwaysAvailable) { this.alwaysAvailable = alwaysAvailable; }
}
