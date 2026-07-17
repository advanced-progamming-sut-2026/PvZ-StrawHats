package model.match.mini_games.vasebreaker.vase;

import model.match.mini_games.vasebreaker.Vasebreaker;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

/**
 * "Plant vase" — guaranteed to drop a one-time seed packet when broken.
 */
public class PlantVase extends Vase {
    private final int plantId;

    public PlantVase(Position position, int plantId) {
        super(position);
        this.plantId = plantId;
    }

    public int getPlantId() { return plantId; }

    @Override
    protected void onBreak(GameSession session, Vasebreaker minigame) {
        minigame.dropSeedPacket(position, plantId);
    }
}
