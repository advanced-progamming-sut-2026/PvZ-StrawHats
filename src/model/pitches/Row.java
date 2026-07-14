package model.pitches;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private final int index;
    private final List<Square> cells = new ArrayList<>();

    public Row(int index) {
        this.index = index;
    }

    public void addCell(Square cell) {
        cells.add(cell);
    }

    public int getIndex() { return index; }

    public Square getCell(int col) {
        if (col < 0 || col >= cells.size()) return null;
        return cells.get(col);
    }

    public List<Square> getCells() { return cells; }

}
