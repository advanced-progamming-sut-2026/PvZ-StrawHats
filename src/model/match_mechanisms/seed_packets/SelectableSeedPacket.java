package model.match_mechanisms.seed_packets;

import model.user_data.UserState;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;

public class SelectableSeedPacket extends SeedPacket {

    public SelectableSeedPacket(int count) {
        super("Selectable Seed Packet", count);
    }

    public String openWithChoice(UserState state, int plantTypeId) {
        if (!consumeOne()) {
            return "Error: No Selectable Seed Packets left to open!";
        }

        if (!state.isPlantUnlocked(plantTypeId)) {
            return "Error: That plant is not unlocked yet. You can only choose seeds for unlocked plants.";
        }

        state.addSeedPackets(plantTypeId, 10);
        PlantJsonParser.PlantConfig config = PlantFactory.getBlueprints().get(plantTypeId);
        String plantName = (config != null) ? config.name : "#" + plantTypeId;

        return "Opened 1 Selectable Seed Packet! Received 10 seeds of " + plantName + ".";
    }

    @Override
    public String open(UserState state) {
        return "Error: You must choose a plant type to open this packet. Use openWithChoice instead.";
    }
}