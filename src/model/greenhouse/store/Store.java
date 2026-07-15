package model.greenhouse.store;

import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.user_data.UserState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Store {

    private static final Random RANDOM = new Random();

    public String renderPermanentGoods() {
        StringBuilder sb = new StringBuilder();
        sb.append("Permanent Goods:\n");
        for (Product product : Product.values()) {
            if (product == Product.DAILY_OFFER) continue;
            sb.append("- ").append(product.getItemId()).append(": ").append(product.getDisplayName());
            if (product.getCoinCost() > 0) sb.append(", ").append(product.getCoinCost()).append(" coins");
            if (product.getDiamondCost() > 0) sb.append(", ").append(product.getDiamondCost()).append(" diamonds");
            sb.append("\n");
        }
        return sb.toString();
    }

    public String renderDailyOffer(UserState state) {
        refreshDailyOffer(state);
        PlantJsonParser.PlantConfig config = PlantFactory.getBlueprints().get(state.dailyOfferPlantId);
        String name = config == null ? "Unknown Plant" : config.name;
        return "Daily Offer: 10 seed packs of " + name + " for " + Product.DAILY_OFFER.getCoinCost()
                + " coins" + (state.dailyOfferPurchased ? " (already purchased today)" : "");
    }

    public void refreshDailyOffer(UserState state) {
        String today = LocalDate.now().toString();
        if (today.equals(state.dailyOfferDate) && state.dailyOfferPlantId != null) return;

        List<Integer> candidates = new ArrayList<>(state.unlockedPlantIds);
        state.dailyOfferDate = today;
        state.dailyOfferPurchased = false;
        state.dailyOfferPlantId = candidates.isEmpty() ? null : candidates.get(RANDOM.nextInt(candidates.size()));
    }

    public String buy(UserState state, String itemId, int count, Integer plantTypeId) {
        Product product = Product.byItemId(itemId);
        if (product == null) return "Error: unknown item id '" + itemId + "'.";
        if (count <= 0) return "Error: count must be positive.";

        return switch (product) {
            case POT -> buyPot(state, count);
            case PLANT_FOOD -> buyPlantFood(state, count);
            case SEED_RANDOM -> buyRandomSeedPacket(state, count);
            case SEED_CHOICE -> buySelectedSeedPacket(state, count, plantTypeId);
            case CURRENCY_EXCHANGE -> exchange(state, count);
            case DAILY_OFFER -> buyDailyOffer(state);
        };
    }

    private String buyPot(UserState state, int count) {
        int cost = Product.POT.getCoinCost() * count;
        if (state.coins < cost) return "Error: not enough coins.";

        model.greenhouse.Greenhouse greenhouse = model.greenhouse.Greenhouse.getInstance();
        int unlocked = 0;
        for (int i = 0; i < count; i++) {
            if (!greenhouse.unlockNextLockedPot()) break;
            unlocked++;
        }
        if (unlocked == 0) return "Error: greenhouse is already at maximum capacity (20 pots).";

        state.coins -= Product.POT.getCoinCost() * unlocked;
        return "Purchased " + unlocked + " pot(s); " + state.coins + " coins remaining.";
    }

    private String buyPlantFood(UserState state, int count) {
        int cost = Product.PLANT_FOOD.getDiamondCost() * count;
        if (state.diamonds < cost) return "Error: not enough diamonds.";
        if (state.plantFoodCount + count > 3) return "Error: cannot hold more than 3 Plant Food at once.";

        state.diamonds -= cost;
        state.plantFoodCount += count;
        return "Purchased " + count + " Plant Food; " + state.diamonds + " diamonds remaining.";
    }

    private String buyRandomSeedPacket(UserState state, int count) {
        int cost = Product.SEED_RANDOM.getCoinCost() * count;
        if (state.coins < cost) return "Error: not enough coins.";
        List<Integer> candidates = new ArrayList<>(state.unlockedPlantIds);
        if (candidates.isEmpty()) return "Error: no unlocked plants available.";

        state.coins -= cost;
        StringBuilder sb = new StringBuilder("Purchased: ");
        for (int i = 0; i < count; i++) {
            int plantId = candidates.get(RANDOM.nextInt(candidates.size()));
            state.addSeedPackets(plantId, 5);
            sb.append(nameOf(plantId)).append(" x5 packs; ");
        }
        sb.append(state.coins).append(" coins remaining.");
        return sb.toString();
    }

    private String buySelectedSeedPacket(UserState state, int count, Integer plantTypeId) {
        if (plantTypeId == null) return "Error: -t <plant_type> is required for this item.";
        if (!state.isPlantUnlocked(plantTypeId)) return "Error: that plant is not unlocked yet.";

        int cost = Product.SEED_CHOICE.getDiamondCost() * count;
        if (state.diamonds < cost) return "Error: not enough diamonds.";

        state.diamonds -= cost;
        state.addSeedPackets(plantTypeId, 10 * count);
        return "Purchased " + (10 * count) + " seed packs of " + nameOf(plantTypeId) + "; "
                + state.diamonds + " diamonds remaining.";
    }

    private String exchange(UserState state, int count) {
        int cost = Product.CURRENCY_EXCHANGE.getDiamondCost() * count;
        if (state.diamonds < cost) return "Error: not enough diamonds.";

        state.diamonds -= cost;
        state.coins += 500 * count;
        return "Exchanged " + cost + " diamonds for " + (500 * count) + " coins.";
    }

    private String buyDailyOffer(UserState state) {
        refreshDailyOffer(state);
        if (state.dailyOfferPlantId == null) return "Error: no daily offer available.";
        if (state.dailyOfferPurchased) return "Error: you already purchased today's offer.";

        int cost = Product.DAILY_OFFER.getCoinCost();
        if (state.coins < cost) return "Error: not enough coins.";

        state.coins -= cost;
        state.addSeedPackets(state.dailyOfferPlantId, 10);
        state.dailyOfferPurchased = true;
        return "Purchased daily offer: 10 seed packs of " + nameOf(state.dailyOfferPlantId) + "; "
                + state.coins + " coins remaining.";
    }

    private String nameOf(int plantId) {
        Map<Integer, PlantJsonParser.PlantConfig> blueprints = PlantFactory.getBlueprints();
        PlantJsonParser.PlantConfig config = blueprints.get(plantId);
        return config == null ? ("#" + plantId) : config.name;
    }
}
