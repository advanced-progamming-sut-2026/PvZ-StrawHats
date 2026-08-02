package model.match.mini_games.vasebreaker.vase;

import model.match.mini_games.vasebreaker.Vasebreaker;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;


public class PlantVase extends Vase {
    private final int plantId;

    public PlantVase(Position position, int plantId) {
        super(position);
        this.plantId = plantId;
    }

    public int getPlantId() { return plantId; }

    @Override
    public VaseType getVaseType() {
        return VaseType.PLANT_SEED;
    }

    @Override
    public String getRevealedContents() {
        return "Plant seed vase (plant id " + plantId + ")";
    }

    @Override
    protected void onBreak(GameSession session, Vasebreaker minigame) {
        minigame.dropSeedPacket(position, plantId);
    }
}
