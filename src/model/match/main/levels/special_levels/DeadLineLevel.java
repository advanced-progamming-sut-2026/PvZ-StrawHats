package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.match_mechanisms.vector.Position;

public class DeadLineLevel extends Level {
    private Position deadLine; // e.g., column x = 3

    public Position getDeadLine() { return deadLine; }
    public void setDeadLine(Position deadLine) { this.deadLine = deadLine; }
}