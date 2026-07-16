package model.quests;

import java.util.List;

public class GameQuest extends Quest {
    private String id;
    private String title;
    private String type;
    private String priority;
    private Integer expiresAfterSeconds;
    private List<QuestCriterion> criteria;
    private QuestReward reward;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Integer getExpiresAfterSeconds() {
        return expiresAfterSeconds;
    }

    public void setExpiresAfterSeconds(Integer expiresAfterSeconds) {
        this.expiresAfterSeconds = expiresAfterSeconds;
    }

    public List<QuestCriterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<QuestCriterion> criteria) {
        this.criteria = criteria;
    }

    public QuestReward getReward() {
        return reward;
    }

    public void setReward(QuestReward reward) {
        this.reward = reward;
    }
}