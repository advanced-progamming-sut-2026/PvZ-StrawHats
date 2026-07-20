package model.match.main.season.travellog.darkage;

import model.match.main.season.Season;

public class DarkAge extends Season {
    public DarkAge() {
        super("Dark Ages");
    }

    @Override
    public boolean isNight() { return true; }

    /**
     * At the start of each wave, some grave-bearing tiles have a chance to
     * spawn a zombie from underneath.
     * TODO: needs a real Grave/Obstacle model (model.pitches.obstacles.Obstacle is
     * currently an empty marker interface) before this can be implemented -
     * specifically a way to know which cells have a grave and are empty.
     */
    public static void necromancy() { /* blocked on Obstacle/Grave support */ }
}