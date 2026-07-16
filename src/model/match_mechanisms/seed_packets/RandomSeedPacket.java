package model.match_mechanisms.seed_packets;

import model.user_data.UserState;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSeedPacket extends SeedPacket {
    private static final Random RANDOM = new Random();

    public RandomSeedPacket(int count) {
        super("Random Seed Packet", count);
    }

    @Override
    public String open(UserState state) {
        if (!consumeOne()) {
            return "Error: No Random Seed Packets left to open!";
        }

        List<Integer> candidates = new ArrayList<>(state.unlockedPlantIds);
        if (candidates.isEmpty()) {
            return "Error: No unlocked plants available to get seeds for.";
        }

        int plantId = candidates.get(RANDOM.nextInt(candidates.size()));
        state.addSeedPackets(plantId, 5);

        PlantJsonParser.PlantConfig config = PlantFactory.getBlueprints().get(plantId);
        String plantName = (config != null) ? config.name : "#" + plantId;

        return "Opened 1 Random Seed Packet! Received 5 seeds of " + plantName + ".";
    }
}