package model.greenhouse.store;

public enum Product {
    POT("pot", "Vase (Pot)", 2000, 0),
    PLANT_FOOD("plant-food", "Plant Food", 0, 3),
    SEED_RANDOM("random-seed", "Random Seed Packet (5 packs)", 1000, 0),
    SEED_CHOICE("selectable-seed", "Selectable Seed Packet (10 packs)", 0, 5),
    CURRENCY_EXCHANGE("diamond-exchange", "Diamond Exchange (500 coins)", 0, 5),
    DAILY_OFFER("daily-offer", "Daily Special Seed Packet (10 packs)", 1600, 0);

    private final String itemId;
    private final String displayName;
    private final int coinCost;
    private final int diamondCost;

    Product(String itemId, String displayName, int coinCost, int diamondCost) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.coinCost = coinCost;
        this.diamondCost = diamondCost;
    }

    public String getItemId() {
        return itemId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCoinCost() {
        return coinCost;
    }

    public int getDiamondCost() {
        return diamondCost;
    }

    public static Product byItemId(String itemId) {
        for (Product product : values())
            if (product.itemId.equalsIgnoreCase(itemId)) return product;
        return null;
    }
}