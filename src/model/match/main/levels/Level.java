package model.match.main.levels;

public abstract class Level {
    private Season season;

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }
}
