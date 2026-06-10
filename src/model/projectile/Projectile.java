package model.projectile;

import model.match_mechanisms.vector.MovementDirection;
import model.match_mechanisms.vector.Position;

public class Projectile {
    public Position position;
    public MovementDirection move;
    private int damgae;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
    }

    private int speed;

    public int getDamgae() {
        return damgae;
    }
}
