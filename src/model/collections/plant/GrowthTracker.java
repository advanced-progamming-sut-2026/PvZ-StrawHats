package model.collections.plant;

import java.util.List;
import java.util.Map;

public class GrowthTracker {
    private List<Map<String, Object>> stages;
    private int currentStage = 1;

    public GrowthTracker(List<Map<String, Object>> stages) {
        this.stages = stages;
    }
    public void update(double deltaTime) {}
    public Double getStageValue(String key) { return null; }
    public int getCurrentStage() { return currentStage; }
    public void skipToMaxStage() { if(stages != null) currentStage = stages.size(); }
    public List<Map<String, Object>> getRawStages() { return stages; }
}