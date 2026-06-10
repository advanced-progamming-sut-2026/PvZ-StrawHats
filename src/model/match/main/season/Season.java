package model.match.main.season;

import model.match.main.season.travellog.TravelLog;
import model.pitches.ObstacleInformation;

import java.util.ArrayList;

public abstract class Season {
    public TravelLog travelLog;
    public static void handleTravellog(){}
    public ArrayList<ObstacleInformation> obstacles = new ArrayList<>();
    public static void applyeffect(){};
}

