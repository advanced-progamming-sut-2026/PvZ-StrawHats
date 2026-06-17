package model.match_mechanisms.sun;

import model.match_mechanisms.vector.Position;

public abstract class Sun {
    public Position position;

    private Position fallPosition;
    private double fallSpeed;
    private boolean isFallen;

    public void dispose(){};
    public abstract int getSunAmount();

    public boolean isFallen() {
        return isFallen;
    }

    public void setFallen(boolean fallen) {
        isFallen = fallen;
    }

    public Position getFallPosition() {
        return fallPosition;
    }

    public void setFallPosition(Position fallPosition) {
        this.fallPosition = fallPosition;
    }

    public double getFallSpeed() {
        return fallSpeed;
    }

    public void setFallSpeed(double fallSpeed) {
        this.fallSpeed = fallSpeed;
    }

    public void dropPositionEngine(){};
}
