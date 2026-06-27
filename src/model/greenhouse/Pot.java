package model.greenhouse;

import controller.menus.greenhouse.PotController;

public class Pot {
    private PotPlant potPlant;
    private boolean isLocked;
    private final PotController potController;

    private static final int UNLOCK_COST = 100; // 100 is an example

    public Pot(boolean isLocked) {
        this.isLocked = isLocked;
        potController = new PotController(this);
    }

    public void freePot() {
        this.potPlant = null;
    }

    public void unlockPot() {
        isLocked = false;
    }

    public static int getUnlockCost() {
        return UNLOCK_COST;
    }
    public PotPlant getPotPlant() {
        return potPlant;
    }
    public boolean isLocked() {
        return isLocked;
    }
    public PotController getPotController() {
        return potController;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }
    public void setPotPlant(PotPlant potPlant) {
        this.potPlant = potPlant;
    }
}
