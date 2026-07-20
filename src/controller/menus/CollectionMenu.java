package controller.menus;

import model.Regex;
import model.collections.plant.Plant;

import java.util.List;

public class CollectionMenu extends Menu{
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
    public String getName() {
        return "Collection Menu";
    }

    @Override
    public void handleCommand(String text) {
        if (Regex.MENU_COLLECTION_SHOW_ALL_PLANTS.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_COLLECTION_SHOW_PLANT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_COLLECTION_UPGRADE_PLANT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_COLLECTION_SHOW_PLANTS.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_COLLECTION_SHOW_ALL_ZOMBIES.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_COLLECTION_SHOW_ZOMBIE.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_COLLECTION_SHOW_ZOMBIES.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_COLLECTION_PURCHASE_PLANT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_ENTER.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {

        } else if (Regex.MENU_SHOW_CURRENT.getMatcherRaw(text).matches()) {

        }
    }

    @Override
    public void exitMenu() {

    }

    @Override
    public String showMenu() {
        return "";
    }

}
