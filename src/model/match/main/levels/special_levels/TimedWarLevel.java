package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.match_mechanisms.Time;
import model.utils.GameSession;

public class TimedWarLevel extends Level {
    private Time timeLimit;
    private double configuredTimeLimitSeconds;
    private int zombiesToKill;
    private int zombiesKilledSoFar = 0;

    @Override
    public void initSpecial(GameSession session) {
        zombiesKilledSoFar = 0;
        if (timeLimit != null) timeLimit.reset(configuredTimeLimitSeconds);
    }

    /**
     * Call once per tick while the player is in this level.
     */
    public void tickTimer(double deltaSeconds) {
        if (timeLimit != null) {
            timeLimit.tick(deltaSeconds);
        }
    }

    /**
     * To be called whenever a zombie dies while this level is active.
     */
    public void recordZombieKill() {
        zombiesKilledSoFar++;
    }

    @Override
    public boolean checkLossCondition(GameSession session) {
        return timeLimit != null && timeLimit.isZero() && zombiesKilledSoFar < zombiesToKill;
    }

    @Override
    public boolean checkWinCondition(GameSession session) {
        return zombiesKilledSoFar >= zombiesToKill;
    }

    public int getZombiesKilledSoFar() { return zombiesKilledSoFar; }

    public Time getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Time timeLimit) {
        this.timeLimit = timeLimit;
        this.configuredTimeLimitSeconds = timeLimit == null ? 0 : timeLimit.getSecondsRemaining();
    }
    public int getZombiesToKill() { return zombiesToKill; }
    public void setZombiesToKill(int zombiesToKill) { this.zombiesToKill = zombiesToKill; }
}
