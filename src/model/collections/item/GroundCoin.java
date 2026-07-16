package model.collections.item;

import model.match_mechanisms.vector.Position;
import model.user_data.UserState;
import model.utils.GameSession;

import java.util.Random;

public class GroundCoin extends GroundItem {

    public enum CoinTier {
        BRONZE(10, 70),
        SILVER(50, 25),
        GOLD(100, 5);

        private final int value;
        private final int probability;

        CoinTier(int value, int probability) {
            this.value = value;
            this.probability = probability;
        }

        public int getValue() {
            return value;
        }

        public static CoinTier rollRandom() {
            Random random = new Random();
            int roll = random.nextInt(100);
            int cumulative = 0;
            for (CoinTier tier : values()) {
                cumulative += tier.probability;
                if (roll < cumulative) return tier;
            }
            return BRONZE;
        }
    }

    private final CoinTier tier;

    public GroundCoin(Position position, CoinTier tier) {
        super(ItemType.COIN, position, 15, 0.6);
        this.tier = tier;
    }

    @Override
    public void applyRewards(GameSession session, UserState state) {
        state.coins += tier.getValue();
    }

    public CoinTier getTier() {
        return tier;
    }
}
