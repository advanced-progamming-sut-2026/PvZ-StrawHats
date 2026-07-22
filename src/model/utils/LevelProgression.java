package model.utils;

import model.match.main.levels.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Keeps stage unlock checks based on level order instead of gaps in numeric IDs. */
public final class LevelProgression {
    private LevelProgression() {
    }

    public static List<Level> sorted(List<Level> levels) {
        List<Level> result = new ArrayList<>(levels);
        result.sort(Comparator.comparingInt(Level::getId));
        return result;
    }

    public static boolean isCompleted(List<Level> allLevels, int lastCompletedLevelId, Level level) {
        int levelIndex = indexOf(allLevels, level.getId());
        int completedIndex = completedIndex(allLevels, lastCompletedLevelId);
        return levelIndex >= 0 && levelIndex <= completedIndex;
    }

    public static boolean isUnlocked(List<Level> allLevels, int lastCompletedLevelId, Level level) {
        int levelIndex = indexOf(allLevels, level.getId());
        int completedIndex = completedIndex(allLevels, lastCompletedLevelId);
        return levelIndex >= 0 && levelIndex <= completedIndex + 1;
    }

    public static int completedIndex(List<Level> allLevels, int lastCompletedLevelId) {
        if (lastCompletedLevelId <= 0) return -1;
        return indexOf(allLevels, lastCompletedLevelId);
    }

    private static int indexOf(List<Level> allLevels, int levelId) {
        List<Level> ordered = sorted(allLevels);
        for (int i = 0; i < ordered.size(); i++) {
            if (ordered.get(i).getId() == levelId) return i;
        }
        return -1;
    }
}
