package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.match_mechanisms.Time;

public class TimedWarLevel extends Level {
    private Time timeLimit;
    private int zombiesToKill;

    public Time getTimeLimit() { return timeLimit; }
    public void setTimeLimit(Time timeLimit) { this.timeLimit = timeLimit; }
    public int getZombiesToKill() { return zombiesToKill; }
    public void setZombiesToKill(int zombiesToKill) { this.zombiesToKill = zombiesToKill; }
}