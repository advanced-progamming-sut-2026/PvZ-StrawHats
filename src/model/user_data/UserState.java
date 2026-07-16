package model.user_data;

import model.news.News;

import java.util.*;

import model.greenhouse.PotData;

public class UserState {

    public News[] news;
    public int lastLevel, diamonds, coins;

    public Set<Integer> unlockedPlantIds = new HashSet<>();
    public Map<Integer, Integer> seedPacketInventory = new HashMap<>();
    public Map<Integer, Boolean> plantBoosts = new HashMap<>();
    public List<List<PotData>> greenhousePots;
    public int plantFoodCount = 0;

    public String dailyOfferDate;
    public Integer dailyOfferPlantId;
    public boolean dailyOfferPurchased;

    public UserState(News[] news, int lastLevel, int diamonds, int coins) {
        this.news = news;
        this.lastLevel = lastLevel;
        this.diamonds = diamonds;
        this.coins = coins;
        this.unlockedPlantIds.add(1);
    }

    public boolean isPlantUnlocked(int plantId) {
        return unlockedPlantIds.contains(plantId);
    }

    public void unlockPlant(int plantId) {
        unlockedPlantIds.add(plantId);
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
}
