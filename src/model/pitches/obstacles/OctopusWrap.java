package model.pitches.obstacles;

import model.collections.plant.Plant;

/**
 * Left behind by an Octopus zombie's grapple: pins a plant in place
 * (incapacitated) until enough damage is dealt to the wrap itself.
 */
public class OctopusWrap implements Obstacle {
    private final Plant wrappedPlant;
    private int hp;

    public OctopusWrap(Plant wrappedPlant, int hp) {
        this.wrappedPlant = wrappedPlant;
        this.hp = hp;
    }

    /**
     * Damages the wrap. Once its HP is exhausted, it frees the plant it's
     * holding. Returns true if this broke the wrap open.
     */
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
        if (wrappedPlant != null) {
            wrappedPlant.setState(Plant.PlantState.ACTIVE);
        }
    }

    public Plant getWrappedPlant() {
        return wrappedPlant;
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
        return "Octopus Wrap";
    }
}
