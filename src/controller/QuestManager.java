package controller;

import model.quests.GameQuest;
import model.quests.QuestCriterion;
import model.quests.QuestLoader;

import java.util.Map;

public class QuestManager {

    public static void updateProgress(String criteriaType, int amount, Map<String, Object> context) {
        // *
        for (GameQuest quest : QuestLoader.getAllQuests()) {
            if (quest.isCompleted()) {
                continue;
            }
            // *
            for (QuestCriterion criterion : quest.getCriteria()) {
                if (criterion.getType().equals(criteriaType)) {
                    if (matchesParams(criterion.getParams(), context)) {
                        quest.setProgress(quest.getProgress() + amount);
                        checkCompletion(quest);
                    }
                }
            }
        }
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
        rewardUser(quest);
        QuestLoader.saveActiveQuestsProgress();
    }

    private static void rewardUser(GameQuest quest) {
        // *
        String rewardType = quest.getReward().getRewardType();
        int amount = quest.getReward().getAmount();
        String formula = quest.getReward().getFormula();

        if (formula != null && !formula.isEmpty()) {
            amount = evaluateFormula(formula, quest);
        }

        if (rewardType.equals("COIN")) {
            // *
        } else if (rewardType.equals("GEM")) {
            // *
        } else if (rewardType.equals("SEED_PACK")) {
            // *
        }
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