package controller.menus;

import model.collections.plant.Plant;

import java.util.List;

public class CollectionMenu implements Menu{
    private List<Plant> unlockedPlants;
    private List<Plant> lockedPlants;

    public void unlockPlant(Plant plant)
    {
        for (Plant lockedPlant : lockedPlants)
        {
            if (plant.equals(lockedPlant))
            {
                lockedPlants.remove(plant);
                unlockedPlants.add(plant);
            }
        }
    }
    public List<Plant> getUnlockedPlants() {
        return unlockedPlants;
    }

    public void setUnlockedPlants(List<Plant> unlockedPlants) {
        this.unlockedPlants = unlockedPlants;
    }

    public List<Plant> getLockedPlants() {
        return lockedPlants;
    }

    public void setLockedPlants(List<Plant> lockedPlants) {
        this.lockedPlants = lockedPlants;
    }

    public void sortPlants(){};
    @Override
    public void changeMenu(Menu targetMenu) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void handleCommand() {

    }
}
