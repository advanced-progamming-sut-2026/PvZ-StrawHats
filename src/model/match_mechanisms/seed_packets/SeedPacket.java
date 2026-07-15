package model.match_mechanisms.seed_packets;

public abstract class SeedPacket {
    private final int plantId;
    private int count;

    protected SeedPacket(int plantId, int count) {
        this.plantId = plantId;
        this.count = count;
    }

    public int getPlantId() {
        return plantId;
    }

    public int getCount() {
        return count;
    }

    public void addCount(int amount) {
        this.count += amount;
    }

    public boolean consumeOne() {
        if (count <= 0) return false;
        count--;
        return true;
    }
}
