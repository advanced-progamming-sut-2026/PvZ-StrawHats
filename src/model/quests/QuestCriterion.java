package model.quests;

import java.util.Map;

public class QuestCriterion {
    private String type;
    private int target;
    private Map<String, Object> params;

    /**
     * Name of the entry inside {@code params} that "variableOptions" should
     * randomize into, at quest-assignment time (e.g. "maxPlantsLost",
     * "familyType", "chapter"). When null/blank, a numeric variableOptions list
     * randomizes {@code target} directly instead (legacy behaviour, used by
     * e.g. the daily sun collector).
     */
    private String variableParam;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getVariableParam() {
        return variableParam;
    }

    public void setVariableParam(String variableParam) {
        this.variableParam = variableParam;
    }
}