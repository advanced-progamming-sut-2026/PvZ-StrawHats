package controller;

import model.collections.plant.PlantJsonParser;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantTag;
import model.pitches.Cell;
import model.pitches.Environment;
import model.quests.GameQuest;
import model.quests.QuestCriterion;
import model.quests.QuestLoader;
import model.user_data.User;
import model.user_data.UserState;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuestManager {

    private static final Random RANDOM = new Random();

    public static void updateProgress(String criteriaType, int amount, Map<String, Object> context) {
        if (User.currentUser == null) return;
        boolean changed = false;
        for (GameQuest quest : QuestLoader.getAllQuests()) {
            if (quest.isCompleted()) {
                continue;
            }
            for (QuestCriterion criterion : quest.getCriteria()) {
                if (criterion.getType().equals(criteriaType)) {
                    if (matchesParams(criterion.getParams(), context)) {
                        quest.setProgress(quest.getProgress() + amount);
                        checkCompletion(quest);
                        changed = true;
                    }
                }
            }
        }
        // Persist every progress change immediately (not only on completion) so
        // progress survives even if the player quits before finishing a quest.
        if (changed) {
            QuestLoader.saveActiveQuestsProgress();
        }
    }

    public static void notifyLevelWon(GameSession session) {
        if (session == null || User.currentUser == null) return;
        Environment env = session.getEnvironment();
        if (env == null) return;

        int rows = env.getRows();
        int cols = env.getCols();
        boolean[][] occupied = new boolean[rows][cols];
        boolean[] rowHasPlant = new boolean[rows];
        boolean[] colHasPlant = new boolean[cols];
        int sunProducers = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = env.getCell(r, c);
                boolean hasPlant = cell != null && cell.hasPlant();
                occupied[r][c] = hasPlant;
                if (hasPlant) {
                    rowHasPlant[r] = true;
                    colHasPlant[c] = true;
                    List<PlantTag> tags = cell.getPlant().getTags();
                    if (tags != null && tags.contains(PlantTag.SUN)) {
                        sunProducers++;
                    }
                }
            }
        }

        updateProgress("WIN_LEVEL_EXACT_SUN_LEFT", 1, mapOf("exactSunLeft", session.getSunCount()));

        updateProgress("WIN_LEVEL_MAX_PLANTS_LOST", 1, mapOf("maxPlantsLost", session.getPlantsLostThisMatch()));

        updateProgress("WIN_LEVEL_MAX_SUN_PRODUCERS", 1, mapOf("maxSunProducers", sunProducers));

        boolean symmetric = true;
        for (int r = 0; r < rows / 2 && symmetric; r++) {
            int mirrorRow = rows - 1 - r;
            for (int c = 0; c < cols; c++) {
                if (occupied[r][c] != occupied[mirrorRow][c]) {
                    symmetric = false;
                    break;
                }
            }
        }
        updateProgress("WIN_LEVEL_SYMMETRIC", 1, mapOf("requireSymmetric", symmetric));
        updateProgress("WIN_LEVEL_ASYMMETRIC", 1, mapOf("excludeMiddleRow", !symmetric));

        for (int c = 0; c < cols; c++) {
            if (!colHasPlant[c]) {
                updateProgress("WIN_LEVEL_EMPTY_COLUMN", 1, mapOf("targetColumnIndex", c));
            }
        }
        for (int r = 0; r < rows; r++) {
            if (!rowHasPlant[r]) {
                updateProgress("WIN_LEVEL_EMPTY_ROW", 1, mapOf("targetRowIndex", r));
            }
        }
        for (int r = 0; r < rows; r++) {
            if (rowHasPlant[r]) continue;
            for (int c = 0; c < cols; c++) {
                if (colHasPlant[c]) continue;
                Map<String, Object> ctx = new HashMap<>();
                ctx.put("targetRowIndex", r);
                ctx.put("targetColumnIndex", c);
                updateProgress("WIN_LEVEL_EMPTY_ROW_AND_COLUMN", 1, ctx);
            }
        }
    }

    private static Map<String, Object> mapOf(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private static boolean matchesParams(Map<String, Object> criterionParams, Map<String, Object> context) {
        if (criterionParams == null || criterionParams.isEmpty()) {
            return true;
        }
        if (context == null) {
            return false;
        }

        for (Map.Entry<String, Object> entry : criterionParams.entrySet()) {
            String key = entry.getKey();
            Object requiredValue = entry.getValue();

            if (key.equals("variableOptions")) {
                continue;
            }

            if (!context.containsKey(key)) {
                return false;
            }

            Object contextValue = context.get(key);

            if (requiredValue instanceof String && ((String) requiredValue).startsWith("any")) {
                if (requiredValue.equals("any")) {
                    continue;
                }
                if (requiredValue.equals("any_offensive")) {
                    Boolean isOffensive = (Boolean) context.get("isOffensive");
                    if (isOffensive == null || !isOffensive) {
                        return false;
                    }
                    continue;
                }
            }

            if (key.startsWith("max") || key.equals("timeLimitSeconds")) {
                if (requiredValue instanceof Number && contextValue instanceof Number) {
                    if (((Number) contextValue).doubleValue() > ((Number) requiredValue).doubleValue()) {
                        return false;
                    }
                    continue;
                }
            }

            if (requiredValue instanceof Number && contextValue instanceof Number) {
                if (((Number) requiredValue).doubleValue() != ((Number) contextValue).doubleValue()) {
                    return false;
                }
            } else if (!requiredValue.equals(contextValue)) {
                if (requiredValue.equals("n")) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    private static void checkCompletion(GameQuest quest) {
        for (QuestCriterion criterion : quest.getCriteria()) {
            if (quest.getProgress() < criterion.getTarget()) {
                return;
            }
        }
        quest.setCompleted(true);
    }

    public static String collectReward(String questId) {
        if (User.currentUser == null) {
            return "Error: no user logged in.";
        }
        GameQuest quest = findQuestById(questId);
        if (quest == null) {
            return "Error: no such quest.";
        }
        if (!quest.isCompleted()) {
            return "Error: quest not completed yet, you can not receive that.";
        }
        if (quest.isRewardCollected()) {
            return "Error: reward for \"" + quest.getTitle() + "\" was already achieved.";
        }

        rewardUser(quest);
        quest.setRewardCollected(true);
        QuestLoader.saveActiveQuestsProgress();
        return quest.getTitle() + " achieved.";
    }

    public static GameQuest findQuestById(String questId) {
        if (questId == null) return null;
        for (GameQuest quest : QuestLoader.getAllQuests()) {
            if (quest.getId() != null && quest.getId().equalsIgnoreCase(questId)) {
                return quest;
            }
        }
        return null;
    }

    /** Highest criterion target for a quest; used to render its progress bar. */
    public static int getDisplayTarget(GameQuest quest) {
        int target = 1;
        if (quest.getCriteria() != null) {
            for (QuestCriterion criterion : quest.getCriteria()) {
                target = Math.max(target, criterion.getTarget());
            }
        }
        return target;
    }

    private static void rewardUser(GameQuest quest) {
        if (User.currentUser == null || quest.getReward() == null) return;
        UserState state = User.currentUser.userState;

        String rewardType = quest.getReward().getRewardType();
        int amount = quest.getReward().getAmount();
        String formula = quest.getReward().getFormula();

        if (formula != null && !formula.isEmpty() && !formula.equals("random_new_plant")) {
            amount = evaluateFormula(formula, quest);
        }

        if (rewardType.equals("COIN")) {
            state.coins += amount;
        } else if (rewardType.equals("GEM")) {
            state.diamonds += amount;
        } else if (rewardType.equals("SEED_PACK")) {
            Integer plantId = resolveSeedPacketPlantId(quest, formula);
            if (plantId != null) {
                state.addSeedPackets(plantId, amount);
            }
        }
    }

    private static Integer resolveSeedPacketPlantId(GameQuest quest, String formula) {
        UserState state = User.currentUser.userState;
        List<PlantJsonParser.PlantConfig> candidates = new ArrayList<>();

        boolean wantsNewPlant = "random_new_plant".equals(formula);
        for (PlantJsonParser.PlantConfig config : PlantFactory.getBlueprints().values()) {
            boolean unlocked = state.isPlantUnlocked(config.id);
            if (wantsNewPlant ? !unlocked : unlocked) {
                candidates.add(config);
            }
        }

        if (candidates.isEmpty()) {
            for (PlantJsonParser.PlantConfig config : PlantFactory.getBlueprints().values()) {
                candidates.add(config);
            }
        }
        if (candidates.isEmpty()) return null;

        return candidates.get(RANDOM.nextInt(candidates.size())).id;
    }

    private static int evaluateFormula(String formula, GameQuest quest) {
        if (formula.contains("sun_amount / 100")) {
            return quest.getProgress() / 100;
        }
        if (formula.contains("20 - n")) {
            for (QuestCriterion criterion : quest.getCriteria()) {
                if (criterion.getParams() != null && criterion.getParams().containsKey("maxPlantsLost")) {
                    Object val = criterion.getParams().get("maxPlantsLost");
                    if (val instanceof Number) {
                        return 20 - ((Number) val).intValue();
                    }
                }
            }
        }
        return quest.getReward().getAmount();
    }
}