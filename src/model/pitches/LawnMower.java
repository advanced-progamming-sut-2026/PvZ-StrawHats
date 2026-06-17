package model.pitches;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;

public class LawnMower {
    public Position position;

    private Square[] row;
    private boolean isUsed;

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public Square[] getRow() {
        return row;
    }

    public void setRow(Square[] row) {
        this.row = row;
    }

    public void killZombiesInRow(Zombie[] zombies) {};
}
