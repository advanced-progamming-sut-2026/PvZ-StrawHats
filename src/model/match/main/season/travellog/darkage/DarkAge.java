package model.match.main.season.travellog.darkage;

import model.match.main.season.Season;

public class DarkAge extends Season {
    public DarkAge() {
        super("Dark Ages");
    }

    @Override
    public boolean isNight() { return true; }

    public static void necromancy() { /* to be implemented */ }
}