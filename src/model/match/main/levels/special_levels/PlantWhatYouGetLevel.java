package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.utils.GameSession;

public class PlantWhatYouGetLevel extends Level {
    private int primarySun;

    @Override
    public void initSpecial(GameSession session) {
        handleBanSunflower();
        session.addSun(primarySun);
    }

    @Override
    public boolean isSkySunEnabled() {
        return false;
    }

    /** Sunflower selection isn't available in this level; the player lives off the initial sun pool only.*/
    public void handleBanSunflower() {
        if (getAvailablePlants() != null) {
            getAvailablePlants().removeIf(alias -> alias.toLowerCase().contains("sunflower"));
        }
    }

    /** Player plants freely (without recharge costs applying) until "start zombie waves"; then normal*/
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
