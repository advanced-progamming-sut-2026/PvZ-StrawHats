package model.collections.item;

import model.match_mechanisms.vector.Position;
import model.user_data.UserState;
import model.utils.GameSession;

public class GroundDiamond extends GroundItem {
    private final int amount;

    public GroundDiamond(Position position, int amount) {
        super(ItemType.DIAMOND, position, 15, 0.6);
        this.amount = amount;
    }

    @Override
    public void applyRewards(GameSession session, UserState state) {
        state.diamonds += amount;
    }

    public int getAmount() {
        return amount;
    }
}
