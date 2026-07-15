package model.match.main.levels.special_levels;

import model.match.main.levels.Level;

public class PlantWhatYouGetLevel extends Level {
    private int primarySun;
    public void startWave(){};
    public void handleBanSunflower(){};

    public int getPrimarySun() {
        return primarySun;
    }

    public void setPrimarySun(int primarySun) {
        this.primarySun = primarySun;
    }
}
