package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.utils.GameSession;

public class PlantWhatYouGetLevel extends Level {
    private int primarySun;

    @Override
    public void initSpecial(GameSession session) {
        handleBanSunflower();
    }

    @Override
    public boolean isSkySunEnabled() {
        return false;
    }

    
    public void handleBanSunflower() {
        if (getAvailablePlants() != null) {
            getAvailablePlants().removeIf(alias -> alias.toLowerCase().contains("sunflower"));
        }
    }

    
    public void startWave(GameSession session) {
        session.startWaves();
    }

    public int getPrimarySun() {
        return primarySun;
    }

    public void setPrimarySun(int primarySun) {
        this.primarySun = primarySun;
    }
}
