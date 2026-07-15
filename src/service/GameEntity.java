package service;

import model.match_mechanisms.Time;
import model.match_mechanisms.vector.Position;

public class GameEntity extends Time {
    private Position position;
    private Position speed;

    protected boolean isAlive = true;

    public GameEntity(double seconds) {
        super(seconds);
    }

    public void tick() {

    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position target) {
        this.position = target;
    }

    public Position getSpeed() {
        return this.speed;
    }

    public void setSpeed(Position speed) {
        this.speed = speed;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

}
