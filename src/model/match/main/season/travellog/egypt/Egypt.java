package model.match.main.season.travellog.egypt;

import model.match.main.season.Season;

public class Egypt extends Season {
    public Egypt() {
        super("Egypt");
        // Add Egypt-specific obstacles (graves)
        // obstacles.add(new ObstacleInformation("grave", 700, true));
    }

    @Override
    public boolean hasGraves() { return true; }
}