package model.collections.item;

import model.greenhouse.Greenhouse;
import model.match_mechanisms.vector.Position;
import model.user_data.UserState;
import model.utils.GameSession;

public class GroundPot extends GroundItem {

    public GroundPot(Position position) {
        super(ItemType.POT, position, 15, 0.6);
    }

    @Override
    public void applyRewards(GameSession session, UserState state) {
        Greenhouse.getInstance().unlockNextLockedPot();
    }
}
