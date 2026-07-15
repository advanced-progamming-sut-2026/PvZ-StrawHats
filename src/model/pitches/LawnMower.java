package model.pitches;

import model.collections.zombie.Zombie;

public class LawnMower {
    private final int rowNumber;
    private Cell[] row;
    private boolean isUsed;

    public LawnMower(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public boolean killZombiesInRow() {
        if (isUsed) {
            System.out.println("The zombie ate your brain; LOSER!!!");
            return false;
        }

        System.out.println("The lawn mower in row " + rowNumber + " is triggered and killed these zombies:");
        if (row != null) {
            for (Cell cell : row) {
                for (Zombie zombie : cell.getZombies()) {
                    zombie.setHp(0);
                    System.out.println(zombie.getName());
                }
                cell.clearZombies();
            }
        }
        isUsed = true;
        return true;
    }

    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean used) { isUsed = used; }

    public int getRowNumber() { return rowNumber; }
    public Cell[] getRow() { return row; }
    public void setRow(Cell[] row) { this.row = row; }
}