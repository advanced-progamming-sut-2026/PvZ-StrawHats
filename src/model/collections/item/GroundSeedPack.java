package model.collections.item;

import model.match_mechanisms.vector.Position;
import model.user_data.UserState;
import model.utils.GameSession;

public class GroundSeedPack extends GroundItem {
    private final int plantId;
    private final int packCount;

    public GroundSeedPack(Position position, int plantId, int packCount) {
        super(ItemType.SEED_PACK, position, 15, 0.6);
        this.plantId = plantId;
        this.packCount = packCount;
    }

    @Override
    public void applyRewards(GameSession session, UserState state) {
        state.addSeedPackets(plantId, packCount);
    }

    public int getPlantId() {
        return plantId;
    }

    public int getPackCount() {
        return packCount;
    }
}
