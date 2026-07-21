package model.match.main.season.travellog.beach;

import model.match.main.season.Season;
import model.utils.GameSession;

public class Beach extends Season {
    public Beach() {
        super("Big Wave Beach");
    }

    @Override
    public boolean hasTide() { return true; }

    @Override
    public void onWaveStart(GameSession session, int waveIndex) {
        if (session == null || session.getLevel() == null) return;
        if (waveIndex % 2 == 0) {
            Flood.riselevel(session.getLevel());
        } else {
            Flood.falllevel(session.getLevel());
        }
    }
}