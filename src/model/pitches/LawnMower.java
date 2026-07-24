package model.pitches;

import model.collections.zombie.Zombie;
import view.GeneralPrinter;

import java.util.List;

public class LawnMower {
    private final int rowNumber;
    private Cell[] row;
    private boolean isUsed;

    public LawnMower(int rowNumber) {
        this.rowNumber = rowNumber;
    }


    public boolean killZombiesInRow(List<Zombie> zombiesInRow) {
        if (isUsed) {
            GeneralPrinter.print("The zombie ate your brain; LOSER!!!");
            return false;
        }

        GeneralPrinter.print("The lawn mower in row " + (rowNumber + 1) + " is triggered and killed these zombies:");
        if (zombiesInRow != null) {
            for (Zombie zombie : zombiesInRow) {
                if (zombie != null && zombie.isAlive()) {
                    zombie.setHp(0);
                    GeneralPrinter.print(" - " + zombie.getName());
                }
            }
        }
        if (row != null) {
            for (Cell cell : row) {
                if (cell != null) cell.clearZombies();
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
