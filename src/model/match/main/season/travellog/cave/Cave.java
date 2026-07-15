package model.match.main.season.travellog.cave;

import model.collections.zombie.Zombie;
import model.match.main.season.Season;
import java.util.ArrayList;

public class Cave extends Season {
    public ArrayList<Zombie> frozenZombies = new ArrayList<>();

    public Cave() {
        super("Frostbite Caves");
    }

    @Override
    public boolean hasIceTiles() { return true; }

    public static void meltIce() { /* to be implemented */ }
}