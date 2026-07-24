package model.pitches.obstacles;

import model.collections.plant.Plant;


public class IceBlock implements Obstacle {
    private final Plant frozenPlant;
    private int hp;

    public IceBlock(Plant frozenPlant, int hp) {
        this.frozenPlant = frozenPlant;
        this.hp = hp;
    }


    public boolean takeDamage(int amount) {
        if (hp <= 0) return false;
        hp -= amount;
        if (hp <= 0) {
            hp = 0;
            release();
            return true;
        }
        return false;
    }

    public void release() {
        if (frozenPlant != null) {
            frozenPlant.setState(Plant.PlantState.ACTIVE);
            frozenPlant.setChillLevel(0);
        }
    }

    public Plant getFrozenPlant() {
        return frozenPlant;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public boolean blocksPlanting() {
        return true;
    }

    @Override
    public String getName() {
        return "Ice Block";
    }
}
