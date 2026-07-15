package model.match.main.season.travellog.beach;

import model.match.main.season.Season;

public class Beach extends Season {
    public Beach() {
        super("Big Wave Beach");
    }

    @Override
    public boolean hasTide() { return true; }
}