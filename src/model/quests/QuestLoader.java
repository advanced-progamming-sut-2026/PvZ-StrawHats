package model.quests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuestLoader {
    private static List<GameQuest> templateQuests = new ArrayList<>();
    private static List<GameQuest> activeQuests = new ArrayList<>();

    public static void loadTemplates(String filePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<GameQuest>>() {}.getType();
            templateQuests = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void initializeActiveQuestsForUser() {
        // *
        activeQuests.clear();
        Random random = new Random();

        for (GameQuest template : templateQuests) {
            GameQuest activeCopy = cloneQuest(template);

            for (QuestCriterion criterion : activeCopy.getCriteria()) {
                Map<String, Object> params = criterion.getParams();
                if (params != null) {
                    if (params.containsKey("variableOptions")) {
                        List<Double> options = (List<Double>) params.get("variableOptions");
                        if (options != null && !options.isEmpty()) {
                            int selectedTarget = options.get(random.nextInt(options.size())).intValue();
                            criterion.setTarget(selectedTarget);
                        }
                    }

                    if ("n".equals(params.get("targetRowIndex"))) {
                        params.put("targetRowIndex", random.nextInt(5));
                    }
                    if ("n".equals(params.get("targetColumnIndex"))) {
                        params.put("targetColumnIndex", random.nextInt(9));
                    }
                }
            }
            activeQuests.add(activeCopy);
        }
        // *
        saveActiveQuestsProgress();
    }

    private static GameQuest cloneQuest(GameQuest src) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(src), GameQuest.class);
    }

    public static void saveActiveQuestsProgress() {
        // *
    }

    public static void loadActiveQuestsProgress() {
        // *
    }

    public static List<GameQuest> getAllQuests() {
        return activeQuests;
    }
}