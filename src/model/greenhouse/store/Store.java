package model.greenhouse.store;

import model.greenhouse.Pot;
import model.match_mechanisms.seed_packets.SeedPacket;

public abstract class Store {
    public SeedPacket dailySeedPackets;
    public abstract Pot buyPot();
    public abstract void buyPlantFood();
    public abstract void buyRandomSeedPacket();
    public abstract void buySelectedSeedPacket();
    public abstract void exchange();
}
