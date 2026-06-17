package model.collections.zombie;

import model.collections.Item;
import model.collections.armour.Armour;
import model.match_mechanisms.Attack;
import model.match_mechanisms.vector.MovementDirection;

public abstract class Zombie extends Item implements Attack {
    private String name;
    public MovementDirection move;
    public Armour armour;
    private int speed;

    public void handleMovement(){};

    public void handleAbility(){};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
