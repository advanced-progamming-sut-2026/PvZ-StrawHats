package model.collections.item;

import model.collections.Item;
import model.match_mechanisms.vector.Position;

public class GroundPlantFood extends Item {
    private boolean collected = false;

    public GroundPlantFood(Position position) {
        super(position, 1);
        setPosition(position);
    }

    @Override
    public void tick() {
    }

    public ItemType getItemType() {
        return ItemType.PLANT_FOOD;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
        setAlive(false);
    }
}
