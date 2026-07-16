package model.greenhouse;

import model.collections.plant.PlantFactory;

import java.util.ArrayList;
import java.util.List;

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
            for (int j = 0; j < COL_COUNT; j++) {
                instance.pots[0][j] = new Pot(false, 1, j + 1);
                for (int i = 1; i < ROW_COUNT; i++) {
                    instance.pots[i][j] = new Pot(true, i + 1, j + 1);
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

    public Pot getPot(int x, int y) {
        if (x < 1 || x > COL_COUNT || y < 1 || y > ROW_COUNT) return null;
        return pots[y - 1][x - 1];
    }

    public int countUnlockedPots() {
        int count = 0;
        for (Pot[] row : pots)
            for (Pot pot : row)
                if (!pot.isLocked()) count++;
        return count;
    }

    public boolean unlockNextLockedPot() {
        for (int i = 1; i < ROW_COUNT; i++) {
            for (int j = 0; j < COL_COUNT; j++) {
                if (pots[i][j].isLocked()) {
                    pots[i][j].unlockPot();
                    return true;
                }
            }
        }
        return false;
    }

    public List<List<PotData>> serialize() {
        List<List<PotData>> result = new ArrayList<>();

        for (int r = 0; r < ROW_COUNT; r++) {
            List<PotData> row = new ArrayList<>();

            for (int c = 0; c < COL_COUNT; c++) {
                Pot pot = pots[r][c];

                PotData data = new PotData();

                data.locked = pot.isLocked();

                if (pot.getPotPlant() != null) {
                    PotPlant plant = pot.getPotPlant();

                    data.plantId = plant.getPlantId();
                    data.plantedAtMillis = plant.getPlantedAtMillis();
                }

                row.add(data);
            }

            result.add(row);
        }

        return result;
    }

    public void load(List<List<PotData>> data) {

        if (data == null)
            return;

        for (int r = 0; r < ROW_COUNT; r++) {
            for (int c = 0; c < COL_COUNT; c++) {

                Pot pot = pots[r][c];
                PotData save = data.get(r).get(c);

                pot.setLocked(save.locked);

                if (save.plantId != null) {

                    GreenhousePlant plant =
                            new GreenhousePlant(
                                    pot,
                                    save.plantId,
                                    PlantFactory.getBlueprints()
                                            .get(save.plantId).name);

                    plant.setPlantedAtMillis(save.plantedAtMillis);

                    pot.setPotPlant(plant);
                } else {
                    pot.setPotPlant(null);
                }
            }
        }
    }

    public String renderStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Greenhouse (" + countUnlockedPots() + "/" + (ROW_COUNT * COL_COUNT) + " pots unlocked)\n");
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COL_COUNT; j++) {
                Pot pot = pots[i][j];
                sb.append("(").append(j + 1).append(",").append(i + 1).append(") ");
                if (pot.isLocked()) {
                    sb.append("LOCKED");
                } else if (pot.getPotPlant() == null) {
                    sb.append("EMPTY");
                } else {
                    PotPlant plant = pot.getPotPlant();
                    if (plant.isCollectAble()) {
                        sb.append(plant.getPlantName()).append(" READY");
                    } else {
                        sb.append(plant.getPlantName()).append(" growing, ")
                                .append(formatDuration(plant.getRemainingSeconds()))
                                .append(" remaining");
                    }
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static String formatDuration(long totalSeconds) {
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
