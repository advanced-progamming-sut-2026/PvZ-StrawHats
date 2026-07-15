package model.pitches;

public class Environment {
    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    public Environment(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return null;
        return grid[row][col];
    }

    public Cell[] getRowCells(int row) {
        if (row < 0 || row >= rows) return null;
        return grid[row];
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
}