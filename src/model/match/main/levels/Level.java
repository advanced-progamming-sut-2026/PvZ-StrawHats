package model.match.main.levels;

import model.match.main.season.Season;

public abstract class Level {
    private Season season;

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }
}
