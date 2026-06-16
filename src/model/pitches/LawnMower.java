package model.pitches;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;

public class LawnMower {
    private Position row;
    private boolean isUsed;

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public void killZombiesInRow(Zombie[] zombies) {};
}
