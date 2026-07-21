package model.user_data;

import model.greenhouse.PotData;
import model.news.News;
import model.quests.GameQuest;

import java.util.*;

public class UserState {

    public List<News> news;
    public int lastLevel, diamonds, coins;
    public int difficultyLevel;
    public int gamesPlayed = 0;
    public int highScore = 0;

    public Set<Integer> unlockedPlantIds = new HashSet<>();
    public Map<Integer, Integer> plantLevels = new HashMap<>();
    public Map<Integer, Integer> seedPacketInventory = new HashMap<>();
    public Map<Integer, Boolean> plantBoosts = new HashMap<>();
    public List<List<PotData>> greenhousePots;
    public int plantFoodCount = 0;

    public List<GameQuest> activeQuests = new ArrayList<>();

    public String dailyOfferDate;
    public Integer dailyOfferPlantId;
    public boolean dailyOfferPurchased;

    public UserState(List<News> news, int lastLevel, int diamonds, int coins) {
        this.news = news;
        this.lastLevel = lastLevel;
        this.diamonds = diamonds;
        this.coins = coins;
        this.difficultyLevel = 3;
        this.unlockedPlantIds.add(1);
    }

    public boolean isPlantUnlocked(int plantId) {
        return unlockedPlantIds.contains(plantId);
    }

    public void unlockPlant(int plantId) {
        unlockedPlantIds.add(plantId);
    }

    public int getPlantLevel(int plantId) {
        return plantLevels.getOrDefault(plantId, 1);
    }

    public void setPlantLevel(int plantId, int level) {
        plantLevels.put(plantId, level);
    }

    public boolean hasBoost(int plantId) {
        return plantBoosts.getOrDefault(plantId, false);
    }

    public boolean grantBoost(int plantId) {
        if (hasBoost(plantId)) return false;
        plantBoosts.put(plantId, true);
        return true;
    }

    public boolean consumeBoost(int plantId) {
        if (!hasBoost(plantId)) return false;
        plantBoosts.put(plantId, false);
        return true;
    }

    public void addSeedPackets(int plantId, int count) {
        seedPacketInventory.merge(plantId, count, Integer::sum);
    }

    public void addNews(News item) {
        if (item != null) news.add(item);
    }

    public void recordGameResult(int levelReached, int score) {
        gamesPlayed++;
        if (levelReached > lastLevel) lastLevel = levelReached;
        if (score > highScore) highScore = score;
    }

    public boolean hasUnreadNews() {
        for (News item : news) {
            if (!item.isRead()) return true;
        }
        return false;
    }
}
