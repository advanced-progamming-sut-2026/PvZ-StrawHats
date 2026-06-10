package model.pitches;

import model.collections.zombie.Zombie;

public class LawnMower {
    private Obstacle[] row;
    private boolean isUsed;

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public void killZombiesInRow(Zombie[] zombies) {};
}
