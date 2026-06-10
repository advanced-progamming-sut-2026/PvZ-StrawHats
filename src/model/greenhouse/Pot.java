package model.greenhouse;

import model.collections.plant.Plant;

public abstract class Pot {
    public Plant plant;
    private boolean locked;


    public abstract void planting();
    public abstract void collect();
    public abstract int getRemainingTime();
    public abstract int getPrice();
    public abstract void sell();

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
