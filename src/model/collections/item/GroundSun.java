package model.collections.item;

import model.collections.Item;
import model.match_mechanisms.vector.Position;

public class GroundSun extends Item {
    private final int sunValue;
    private boolean collected = false;

    public GroundSun(Position position, int sunValue) {
        super(position, 1);
        this.sunValue = sunValue;
        setPosition(position);
    }

    @Override
    public void tick() {
    }

    public ItemType getItemType() {
        return ItemType.SUN;
    }

    public int getSunValue() {
        return sunValue;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
        setAlive(false);
    }
}
