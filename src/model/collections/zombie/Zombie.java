package model.collections.zombie;

import model.collections.Item;
import model.collections.armour.Armour;
import model.match_mechanisms.Attack;
import model.match_mechanisms.sun.NormalSun;
import model.match_mechanisms.sun.RadioactivateSun;
import model.match_mechanisms.sun.SpecialSun;
import model.match_mechanisms.vector.Position;

import java.util.Random;

public abstract class Zombie extends Item implements Attack {
    private String name;
    private Armour armour;
    private boolean isFacingRight;
    private int speed;
    private boolean hasPlantFood;

    public Zombie(String name, Position position, int HP, boolean isFacingRight, Armour armour, int speed) {
        super(position, HP);
        this.name = name;
        this.armour = armour;
        this.isFacingRight = isFacingRight;
        this.speed = speed;
        hasPlantFood = chanceToHavePlantFood();
    }

    public void handleMovement(){};

    public void handleAbility(){};

    public boolean chanceToHavePlantFood() {
        Random random = new Random();
        int chance = random.nextInt(100);

        return chance < 5;
    }


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

    public boolean hasPlantFood() {
        return hasPlantFood;
    }

    public void setHasPlantFood(boolean hasPlantFood) {
        this.hasPlantFood = hasPlantFood;
    }

    public Armour getArmour() {
        return armour;
    }

    public void setArmour(Armour armour) {
        this.armour = armour;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void setFacingRight(boolean facingRight) {
        isFacingRight = facingRight;
    }

    @Override
    public void dealDamage(Item target) {

    }
}
