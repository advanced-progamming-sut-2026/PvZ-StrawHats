package model.projectile;

import model.match_mechanisms.vector.Position;

public class Projectile {
    public Position position;
    private boolean isGoingRight;

    private int damage;

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
    }

    private int speed;

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damgae) {
        this.damage = damgae;
    }

    public boolean isGoingRight() {
        return isGoingRight;
    }

    public void setGoingRight(boolean goingRight) {
        isGoingRight = goingRight;
    }
}
