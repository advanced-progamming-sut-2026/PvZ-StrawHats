package model.match.main.season.travellog.beach;

import model.match.main.levels.Level;

public class Flood {
    /**
     * Raises the water by one column (toward maxTideColumn). Per the spec
     * this should be called when a new zombie wave starts.
     */
    public static void riselevel(Level level) {
        if (level == null) return;
        int next = Math.min(level.getCurrentTideColumn() + 1, level.getMaxTideColumn());
        level.setCurrentTideColumn(next);
    }

    /**
     * Lowers the water by one column (toward dry land). Per the spec this
     * should be called when a new zombie wave starts.
     */
    public static void falllevel(Level level) {
        if (level == null) return;
        int next = Math.max(level.getCurrentTideColumn() - 1, 0);
        level.setCurrentTideColumn(next);
    }
}
