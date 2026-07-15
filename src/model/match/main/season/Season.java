package model.match.main.season;

import model.match.main.season.travellog.TravelLog;
import model.pitches.ObstacleInformation;
import java.util.ArrayList;

public abstract class Season {
    protected String name;
    public TravelLog travelLog;
    public ArrayList<ObstacleInformation> obstacles = new ArrayList<>();

    public Season(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public static void handleTravellog() {}
    public static void applyEffect() {}

    // Season-specific checks (can be overridden)
    public boolean hasGraves() { return false; }
    public boolean hasIceTiles() { return false; }
    public boolean hasTide() { return false; }
    public boolean isNight() { return false; }
}