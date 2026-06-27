package model.greenhouse;

import java.util.ArrayList;

public class Greenhouse {
    private static final int ROW_COUNT = 4;
    private static final int COL_COUNT = 5;

    private static Greenhouse instance;
    private Pot[][] pots;

    private Greenhouse(Pot[][] pots) {
        this.pots = pots;
    }

    public static Greenhouse getInstance() {
        if (instance == null) {
            instance = new Greenhouse(new Pot[ROW_COUNT][COL_COUNT]);
            for (int j = 0 ; j < COL_COUNT ; j++) {
                instance.pots[0][j] = new Pot(false); // radif aval unlock
                for (int i = 1 ; i < ROW_COUNT ; i++) {
                    instance.pots[i][j] = new Pot(true);
                }
            }
        }
        return instance;
    }

    public static int getColCount() {
        return COL_COUNT;
    }
    public static int getRowCount() {
        return ROW_COUNT;
    }
    public Pot[][] getPots() {
        return pots;
    }

    public void setPots(Pot[][] pots) {
        this.pots = pots;
    }

}
