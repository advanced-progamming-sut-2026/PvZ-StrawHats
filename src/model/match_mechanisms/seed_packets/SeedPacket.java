package model.match_mechanisms.seed_packets;

import model.user_data.UserState;

public abstract class SeedPacket {
    protected String name;
    protected int count;

    public SeedPacket(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void addCount(int amount) {
        this.count += amount;
    }

    public boolean consumeOne() {
        if (count > 0) {
            count--;
            return true;
        }
        return false;
    }

    public abstract String open(UserState state);
}