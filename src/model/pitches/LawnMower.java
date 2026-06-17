package model.pitches;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;

public class LawnMower {
    public Position position;

    private int rowNumber;
    private Square[] row;
    private boolean isUsed;

    public void killZombiesInRow() {
        if (isUsed) {
            System.out.println("The zombie ate your brain; LOSER!!!");
        }
        else {
            System.out.println("The lawn mower in the row "+rowNumber+"is triggered and killed these zombies:");
            for (Square square : row) {
                for (Zombie zombie : square.getZombies()) {
                    zombie.setHP(0);
                    System.out.println(zombie.getName());
                }
            }
            isUsed = true;
        }
    };

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

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

}
